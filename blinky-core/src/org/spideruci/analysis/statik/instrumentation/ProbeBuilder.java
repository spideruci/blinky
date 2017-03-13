package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.statik.instrumentation.Config.STRING_DESC;
import static org.spideruci.analysis.statik.instrumentation.Config.OBJECT_DESC;
import static org.spideruci.analysis.statik.instrumentation.Config.PROFILER_NAME;
import static org.spideruci.analysis.dynamic.Profiler.GETHASH;
import static org.spideruci.analysis.dynamic.Profiler.GETHASH_DESC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.util.Buffer;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.util.MyAssert;

/**
 * This class presents an abstraction to create bytecode instruction sequences
 * pertaining to the instrumentation probes that are meant to call the Profiler
 * code when such probes are executed with the rest of the program during 
 * execution.
 * @author vpalepu
 *
 */
public class ProbeBuilder implements Opcodes {
  private final StringBuffer callbackDesc;
  private final MethodVisitor mv;
  
  public static ProbeBuilder start(MethodVisitor mv) {
    ProbeBuilder callBack = new ProbeBuilder(mv);
    callBack.callbackDesc.append("(");
    return callBack;
  }
  
  private ProbeBuilder(MethodVisitor mv) {
    this.callbackDesc = new StringBuffer();
    this.mv = mv;
  }
  
  public ProbeBuilder setStaticBooelanField(boolean value, String fieldName, String fieldOwner) {
    this.mv.visitLdcInsn(value);
    this.mv.visitFieldInsn(Opcodes.PUTSTATIC, fieldOwner, fieldName, Config.BOOLEAN_TYPEDESC);
    return this;
  }
  
  public ProbeBuilder passArg(String arg) {
    this.mv.visitLdcInsn(arg);
    this.callbackDesc.append(Config.STRING_DESC);
    return this;
  }
  
  public ProbeBuilder passArg(boolean b) {
    this.mv.visitLdcInsn(b);
    this.callbackDesc.append(Config.BOOLEAN_TYPEDESC);
    return this;
  }
  
  public ProbeBuilder passArg(int arg) {
    this.mv.visitLdcInsn(arg);
    this.callbackDesc.append(Config.INT_TYPEDESC);
    return this;
  }
  
  public ProbeBuilder passArg(EventType type) {
    this.mv.visitLdcInsn(type);
    this.callbackDesc.append(Config.EVENT_TYPE_DESC);
    return this;
  }
  
  public ProbeBuilder passThis(String methodAccess) {
    int access = Integer.parseInt(methodAccess);
    if((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
      mv.visitLdcInsn("C");
    } else {
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Config.OBJECT_DESC));
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                         Config.PROFILER_NAME, 
                         Profiler.GETHASH, 
                         Profiler.GETHASH_DESC, false);
    }
    this.callbackDesc.append(Config.STRING_DESC);
    return this;
  }
  
  public ProbeBuilder passRef(int var) {
    mv.visitVarInsn(Opcodes.ALOAD, var);
    mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Config.OBJECT_DESC));
    this.callbackDesc.append(Config.OBJECT_DESC);
    return this;
  }
  
  public ProbeBuilder passNakedRef(int var) {
    mv.visitVarInsn(Opcodes.ALOAD, var);
    this.callbackDesc.append(Config.OBJECT_DESC);
    return this;
  }
  
  public ProbeBuilder setupGetInsnStackArgs(int opcode, String owner) {
    MyAssert.assertThat(GETFIELD == opcode || GETSTATIC == opcode);
    
    if(opcode == GETFIELD) { // ref
      mv.visitInsn(DUP); // ref, ref
    }
    
    return this;
  }
  
  private boolean isDescRef(String desc) {
    switch(desc) {
    case "B":
    case "C":
    case "D":
    case "F":
    case "I":
    case "J":
    case "S":
    case "Z":
      return false;
    default:
      return true;
    }
  }
  
  private boolean isDescWide(String desc) {
    switch(desc) {
    case "D":
    case "J":
      return true;
    default:
      return false;
    }
  }
  
  public ProbeBuilder passGetInsnStackArgs(int opcode, String desc, String owner) {
    final boolean isValueWide = isDescWide(desc);
    final boolean isValueRef = isDescRef(desc);
    
//    if(opcode == GETSTATIC || !isValueWide) {
//      unsafeGet(opcode, desc, owner);
//    } else {
//      safeGet(opcode, desc, owner);
//    }
    
    unsafeGet(opcode, desc, owner);
    
    this.callbackDesc.append(STRING_DESC);
    this.callbackDesc.append(STRING_DESC);
    return this;
  }
  
  public ProbeBuilder passPutInsnStackArgs(int opcode, String desc, String owner) {
    MyAssert.assertThat(PUTFIELD == opcode || PUTSTATIC == opcode);
    
    final boolean isWide = isDescWide(desc);
    final boolean isRef = isDescRef(desc);
    
    if(opcode == Opcodes.PUTFIELD) { //visiting non-static field dereferences.

      if(isRef) { // ref, val
        mv.visitInsn(DUP2); // ref, val, ref, val
        visitLoadHash(); // ref, val, ref, val#
        mv.visitInsn(SWAP); // refm val, val#, ref
        visitLoadHash(); // ref, val, val#, ref#
        mv.visitInsn(SWAP); // ref, val, ref#, val#
      } else {
        if(isWide) { // ref, val1, val2
//          mv.visitLdcInsn("0");
//          mv.visitLdcInsn("0");
          mv.visitInsn(DUP2_X1); // val1, val2, ref, val1, val2
          mv.visitInsn(POP2); // val1, val2, ref
          mv.visitInsn(DUP_X2); // ref, val1, val2, ref
          visitLoadHash(); // ref, val1, val2, ref#
          mv.visitLdcInsn("0"); // ref, val, ref#, "0"
        } else { // ref, val
          mv.visitInsn(DUP2); // ref, val, ref, val
          mv.visitInsn(POP); // ref, val, ref
//          visitLoadHash(); 
          mv.visitMethodInsn(INVOKESTATIC, PROFILER_NAME, GETHASH, GETHASH_DESC, false); // ref, val, ref#
//          mv.visitLdcInsn("0");
          mv.visitLdcInsn("0"); // ref, val, ref#, "0"
        }
      }
    } else { // visiting PUTSTATIC
      if(isRef) { // refval
        mv.visitInsn(DUP); // refval, refval
        visitLoadHash(); // refval, val#
      } else { // primval
        mv.visitLdcInsn("0"); // primval, "0"
      }
      mv.visitLdcInsn(owner); // val, val#, "owner"
      mv.visitInsn(SWAP); // // val, "owner", val#
    }
    
    this.callbackDesc.append(STRING_DESC);
    this.callbackDesc.append(STRING_DESC);
    
    return this;
  }
  
  private void visitLoadStringValue(String desc) {
    switch(desc) {
    case "I":
    case "B":
    case "S":
      desc = "I";
      break;
    }
    
    if(desc.startsWith("L") || desc.startsWith("[")) {
      visitLoadHash();
    } else {
      if(desc.equals("J") || desc.equals("D")) {
        mv.visitInsn(Opcodes.POP2);
      } else {
        mv.visitInsn(Opcodes.POP);
      }
      
      mv.visitLdcInsn("0");
//      String methodDesc = "(" + desc + ")" + STRING_DESC;
//      mv.visitMethodInsn(INVOKESTATIC, STRING_DESC, "valueOf", methodDesc, false);
    }
  }
  
  private void visitLoadHash() {
    mv.visitTypeInsn(CHECKCAST, OBJECT_DESC);
    mv.visitMethodInsn(INVOKESTATIC, PROFILER_NAME, GETHASH, GETHASH_DESC, false);
  }
  
  public ProbeBuilder passArrayLoadStackArgs(int opcode) {
    MyAssert.assertThat(Opcodes.IALOAD <= opcode && opcode <= Opcodes.SALOAD,
        "not an array load.");
    mv.visitInsn(Opcodes.DUP2);
    this.callbackDesc.append(Config.OBJECT_DESC);
    this.callbackDesc.append(Config.INT_TYPEDESC);
    return this;
  }
  
  public ProbeBuilder passArrayStoreStackArgs(int opcode) {
    MyAssert.assertThat(Opcodes.IASTORE <= opcode && opcode <= Opcodes.SASTORE,
        "not an array store.");
    
    boolean isWide = opcode == Opcodes.LASTORE || opcode == Opcodes.DASTORE;
    
    if(!isWide) { // wide padding
      mv.visitInsn(Opcodes.ACONST_NULL);
    }
    
    // wide swap val-val and ref-idx
    mv.visitInsn(Opcodes.DUP2_X2);
    mv.visitInsn(Opcodes.POP2);
    
    // duplicate and place ref-idx
    mv.visitInsn(Opcodes.DUP2_X2);
    mv.visitInsn(Opcodes.DUP2_X2);
    mv.visitInsn(Opcodes.POP2);
    
    if(!isWide) { // remove wide padding
      mv.visitInsn(Opcodes.POP);
      mv.visitInsn(Opcodes.DUP_X2);
    } else {
      mv.visitInsn(Opcodes.DUP2_X2);
    }
    
    switch(opcode) {
    case Opcodes.AASTORE:
      mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
    case Opcodes.BASTORE:
    case Opcodes.SASTORE:
    case Opcodes.IASTORE:
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
      break;
    case Opcodes.FASTORE:
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false);
      break;
    case Opcodes.CASTORE:
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false);
      break;

    case Opcodes.LASTORE:
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
      break;
    case Opcodes.DASTORE:
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
      break;
    default:
      throw new RuntimeException("unexpected opcode: "  + opcode);
    }
    
    this.callbackDesc.append(Config.OBJECT_DESC);
    this.callbackDesc.append(Config.INT_TYPEDESC);
    this.callbackDesc.append(Config.STRING_DESC);
    return this;
  }
  
  public ProbeBuilder appendDesc(final String typeDesc) {
    this.callbackDesc.append(typeDesc);
    return this;
  }
  
  public ProbeBuilder passNewWithDefaultCtor(final String typeDesc) {
    final Buffer typeNameBuffer = new Buffer();
    for(int i = 0; i < typeDesc.length(); i += 1) {
      char ch = typeDesc.charAt(i);
      if(ch == '/') {
        typeNameBuffer.append('.');
      } else {
        typeNameBuffer.append(ch);
      }
    }
    
    final String typeName = typeNameBuffer.toString();
    this.mv.visitTypeInsn(Opcodes.NEW, typeName);
    
    return this;
  }
  
  public void build(String callBackName) {
//    this.build(callBackName, OfflineInstrumenter.isActive ? Config.PROFILER_B_NAME : Config.PROFILER_NAME);
    this.build(callBackName, Config.PROFILER_NAME);
  }
  
  public void build(String callBackName, String classbackClassName) {
    build(callBackName, classbackClassName, "V");
  }
  
  public void build(String callBackName, String classbackClassName, String returnType) {
    this.callbackDesc.append(")" + returnType);
    this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       classbackClassName, 
                       callBackName, 
                       callbackDesc.toString(),
                       false);
  }
  
  public StringBuffer getCallbackDesc() {
    return this.callbackDesc;
  }
  
  private void safeGet(int opcode, String desc, String owner) {
    final boolean isValueWide = desc.equals("D") || desc.equals("J");
    final boolean isValueRef = desc.equals("[") || desc.equals("L");
    
    if(opcode == GETFIELD) {
      if(isValueWide) { // ref, val, val
        mv.visitInsn(DUP2_X1); // val, val, ref, val, val
        mv.visitInsn(POP2); // val, val, ref
      } else { // ref, val
        mv.visitInsn(SWAP); // val, ref
      }
      mv.visitInsn(POP);
    }
    
    // start --> val; end --> val, ref#={0|owner}, val#
    
    if(isValueRef) { // value
      mv.visitInsn(DUP); // value, value
      visitLoadHash(); // value, value#
    } else {
      mv.visitLdcInsn("0"); // value, value#{"0"}
    }
    
    if(opcode == GETFIELD) {
      mv.visitLdcInsn("0"); // value, value#, ref#{0}
    } else {
      mv.visitLdcInsn(owner); // value, value#, ref#{owner}
    }
    
    mv.visitInsn(SWAP); // value, ref#{0|owner}, value#
  }
  
  private void unsafeGet(int opcode, String desc, String owner) {
    final boolean isValueWide = desc.equals("D") || desc.equals("J");
    final boolean isValueRef = desc.equals("[") || desc.equals("L");
    
    // start --> ref?, val [, val]
    if(opcode == GETSTATIC) {
      // start --> val [, val]; end --> val, ref#{owner}, val#{#|0}
      if(isValueRef) { // val
        mv.visitInsn(DUP); // val, val
        visitLoadHash(); // val, val#
      } else {
        mv.visitLdcInsn("0"); // val, val#{0}
      }
      
      mv.visitLdcInsn(owner); // val, val#{0|#}, ref#{owner}
      mv.visitInsn(SWAP); // val, ref#{owner}, val#{0}
    } else {
      // start --> ref, val [, val]; end --> val [, val], ref#, val#{0|#}
      
      if(isValueWide) { // ref, val, val
        mv.visitInsn(DUP2_X1); // val, val, ref, val, val
        mv.visitInsn(POP2); // val, val, ref
        mv.visitLdcInsn("0"); // val, val, ref, val#{0}
      } else { // ref, val
        if(isValueRef) {
          mv.visitInsn(DUP_X1); // val, ref, val
          visitLoadHash(); // val, ref, val#
        } else {
          mv.visitInsn(DUP_X1); // val, ref, val
          mv.visitInsn(POP); // val, ref
          mv.visitLdcInsn("0"); // val, ref, val#{0}
        }
      }
      
      // val [, val], ref, val#{0|#}
      mv.visitInsn(SWAP); // val [, val], val#{0|#}, ref
      visitLoadHash(); // val [, val], val#{0|#}, ref#
      mv.visitInsn(SWAP); // val [, val], ref#, val#{0|#}
    }
  }
}
