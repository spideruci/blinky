package org.spideruci.analysis.statik.flow;

import java.util.ArrayList;
import java.util.List;

import org.spideruci.analysis.statik.DebugUtil;
import org.spideruci.analysis.statik.Items;
import org.spideruci.analysis.statik.SootCommander;
import org.spideruci.analysis.statik.Statik;
import org.spideruci.analysis.statik.calls.CallGraphManager;
import org.spideruci.analysis.statik.controlflow.Graph;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.LineNumberTag;

public class StatikFlowGraph {
  
//  private CallGraph callgraph;
  private CallGraphManager callgraphMgr;
//  private ArrayList<SootMethod> entryPoints;
  public final IcfgManager icfgMgr;
  
  /**
   * Initializes a static inter-proc flow graph, without using the main method
   * as an entry point.
   * @param cg
   * @param entryMethods
   * @return
   */
  public static StatikFlowGraph init(CallGraphManager cgm) {
    return new StatikFlowGraph(cgm);
  }
  
  private StatikFlowGraph(CallGraphManager cgm) {
    this.callgraphMgr = cgm;
    this.icfgMgr = new IcfgManager();
  }
  
  public Graph<Unit> getIcfg() {
    return this.icfgMgr.icfg();
  }

  public void buildIcfg() {
    
    ArrayList<SootMethod> worklist = initWorklist();
    ArrayList<SootMethod> visited = new ArrayList<>();
    
    int i = 0;
    CallGraph callgraph = callgraphMgr.getCallgraph();
    
    while(!worklist.isEmpty()) {

      if(i%10 == 0)
        System.out.println("building.....");
      i++;

      SootMethod srcMethod = worklist.remove(0);
      if(methodIsInvalid(srcMethod)) {
        continue;
      }

      visited.add(srcMethod);
      icfgMgr.addSootMethodToIcfg(srcMethod);
      Items<Edge> outEdges = new Items<>(callgraph.edgesOutOf(srcMethod));

      for(Edge edge : outEdges) {
        SootMethod tgtMethod = edge.tgt();
        
        if(methodIsInvalid(tgtMethod))
          continue;
        
        Body tgtBody = tgtMethod.retrieveActiveBody();
        
        Unit callUnit = edge.srcUnit();
        Unit entryUnit = getEntryUnit(tgtBody);
//        int tgtStartLine = tgtMethod.getJavaSourceStartLineNumber();
//        entryUnit.addTag(new LineNumberTag(tgtStartLine));
        
        List<Unit> tgtExitUnits = SootCommander.GET_UNIT_GRAPH(tgtMethod).getTails();
        
        icfgMgr.addIcfgEdge(callUnit, srcMethod, entryUnit, tgtMethod);
        
        for(Unit tgtExit : tgtExitUnits) {
          icfgMgr.addIcfgEdge(tgtExit, tgtMethod, callUnit, srcMethod);
        }
        
        if(!visited.contains(tgtMethod) && !worklist.contains(tgtMethod)) {
          worklist.add(tgtMethod);
        }
      }
    }
    
  }
  
    private Unit getEntryUnit(Body body) {
      PatchingChain<Unit> units = body.getUnits();
      
      for(Unit unit : units) {
        if(unit.getJavaSourceStartLineNumber() > 0)
          return unit;
      }
      
      return units.getFirst();
    }
  
    private boolean methodIsInvalid(SootMethod method) {
      return method == null || !method.isConcrete();
    }
  
    private ArrayList<SootMethod> initWorklist() {
      DebugUtil.printfln("initializing worklist of Soot Methods.");

      ArrayList<SootMethod> entryPoints = this.callgraphMgr.getEntryPoints();
      ArrayList<SootMethod> worklist = new ArrayList<>();
      
      for(SootMethod entrypoint : entryPoints) {
        DebugUtil.printfln(
            "adding entrypoint method to worklist: %s", 
            entrypoint.toString());
        worklist.add(entrypoint);
      }
      
      CallGraph callgraph = callgraphMgr.getCallgraph();
      Items<MethodOrMethodContext> sources = new Items<>(callgraph.sourceMethods());

      for(MethodOrMethodContext momCtx : sources) {
        if(momCtx == null)
          continue;
        
        SootMethod method = momCtx.method();
        if(method == null) {
          continue;
        }
        
        if(!method.isConcrete()) {
          DebugUtil.printfln("Not adding %s to worklist because "
              + "method is not concrete", method.toString());
          continue;
        }
        
        if(!entryPoints.contains(method)) {
          DebugUtil.printfln("Not adding %s to worklist because "
              + "method not an entrypoint", method.toString());
          continue;
        }
        
        if(worklist.contains(method)) {
          DebugUtil.printfln("Not adding %s to worklist because "
              + "worklist alread contains it.", method.toString());
          continue;
        }
        
        DebugUtil.printfln("adding method to worklist: %s", method.toString());
        worklist.add(method);
      }

      return worklist;
    }
    
}
