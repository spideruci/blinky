package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.statik.Items;
import org.spideruci.analysis.statik.calls.CallGraphManager;

import soot.MethodOrMethodContext;
import soot.SootMethod;

public class CallGraphBlock extends AnalysisBlock {

  @Override
  public void execute(CallGraphManager cgm) {

//    System.out.println("----");
//
//    int i = 0;
//    for(SootMethod method : cgm.getBfsTraversal()) {
//      System.out.println(method);
//      i += 1;
//    	if(method.getName().contains("test_isPrecise")){
//    		System.out.println(method);
//    	}
    	
//    }
//    System.out.println(i);

//    System.out.println("----");
//    int j = 0;
//    for(SootMethod method : cgm.getBottomupTopology()) {
//      j += 1;
//      System.out.println(method);
//    }
//    System.out.println(j);

    System.out.println("---- Call Graph ----");

//    cgm.printGraph();
    
    for(MethodOrMethodContext m: Items.items(cgm.getCallgraph().sourceMethods())){
    	if(m == null)
    		continue;
    	
    	System.out.println(m);
    }

  }

}
