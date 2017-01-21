package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.statik.calls.CallGraphManager;

public abstract class AnalysisBlock {
  private String subject;
	
  public abstract void execute(CallGraphManager cgm);

  public void setSubject(String s){
	  subject = s;
  }
  
  protected String getSubject(){
	  return subject;
  }
}
