package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.trace.EventBuilder.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.EventType;
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
  
  @Override
  protected void onMethodEnter() {
    int lineNum = Profiler.latestLineNumber;
    int access = Integer.parseInt(methodDecl.getDeclAccess());
    int opcode = ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) ? -3 : -2;
    if(methodDecl.getDeclName().contains("<init>")) {
      opcode = -5;
    }
    
    String instructionLog =
        buildInstructionLog(-1, EventType.$enter$, opcode, methodDecl.getId());
    
    ProfilerCallBack.start(mv)
    .passArg(methodDecl.getDeclOwner())
    .passArg(methodDecl.getDeclName())
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Deputy.PROFILER_METHODENTER);
    
    Profiler.latestLineNumber = lineNum;
    shouldInstrument = true;
  }
  
  @Override
  protected void onMethodExit(int opcode) {
    int lineNum = Profiler.latestLineNumber;
    EventType eventType = 
        (opcode == Opcodes.ATHROW) ? EventType.$athrow$ : EventType.$return$; 
    String instructionLog =
        buildInstructionLog(lineNum, eventType, opcode, methodDecl.getId());
    
    ProfilerCallBack.start(mv)
    .passArg(methodDecl.getDeclOwner())
    .passArg(methodDecl.getDeclName())
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Deputy.PROFILER_METHODEXIT);
    
    Profiler.latestLineNumber = lineNum;
  }
  
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
    
    ProfilerCallBack.start(mv)
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Deputy.PROFILER_LINENUMER);
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    int lineNum = Profiler.latestLineNumber;
    if(!shouldInstrument) {
      super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
      return;
    }
    
    String methodName = owner + "/" + name + desc;
    String instructionLog = buildInstructionLog(lineNum, EventType.$invoke$, 
        opcode, methodDecl.getId(), methodName);
    
    ProfilerCallBack.start(mv)
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Deputy.PROFILER_INVOKE);

    Profiler.latestLineNumber = lineNum;
    super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
  }
  
  @Override
  public void visitMaxs(int MaxStack, int maxLocals) {
    super.visitMaxs(MaxStack, maxLocals);
  }
}