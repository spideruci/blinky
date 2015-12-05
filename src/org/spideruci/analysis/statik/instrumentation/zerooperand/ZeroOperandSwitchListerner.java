package org.spideruci.analysis.statik.instrumentation.zerooperand;

import static org.spideruci.analysis.trace.EventBuilder.buildInstructionLog;

import org.objectweb.asm.MethodVisitor;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.ProfilerCallBack;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.util.MyAssert;

public class ZeroOperandSwitchListerner {
  
  private final MethodVisitor mv;
  private final int linenumber;
  private final TraceEvent methodDecl;
  
  public static ZeroOperandSwitchListerner create(MethodVisitor mv, 
      int linenumber, TraceEvent methodDecl) {
    MyAssert.assertThat(methodDecl.getType() == EventType.$$method$$, 
        "methodDecl's expected event type is " + EventType.$$method$$ +
        "\n methodDecl's actual event type is " + methodDecl.getType());
    
    ZeroOperandSwitchListerner listerner = 
        new ZeroOperandSwitchListerner(mv, linenumber, methodDecl);
    return listerner;
  }
  
  private ZeroOperandSwitchListerner(MethodVisitor mv, int linenumber, TraceEvent methodDecl) {
    this.mv = mv;
    this.linenumber = linenumber;
    this.methodDecl = methodDecl;
  }
  
  public void onConstantLoad(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$constant$);
  }

  public void onPrimitiveArrayLoad(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arrayload$);
  }

  public void onWidePrimitiveArrayLoad(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arrayload$);
  }

  public void onReferenceArrayLoad(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arrayload$);
  }

  public void onWidePrimitiveArrayStore(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arraystore$);
  }

  public void onPrimitiveArrayStore(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arraystore$);
  }

  public void onReferenceArrayStore(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arraystore$);
  }

  public void onStackManipulation(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$stack$);
  }

  public void onMathBoolOrBit(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$math$);
  }

  public void onPrimitiveTypeConversion(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$type$);
  }

  public void onComparison(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$compare$);
  }

  public void onReturn(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$return$);
  }

  public void onArraylength(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$arraylen$);
  }

  public void onAthrow(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$athrow$);
  }

  public void onMonitor(final int opcode) {
    onZeroOperandEvent(opcode, EventType.$monitor$);
  }

    private void onZeroOperandEvent(final int opcode, final EventType eventType) {
      String instructionLog = buildInstructionLog(linenumber, eventType, 
          opcode, methodDecl.getId());
  
      ProfilerCallBack.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .passArg(eventType.toString())
      .build(Profiler.ZERO_OP);
    }

}
