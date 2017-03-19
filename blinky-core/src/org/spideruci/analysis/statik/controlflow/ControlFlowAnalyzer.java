package org.spideruci.analysis.statik.controlflow;

import static org.spideruci.analysis.util.caryatid.Helper.i2s;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.Count;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.trace.events.props.ControlFlowPropNames;


public class ControlFlowAnalyzer extends Analyzer<BasicValue> {
  
  public static ControlFlowAnalyzer init(final String methodDeclId, final AbstractInsnNode[] insns) {
    return new ControlFlowAnalyzer(new BasicInterpreter(), methodDeclId, insns);
  }
  
  private final String methodDeclId;
  private final AbstractInsnNode[] insns;
  
  public ControlFlowAnalyzer(Interpreter<BasicValue> interpreter,
      final String methodDeclId,
      final AbstractInsnNode[] insns) {
    super(interpreter);
    this.methodDeclId = methodDeclId;
    this.insns = insns;
  }

  protected void newControlFlowEdge(int src, int dst) {
    addControlFlowEdge(src, dst, 0);
  }

  protected boolean newControlFlowExceptionEdge(int src, int dst) {
    addControlFlowEdge(src, dst, 1);
    return true;
  }

  private void addControlFlowEdge(int src, int dst, int isExceptional) {
    int flowId = Count.anotherFlow();
    
    int srcOp = insns[src] == null? -1 : insns[src].getOpcode();
    int dstOp = insns[dst] == null? -1 : insns[dst].getOpcode();
    
    TraceEvent flowEvent = TraceEvent.createControlFlowEvent(flowId);
    flowEvent.setProp(ControlFlowPropNames.DECL_HOST_ID, methodDeclId);
    
    flowEvent.setProp(ControlFlowPropNames.SRC_BYTECODE, i2s(src));
    flowEvent.setProp(ControlFlowPropNames.SRC_OPCODE, i2s(srcOp));
    
    flowEvent.setProp(ControlFlowPropNames.DST_BYTECODE, i2s(dst));
    flowEvent.setProp(ControlFlowPropNames.DST_OPCODE, i2s(dstOp));
    
    flowEvent.setProp(ControlFlowPropNames.EXCEPTIONAL, i2s(isExceptional));
    
    String instructionLog = flowEvent.getLog();
    
    synchronized (Profiler.REAL_OUT) {
      Profiler.REAL_OUT.println(instructionLog);
    }
    
  }

}