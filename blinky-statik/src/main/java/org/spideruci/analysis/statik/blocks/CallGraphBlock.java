package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.statik.calls.CallGraphManager;

import soot.SootMethod;

public class CallGraphBlock extends AnalysisBlock {

  @Override
  public void execute(CallGraphManager cgm) {

    System.out.println("----");

    int i = 0;
    for(SootMethod method : cgm.getBfsTraversal()) {
      System.out.println(method);
      i += 1;
    }
    System.out.println(i);

    System.out.println("----");
    int j = 0;
    for(SootMethod method : cgm.getBottomupTopology()) {
      j += 1;
      System.out.println(method);
    }
    System.out.println(j);

    System.out.println("---- Call Graph ----");

    cgm.printGraph();

  }

}
