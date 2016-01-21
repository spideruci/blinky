package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.trace.EventBuilder.*;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.BUFFER_TYPE_NAME;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.BUFFER_TYPE_NAME_SYSID;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.CLEAR_BUFFER;
import static org.spideruci.analysis.statik.instrumentation.Deputy.RUNTIME_TYPE_PROFILER_NAME;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitcher;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitchListerner;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.util.MyAssert;
import org.spideruci.analysis.util.caryatid.Helper;

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
    final boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    int opcode = isStatic ? -3 : -2;
    if(methodDecl.getDeclName().contains("<init>")) {
      opcode = -5;
    }
    
    if(Profiler.logEnterRuntimeSign) {
      ProfilerCallBack.start(mv)
      .build(CLEAR_BUFFER, RUNTIME_TYPE_PROFILER_NAME);
      
      final String methodName = methodDecl.getDeclName();
      final String[] argTypes = Helper.getArgTypeSplitFromMethod(methodName);
      for(int i = 0; i < argTypes.length; i += 1) {
        String argType = argTypes[i];
        int varIndex = i + (isStatic? 0 : 1);
        if(argType.startsWith("L")) {
          ProfilerCallBack.start(mv)
          .passRef(varIndex)
          .passArg(argType)
          .build(BUFFER_TYPE_NAME_SYSID, RUNTIME_TYPE_PROFILER_NAME);
        } else if(argType.startsWith("[")) {
          ProfilerCallBack.start(mv)
          .passArg(argType)
          .passRef(varIndex)
          .build(BUFFER_TYPE_NAME_SYSID, RUNTIME_TYPE_PROFILER_NAME);
        } else {
          ProfilerCallBack.start(mv)
          .passArg(argType)
          .build(BUFFER_TYPE_NAME, RUNTIME_TYPE_PROFILER_NAME);
        }
      }
    }
    
    String instructionLog =
        buildInstructionLog(-1, EventType.$enter$, opcode, methodDecl.getId());
    
    ProfilerCallBack.start(mv)
    .passArg(methodDecl.getDeclOwner())
    .passArg(methodDecl.getDeclName())
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Profiler.METHODENTER);
    
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
      
      if(Profiler.logInvokeRuntimeSign) {
        InvokeSignCallBack.buildArgProfileProbe(mv, opcode, owner, name, desc);
      }
      
      String instructionLog = buildInstructionLog(lineNum, EventType.$invoke$, 
          opcode, methodDecl.getId(), owner, name, desc);
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.INVOKE);
      
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
    
    if(shouldInstrument && Profiler.logMethodInvoke) {
      final int lineNum = Profiler.latestLineNumber;
      
      String instructionLog = buildInstructionLog(lineNum, EventType.$complete$, 
          -4, methodDecl.getId(), owner, name, desc);
      
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.COMPLETE);
      
      Profiler.latestLineNumber = lineNum;
    }
  }
  
  @Override
  public void visitVarInsn(int opcode, int operand) { // TODO $ret$
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
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    ProfilerCallBack getProbeCallback = null;
    String instructionLog = null;
    if(shouldInstrument && Profiler.logField) {
      final int lineNum = Profiler.latestLineNumber;
      
      instructionLog = buildInstructionLog(lineNum, EventType.$field$, 
          opcode, methodDecl.getId(), owner, name, desc);
      
      if(opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC) {
        ProfilerCallBack.start(mv)
        .passPutInsnStackArgs(opcode, desc, owner)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.FIELD);
      } else {
        getProbeCallback = ProfilerCallBack.start(mv)
        .setupGetInsnStackArgs(opcode, owner);
      }

      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitFieldInsn(opcode, owner, name, desc); //make the actual call.
    
    if(shouldInstrument && Profiler.logField 
        && (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC)) {
      MyAssert.assertThat(getProbeCallback != null && instructionLog != null);
      
      getProbeCallback.passGetInsnStackArgs(opcode, desc)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.FIELD);
    }
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
    
    super.visitJumpInsn(opcode, label);
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
    switch(opcode) {
    case BIPUSH:
    case SIPUSH:
      onConstantInsn(opcode, String.valueOf(operand));
      break;
    case NEWARRAY:
      onTypeInsn(opcode, Deputy.primitiveCode2String(operand), 1);
      break;
    default:
      throw new RuntimeException("unexpected opcode: " + opcode);
    }
    
    super.visitIntInsn(opcode, operand);
  }
  
  @Override
  public void visitLdcInsn(Object cst) {
    
    String metadata;
    if(cst instanceof Long || cst instanceof Double) {
      metadata = Deputy.LDC_16;
    } else {
      metadata = Deputy.LDC_8;
    }
    
    onConstantInsn(Opcodes.LDC, metadata);
    super.visitLdcInsn(cst);
  }
  
    private void onConstantInsn(int opcode, String value) {
      if(shouldInstrument && Profiler.logConstant) {
        final int lineNum = Profiler.latestLineNumber;
        
        String instructionLog = buildInstructionLog(lineNum, EventType.$constant$, 
            opcode, methodDecl.getId(), value);
        
        ProfilerCallBack.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.CONSTANT);
        
        Profiler.latestLineNumber = lineNum;
      }
    }
  
  @Override
  public void visitTypeInsn(int opcode, String type) {
    int dims = 0;
    if(opcode == Opcodes.ANEWARRAY) {
      dims = 1;
    }
    onTypeInsn(opcode, type, dims);
    super.visitTypeInsn(opcode, type);
  }
  
  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {
    onTypeInsn(Opcodes.MULTIANEWARRAY, desc, dims);
    super.visitMultiANewArrayInsn(desc, dims);
  }
  
    private void onTypeInsn(int opcode, String type, int dims) {
      if(shouldInstrument && Profiler.logType) {
        final int lineNum = Profiler.latestLineNumber;
        
        String instructionLog = buildInstructionLog(lineNum, EventType.$type$, 
            opcode, methodDecl.getId(), type, String.valueOf(dims));
        
        ProfilerCallBack.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.TYPE);
        
        Profiler.latestLineNumber = lineNum;
      }
    }
  
  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    onSwtich(Opcodes.LOOKUPSWITCH);
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }
  
  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    onSwtich(Opcodes.TABLESWITCH);
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }
  
    private void onSwtich(int opcode) {
      if(shouldInstrument && Profiler.logSwitch) {
        final int lineNum = Profiler.latestLineNumber;
        
        String instructionLog = buildInstructionLog(lineNum, EventType.$switch$, 
            opcode, methodDecl.getId());
        
        ProfilerCallBack.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.TYPE);
        
        Profiler.latestLineNumber = lineNum;
      }
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