package org.spideruci.analysis.tacoco.testlisteners;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;

public class BlinkyListener implements ITacocoTestListener {

  @Override
  public void onStart() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTestStart(String testName) {
    Profiler.unsetGuard1();
    
  }

  @Override
  public void onTestPassed() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTestFailed() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTestSkipped() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onTestEnd() {
    Profiler.setGuard1();
    
  }

  @Override
  public void onEnd() {
    // TODO Auto-generated method stub
    
  }

}
