package org.spideruci.analysis.trace.events;

import org.spideruci.analysis.trace.MethodDecl;
import org.spideruci.analysis.trace.TraceEvent;

/**
 * 
 * @author vpalepu
 */
public abstract class TraceEventVisitor {

  protected long latestEventId;

  public long latestEventId() {
    return latestEventId;
  }
  
  abstract public void start();
  abstract public void end();
  
  abstract public void visit(MethodDecl declEvent);
  abstract public void visitInsn(TraceEvent insnEvent);
  
  abstract public void visitSourceLine(TraceEvent event);

  abstract public void visitConstantLoadOprn(TraceEvent event);
  abstract public void visitLoadOprn(TraceEvent event);
  abstract public void visitStoreOprn(TraceEvent event);
  
  abstract public void visitGetStaticInsn(TraceEvent event);
  abstract public void visitPutStaticInsn(TraceEvent event);
  abstract public void visitGetInsn(TraceEvent event);
  abstract public void visitPutInsn(TraceEvent event);
  
  abstract public void visitArrayLoadOprn(TraceEvent event);
  abstract public void visitArrayStoreOprn(TraceEvent event);
  
  abstract public void visitPrimitiveCastOprn(TraceEvent event);
  abstract public void visitPrimitiveCastOprn2(TraceEvent event);
  
  abstract public void visitJumpOprn1(TraceEvent event);
  abstract public void visitJumpOprn2(TraceEvent event);
  abstract public void visitGotoInsn(TraceEvent executionEvent);
  
  abstract public void visitMonitorOprn(TraceEvent event);
  abstract public void visitPopInsn(TraceEvent event);
  abstract public void visitPop2Insn(TraceEvent event);
  abstract public void visitDuplicateOprn(TraceEvent event);
  abstract public void visitSwapInsn(TraceEvent event);
  
  abstract public void visitWideArithmeticOprn(TraceEvent event);
  abstract public void visitArithmeticOprn(TraceEvent event);
  
  abstract public void visitIincInsn(TraceEvent event);
  
  abstract public void visitInvokeOprn(TraceEvent event);
  abstract public void visitMethodEntrySignal(TraceEvent event);
  abstract public void visitReturnOprn(TraceEvent event);
  abstract public void visitAthrowInsn(TraceEvent event);
  abstract public void visitMethodCompleteSignal(TraceEvent event);
  
  abstract public void visitNewOprn(TraceEvent event);
  abstract public void visitInstanceOfInsn(TraceEvent event);
  
  abstract public void visitCheckCastInsn(TraceEvent event);
  abstract public void visitArrayLenInsn(TraceEvent event);
  abstract public void visitNegOprn(TraceEvent event);
  abstract public void visitNopInsn(TraceEvent event);

  abstract public void visitJsrInsn(TraceEvent event);
  abstract public void visitRetInsn(TraceEvent event);

}
