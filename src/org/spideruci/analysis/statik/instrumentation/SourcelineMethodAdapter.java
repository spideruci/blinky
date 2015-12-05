package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.trace.EventBuilder.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitcher;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitchListerner;
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
    .build(Profiler.PROFILER_METHODENTER);
    
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
    .build(Profiler.METHODEXIT);
    
    Profiler.latestLineNumber = lineNum;
  }
  
  @Override
  public void visitLineNumber(int line, Label start) {
    super.visitLineNumber(line, start); //make the actual call.
    
    if(shouldInstrument && Profiler.logSourceLineNumber) {
      Profiler.latestLineNumber = line;
      
      String instructionLog =
          buildInstructionLog(line, EventType.$line$, -1, methodDecl.getId());
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.LINENUMER);
    }
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    if(shouldInstrument && Profiler.logMethodInvoke) {
      final int lineNum = Profiler.latestLineNumber;
      
      String methodName = owner + "/" + name + desc;
      String instructionLog = buildInstructionLog(lineNum, EventType.$invoke$, 
          opcode, methodDecl.getId(), methodName);
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.INVOKE);
      
      Profiler.latestLineNumber = lineNum;
    }

    super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
  }
  
  @Override
  public void visitVarInsn(int opcode, int operand) {
    if(shouldInstrument && Profiler.logVar) {
      final int lineNum = Profiler.latestLineNumber;
      
      String instructionLog = buildInstructionLog(lineNum, EventType.$var$, 
          opcode, methodDecl.getId(), String.valueOf(operand));

      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.VAR);

      Profiler.latestLineNumber = lineNum;
    }
    super.visitVarInsn(opcode, operand); //make the actual call.
  }
  
  @Override
  public void visitInsn(int opcode) {
    if(shouldInstrument && Profiler.logZero) {
      final int lineNum = Profiler.latestLineNumber;
      
      ZeroOperandSwitchListerner listerner = 
          ZeroOperandSwitchListerner.create(mv, lineNum, methodDecl);
      ZeroOperandSwitcher switcher = new ZeroOperandSwitcher(listerner);
      switcher.svitch(opcode);

      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitInsn(opcode); //make the actual call.
  }
  
  @Override
  public void visitJumpInsn(int opcode, Label label) {
    if(shouldInstrument && Profiler.logJump) {
      final int lineNum = Profiler.latestLineNumber;
      
      String instructionLog = buildInstructionLog(lineNum, EventType.$jump$, 
          opcode, methodDecl.getId());
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.JUMP);
      
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitJumpInsn(opcode, label); //make the actual call.
  }
  
  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if(shouldInstrument && Profiler.logField) {
      final int lineNum = Profiler.latestLineNumber;
      
      String fieldName = owner + "/" + name + desc;
      String instructionLog = buildInstructionLog(lineNum, EventType.$field$, 
          opcode, methodDecl.getId(), fieldName);

      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.FIELD);

      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitFieldInsn(opcode, owner, name, desc); //make the actual call.
  }
  
  @Override
  public void visitIincInsn(int var, int increment) {
    if(shouldInstrument && Profiler.logVar) {
      final int lineNum = Profiler.latestLineNumber;
      
      String instructionLog = buildInstructionLog(lineNum, EventType.$iinc$, 
          Opcodes.IINC, methodDecl.getId(), String.valueOf(increment));
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.IINC);
      
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitIincInsn(var, increment);
  }
  
  @Override
  public void visitIntInsn(int opcode, int operand) {
    super.visitIntInsn(opcode, operand);
  }
  
  @Override
  public void visitTypeInsn(int opcode, String type) {
    super.visitTypeInsn(opcode, type);
  }
  
  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }
  
  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }
  
  @Override
  public void visitLdcInsn(Object cst) {
    super.visitLdcInsn(cst);
  }
  
  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {
    super.visitMultiANewArrayInsn(desc, dims);
  }
  
  @Override
  public void visitTryCatchBlock(Label start, Label end, 
      Label handler, String type) {
    super.visitTryCatchBlock(start, end, handler, type);
  }
  
  @Override
  public void visitLocalVariable(String name, String desc, String signature, 
      Label start, Label end, int index) {
    super.visitLocalVariable(name, desc, signature, start, end, index);
  }
  
  @Override
  public void visitMaxs(int MaxStack, int maxLocals) {
    super.visitMaxs(MaxStack, maxLocals);
  }
}