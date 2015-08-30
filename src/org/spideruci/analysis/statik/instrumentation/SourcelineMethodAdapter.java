package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.trace.EventBuilder.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.DeclPropNames;
import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.InsnPropNames;
import org.spideruci.analysis.trace.TraceEvent;

public class SourcelineMethodAdapter extends AdviceAdapter {
  
  private TraceEvent methodDecl;
  private boolean shouldInstrument;
  
  public SourcelineMethodAdapter(TraceEvent methodDecl, int access, String name, 
      String desc, MethodVisitor mv) {
    super(Opcodes.ASM4, mv, access, name, desc);
    this.methodDecl = methodDecl;
    this.shouldInstrument = false;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  protected void onMethodEnter() {
    int lineNum = Profiler.latestLineNumber;
    int access = Integer.parseInt(methodDecl.getDeclAccess());
    int opcode = ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) ? -3 : -2;
    if(methodDecl.getDeclAccess().contains("<init>")) {
      opcode = -5;
    }
    
    String instructionLog =
        buildInstructionLog(-1, EventType.$enter$, opcode, methodDecl.getId());
    //instruction log the arguments for the call
    StringBuffer callbackDesc = new StringBuffer();
    
    mv.visitLdcInsn(methodDecl.getDeclOwner());
    callbackDesc.append("(").append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodDecl.getDeclName());
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(instructionLog); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId(); 
    callbackDesc.append(Deputy.STRING_DESC).append(")V");
    
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Deputy.PROFILER_NAME,
        Deputy.PROFILER_METHODENTER, callbackDesc.toString());

    Profiler.latestLineNumber = lineNum;
    shouldInstrument = true;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  protected void onMethodExit(int opcode) {
    int lineNum = Profiler.latestLineNumber;
    EventType eventType = 
        (opcode == Opcodes.ATHROW) ? EventType.$athrow$ : EventType.$return$; 
    String instructionLog =
        buildInstructionLog(lineNum, eventType, opcode, methodDecl.getId());
    
    StringBuffer callbackDesc = new StringBuffer();
    
    mv.visitLdcInsn(methodDecl.getDeclOwner());
    callbackDesc.append("(").append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodDecl.getDeclName());
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC).append(")V");
    
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_METHODEXIT, 
                       callbackDesc.toString());

    Profiler.latestLineNumber = lineNum;
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitLineNumber(int line, Label start) {
    Profiler.latestLineNumber = line;
    
    if(!shouldInstrument) {
      super.visitLineNumber(line, start); //make the actual call.
      return;
    }
    
    super.visitLineNumber(line, start); //make the actual call.
    String instructionLog =
        buildInstructionLog(line, EventType.$line$, -1, methodDecl.getId());
    
    StringBuffer callbackDesc = new StringBuffer();
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append("(").append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC).append(")V");
    
    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_LINENUMER, 
                       callbackDesc.toString());
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    int lineNum = Profiler.latestLineNumber;
    if(!shouldInstrument) {
      super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
      return;
    }
    
    StringBuffer callbackDesc = new StringBuffer();
    callbackDesc.append("(");
    
    String methodName = owner + "/" + name + desc;
    String instructionLog = buildInstructionLog(lineNum, EventType.$invoke$, 
        opcode, methodDecl.getId(), methodName);
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC);
    
    callbackDesc.append(")V");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_INVOKE, 
                       callbackDesc.toString());
    Profiler.latestLineNumber = lineNum;
    super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
  }
  
  @Override
  public void visitMaxs(int MaxStack, int maxLocals) {
    super.visitMaxs(MaxStack, maxLocals);
  }
  
    @SuppressWarnings("deprecation")
    private void loadRecieverObjectId() {
      int access = Integer.parseInt(methodDecl.getDeclAccess());
      if((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
        mv.visitLdcInsn("C");
      } else {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Deputy.OBJECT_DESC));
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                           Deputy.PROFILER_NAME, 
                           Deputy.PROFILER_GETHASH, 
                           Deputy.PROFILER_GETHASH_DESC);
      }
    }
    


}
