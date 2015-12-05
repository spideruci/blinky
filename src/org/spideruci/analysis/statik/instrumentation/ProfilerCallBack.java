package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.EventType;

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
