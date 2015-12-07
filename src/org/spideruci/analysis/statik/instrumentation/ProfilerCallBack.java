package org.spideruci.analysis.statik.instrumentation;

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
public class ProfilerCallBack {
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
  
  @SuppressWarnings("deprecation")
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
                         Profiler.GETHASH_DESC);
    }
    this.callbackDesc.append(Deputy.STRING_DESC);
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
  
  @SuppressWarnings("deprecation")
  public void build(String callBackName) {
    this.callbackDesc.append(")V");
    this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       callBackName, 
                       callbackDesc.toString());
  }
  
  public StringBuffer getCallbackDesc() {
    return this.callbackDesc;
  }
}
