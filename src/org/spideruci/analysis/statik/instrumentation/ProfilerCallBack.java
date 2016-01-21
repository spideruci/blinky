package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.statik.instrumentation.Deputy.STRING_DESC;
import static org.spideruci.analysis.statik.instrumentation.Deputy.OBJECT_DESC;
import static org.spideruci.analysis.statik.instrumentation.Deputy.PROFILER_NAME;
import static org.spideruci.analysis.dynamic.Profiler.GETHASH;
import static org.spideruci.analysis.dynamic.Profiler.GETHASH_DESC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
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
public class ProfilerCallBack implements Opcodes {
  private final StringBuffer callbackDesc;
  private final MethodVisitor mv;
  
  public static ProfilerCallBack start(MethodVisitor mv) {
    ProfilerCallBack callBack = new ProfilerCallBack(mv);
    callBack.callbackDesc.append("(");
    return callBack;
  }
  
  private ProfilerCallBack(MethodVisitor mv) {
    this.callbackDesc = new StringBuffer();
    this.mv = mv;
  }
  
  public ProfilerCallBack passArg(String arg) {
    this.mv.visitLdcInsn(arg);
    this.callbackDesc.append(Deputy.STRING_DESC);
    return this;
  }
  
  public ProfilerCallBack passArg(EventType type) {
    this.mv.visitLdcInsn(type);
    this.callbackDesc.append(Deputy.EVENT_TYPE_DESC);
    return this;
  }
  
  public ProfilerCallBack passThis(String methodAccess) {
    int access = Integer.parseInt(methodAccess);
    if((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
      mv.visitLdcInsn("C");
    } else {
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Deputy.OBJECT_DESC));
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                         Deputy.PROFILER_NAME, 
                         Profiler.GETHASH, 
                         Profiler.GETHASH_DESC, false);
    }
    this.callbackDesc.append(Deputy.STRING_DESC);
    return this;
  }
  
  public ProfilerCallBack passRef(int var) {
    mv.visitVarInsn(Opcodes.ALOAD, var);
    mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Deputy.OBJECT_DESC));
    this.callbackDesc.append(Deputy.OBJECT_DESC);
    return this;
  }
  
  public ProfilerCallBack setupGetInsnStackArgs(int opcode, String owner) {
    MyAssert.assertThat(GETFIELD == opcode || GETSTATIC == opcode);
    
    if(opcode == GETSTATIC) {
      mv.visitLdcInsn(owner);
    } else {
      mv.visitInsn(DUP);
      visitLoadHash();
      mv.visitInsn(SWAP);
    }
    
    this.callbackDesc.append(STRING_DESC);
    return this;
  }
  
  public ProfilerCallBack passGetInsnStackArgs(int opcode, String desc) {
    boolean isWide = desc.equals("D") || desc.equals("J");
    
    if(isWide) {
      mv.visitInsn(DUP2_X1);
    } else {
      mv.visitInsn(DUP_X1);
    }
    
    visitLoadStringValue(desc);
    this.callbackDesc.append(STRING_DESC);
    
    return this;
  }
  
  public ProfilerCallBack passPutInsnStackArgs(int opcode, String desc, String owner) {
    MyAssert.assertThat(PUTFIELD == opcode || PUTSTATIC == opcode);
    
    boolean isWide = desc.equals("D") || desc.equals("J");
    
    if(opcode == Opcodes.PUTFIELD) { //visiting non-static field dereferences.
      if(isWide) { // ref, val1, val2
        mv.visitInsn(DUP2_X1); // val1, val2, ref, val1, val2
        mv.visitInsn(POP2); // val1, val2, ref
        mv.visitInsn(DUP_X2); // ref, val1, val2, ref
        mv.visitInsn(DUP_X2); //  ref, ref, val1, val2, ref
        mv.visitInsn(POP); //  ref, ref, val1, val2
        mv.visitInsn(DUP2_X1); //  ref, val1, val2, ref, val1, val2
      }
      else { // ref, val
        mv.visitInsn(Opcodes.DUP2); // ref, val, ref, val
      }
      
      visitLoadStringValue(desc);
      mv.visitInsn(SWAP); // ref, val, String.valueOf(val) ref
      visitLoadHash(); // ref, val, String.valueOf(val) string_hash(ref)
      mv.visitInsn(SWAP); // ref, val, string_hash(ref), valId
    } else {
      if(isWide) {
        mv.visitInsn(DUP2);
      } else {
        mv.visitInsn(DUP);
      }
      visitLoadStringValue(desc);
      mv.visitLdcInsn(owner);
      mv.visitInsn(SWAP);
    }
    
    this.callbackDesc.append(STRING_DESC);
    this.callbackDesc.append(STRING_DESC);
    
    return this;
  }
  
  public ProfilerCallBack passArrayLoadStackArgs(int opcode) {
    MyAssert.assertThat(Opcodes.IALOAD <= opcode && opcode <= Opcodes.SALOAD,
        "not an array load.");
    mv.visitInsn(Opcodes.DUP2);
    this.callbackDesc.append(Deputy.OBJECT_DESC);
    this.callbackDesc.append(Deputy.INT_TYPEDESC);
    return this;
  }
  
  public ProfilerCallBack passArrayStoreStackArgs(int opcode) {
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
    
    this.callbackDesc.append(Deputy.OBJECT_DESC);
    this.callbackDesc.append(Deputy.INT_TYPEDESC);
    this.callbackDesc.append(Deputy.STRING_DESC);
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
        String methodDesc = "(" + desc + ")" + STRING_DESC;
        mv.visitMethodInsn(INVOKESTATIC, STRING_DESC, "valueOf", methodDesc, false);
      }
    }
    
    private void visitLoadHash() {
      mv.visitTypeInsn(CHECKCAST, OBJECT_DESC);
      mv.visitMethodInsn(INVOKESTATIC, PROFILER_NAME, GETHASH, GETHASH_DESC, false);
    }
  
  
  public void build(String callBackName) {
    this.build(callBackName, Deputy.PROFILER_NAME);
  }
  
  @SuppressWarnings("deprecation")
  public void build(String callBackName, String classbackClassName) {
    this.callbackDesc.append(")V");
    this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       classbackClassName, 
                       callBackName, 
                       callbackDesc.toString());
  }
  
  public StringBuffer getCallbackDesc() {
    return this.callbackDesc;
  }
}
