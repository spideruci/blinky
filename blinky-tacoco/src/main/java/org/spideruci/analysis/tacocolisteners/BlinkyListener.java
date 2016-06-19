package org.spideruci.analysis.tacocolisteners;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;

public class BlinkyListener implements ITacocoTestListener {

  @Override
  public void onStart() {
    // not yet supported
    
  }

  @Override
  public void onTestStart(String testName) {
    Profiler.unsetGuard1();
    
  }

  @Override
  public void onTestPassed() {
    // not yet supported 
    
  }

  @Override
  public void onTestFailed() {
    // not yet supported
    
  }

  @Override
  public void onTestSkipped() {
    // not yet supported
    
  }

  @Override
  public void onTestEnd() {
    Profiler.setGuard1();
    
  }

  @Override
  public void onEnd() {
    // not yet supported
    
  }

}
