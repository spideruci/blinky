package org.spideruci.analysis.statik.blocks;

import org.spideruci.analysis.io.WriteToText;
import org.spideruci.analysis.statik.calls.CallGraphManager;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.flow.StatikFlowGraph;
import org.spideruci.analysis.statik.flow.data.SourceICFG;

import soot.Unit;

public class FlowBlock extends AnalysisBlock {

  @Override
  public void execute(CallGraphManager cgm) {
	  
//	  cgm.getCallgraph().toString();
	  
	WriteToText wtt = new WriteToText();
	
//	wtt.dump(cgm);
	  
    StatikFlowGraph flowGraph = StatikFlowGraph.init(cgm);
    
    System.out.println("---- I C F G ----");
    
    flowGraph.buildIcfg(this.getSubject());
    
//    wtt.dump(flowGraph.icfgMgr.icfg());
    
//    Graph<Unit> icfg = flowGraph.getIcfg();
//    System.out.println(icfg);
        
    System.out.println("---- Java Source Icfg ----");
    
    Graph<String> icfgGraph = flowGraph.icfgMgr.icfgJavaSourceLines(this.getSubject());
//    System.out.println(icfgGraph);    
    
//    wtt.dump(icfgGraph);

    
//    icfg = null;
    System.gc();
    
    System.out.println("---- Writing to Database ----");  
    SourceICFG sIcfg = new SourceICFG(icfgGraph);
    sIcfg.dumpData(this.getSubject());
    
    System.out.println("Done!");
    
  }

}
