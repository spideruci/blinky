package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.TraceEvent;

public class GuardedMethodAdapter extends AdviceAdapter implements Opcodes {
  
  private TraceEvent methodDecl;
  private boolean shouldInstrument;
  
  public GuardedMethodAdapter(TraceEvent methodDecl, int access, String name, 
      String desc, MethodVisitor mv) {
    super(Opcodes.ASM4, mv, access, name, desc);
    this.methodDecl = methodDecl;
    this.shouldInstrument = false;
  }
  
  @Override
  protected void onMethodEnter() {
    shouldInstrument = true;
    guard();
  }
  
  @Override
  public void visitInsn(int opcode) {
    if(shouldInstrument) {
      switch(opcode) {
      case RETURN:
      case IRETURN:
      case FRETURN:
      case DRETURN:
      case LRETURN:
      case ARETURN:
      case ATHROW:
        ProbeBuilder.start(mv).passArg(false).build(Profiler.REGUARD);
      default: // do nothing
      }
    }
    
    super.visitInsn(opcode); // make the actual call.
  }
  
  @Override
  public void visitVarInsn(int opcode, int var) {
    if(shouldInstrument) {
      switch(opcode) {
      case ASTORE:
      case RET:
        guard();
      default: // do nothing
      }
    }
    
    super.visitVarInsn(opcode, var);
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    if(!shouldInstrument) {
      super.visitMethodInsn(opcode, owner, name, desc); // make the actual call.
      return;
    }
    
    super.visitMethodInsn(opcode, owner, name, desc); // make the actual call.
    guard();
  }
  
  private void guard() {
    ProbeBuilder.start(mv).build(Profiler.GUARD, Deputy.PROFILER_NAME, "Z");
    mv.visitInsn(POP);
  }
}
