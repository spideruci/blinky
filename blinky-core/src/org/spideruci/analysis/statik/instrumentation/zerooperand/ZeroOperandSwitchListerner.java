package org.spideruci.analysis.statik.instrumentation.zerooperand;

import static org.spideruci.analysis.trace.EventBuilder.buildInstructionLog;

import org.objectweb.asm.MethodVisitor;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.BytecodeMethodAdapter;
import org.spideruci.analysis.statik.instrumentation.ProbeBuilder;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.MethodDecl;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.util.MyAssert;

public class ZeroOperandSwitchListerner {
  
  private final MethodVisitor mv;
  private final int linenumber;
  private final int bytecodeIndex;
  private final MethodDecl methodDecl;
  
  public static ZeroOperandSwitchListerner create(MethodVisitor mv, 
      int bytecodeIndex, int linenumber, MethodDecl methodDecl) {
    MyAssert.assertThat(methodDecl.getType() == EventType.$$method$$, 
        "methodDecl's expected event type is " + EventType.$$method$$ +
        "\n methodDecl's actual event type is " + methodDecl.getType());
    
    ZeroOperandSwitchListerner listerner = 
        new ZeroOperandSwitchListerner(mv, bytecodeIndex, linenumber, methodDecl);
    return listerner;
  }
  
  private ZeroOperandSwitchListerner(MethodVisitor mv, int bytecodeIndex, int linenumber, MethodDecl methodDecl) {
    this.mv = mv;
    this.linenumber = linenumber;
    this.bytecodeIndex = bytecodeIndex;
    this.methodDecl = methodDecl;
  }
  
  public void onConstantLoad(final int opcode) {
    onConstantEvent(opcode);
  }

  public void onPrimitiveArrayLoad(final int opcode) {
    onArrayLoadEvent(opcode);
  }

  public void onWidePrimitiveArrayLoad(final int opcode) {
    onArrayLoadEvent(opcode);
  }

  public void onReferenceArrayLoad(final int opcode) {
    onArrayLoadEvent(opcode);
  }

  public void onWidePrimitiveArrayStore(final int opcode) {
    onArrayStoreEvent(opcode);
  }

  public void onPrimitiveArrayStore(final int opcode) {
    onArrayStoreEvent(opcode);
  }

  public void onReferenceArrayStore(final int opcode) {
    onArrayStoreEvent(opcode);
  }

  public void onStackManipulation(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$stack$);
  }

  public void onMathBoolOrBit(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$math$);
  }

  public void onPrimitiveTypeConversion(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$type$);
  }

  public void onComparison(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$compare$);
  }

  public void onReturn(final int opcode) {
    // do nothing.
  }

  public void onArraylength(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$arraylen$);
  }

  public void onAthrow(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$athrow$);
  }

  public void onMonitor(final int opcode) {
    onRegularZeroOperandEvent(opcode, EventType.$monitor$);
  }

    private void onRegularZeroOperandEvent(final int opcode, final EventType eventType) {
      String instructionLog = buildInstructionLog(bytecodeIndex, linenumber, eventType, 
          opcode, methodDecl.getId());
      
      final String profilerName = BytecodeMethodAdapter.profilerToUse(methodDecl.getDeclOwner());
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .passArg(eventType.toString())
      .build(Profiler.ZERO_OP, profilerName);
    }
    
    private void onConstantEvent(final int opcode) {
      String instructionLog = buildInstructionLog(bytecodeIndex, linenumber, EventType.$constant$, 
          opcode, methodDecl.getId());

      final String profilerName = BytecodeMethodAdapter.profilerToUse(methodDecl.getDeclOwner());
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.CONSTANT, profilerName);
    }
    
    private void onArrayLoadEvent(final int opcode) {
      String instructionLog = buildInstructionLog(bytecodeIndex, linenumber, 
          EventType.$arrayload$, opcode, methodDecl.getId());

      final String profilerName = BytecodeMethodAdapter.profilerToUse(methodDecl.getDeclOwner());
      ProbeBuilder.start(mv)
      .passArrayLoadStackArgs(opcode)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.ARRAY, profilerName);
    }
    
    private void onArrayStoreEvent(final int opcode) {
      String instructionLog = buildInstructionLog(bytecodeIndex, linenumber, 
          EventType.$arraystore$, opcode, methodDecl.getId());

      final String profilerName = BytecodeMethodAdapter.profilerToUse(methodDecl.getDeclOwner());
      ProbeBuilder.start(mv)
      .passArrayStoreStackArgs(opcode)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.ARRAY, profilerName);
    }

}
