package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.statik.calls.CallGraphManager;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.flow.StatikFlowGraph;
import org.spideruci.analysis.statik.flow.data.SourceICFG;

import soot.Unit;

public class FlowBlock extends AnalysisBlock {

  @Override
  public void execute(CallGraphManager cgm) {
    StatikFlowGraph flowGraph = StatikFlowGraph.init(cgm);
    
    System.out.println("---- I C F G ----");
    
    flowGraph.buildIcfg();
    Graph<Unit> icfg = flowGraph.getIcfg();
    System.out.println(icfg);
        
    System.out.println("---- Java Source Icfg ----");
    
    Graph<String> icfgGraph = flowGraph.icfgMgr.icfgJavaSourceLines();
    System.out.println(icfgGraph);    
    
//    System.out.println("---- Writing to Database ----");
//  
    SourceICFG sIcfg = new SourceICFG(icfgGraph);
    sIcfg.dumpData();
    
    System.out.println("Done!");
    
  }

}
