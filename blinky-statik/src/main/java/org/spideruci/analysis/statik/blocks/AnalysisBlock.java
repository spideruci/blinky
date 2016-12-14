package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.statik.calls.CallGraphManager;

public abstract class AnalysisBlock {
  
  public abstract void run(CallGraphManager cgm);

}
