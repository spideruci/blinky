package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.List;

import org.spideruci.analysis.statik.controlflow.Graph;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class StatikFlowGraph {
  
  private CallGraph callgraph;
  private ArrayList<SootMethod> entryPoints;
  public final IcfgManager icfgMgr;
  
  public static StatikFlowGraph init(CallGraph cg) {
    StatikFlowGraph sfg = new StatikFlowGraph(cg);
    sfg.setupEntries();
    return sfg;
  }
  
  private StatikFlowGraph(CallGraph cg) {
    this.callgraph = cg;
    this.entryPoints = new ArrayList<>();
    this.icfgMgr = new IcfgManager();
  }

  public void addEntryPoint(SootMethod method) {
    entryPoints.add(method);
  }
  
  public Graph<Unit> getIcfg() {
    return this.icfgMgr.icfg();
  }
  
  private void setupEntries() {
    List<SootMethod> sootmethods = Scene.v().getEntryPoints();
    for(SootMethod method : sootmethods)
      this.addEntryPoint(method);
    
    SootMethod main = Scene.v().getMainClass().getMethodByName("main");
    this.addEntryPoint(main);
  }
  
  /**
   * Does a BFS traversal over the call graph. This uses the entry
   * methods as the start nodes for the traversal.
   * 
   * @return a list of SootMethod's in the order of visits 
   * during the BSF traversal.
   */
  public ArrayList<SootMethod> visitMethodsTopDown() {

    ArrayList<SootMethod> worklist = initWorklist();
    ArrayList<SootMethod> visited = new ArrayList<>();

    while(!worklist.isEmpty()) {
      SootMethod src = worklist.remove(0);

      visited.add(src);

      Items<Edge> outEdges = new Items<>(callgraph.edgesOutOf(src));
      
      for(Edge edge : outEdges) {
        SootMethod tgt = edge.tgt();
        
        if(!visited.contains(tgt)) {
          worklist.add(tgt);
        }
      }
    }

    return visited;
  }
  
  public void buildIcfg() {
    
    ArrayList<SootMethod> worklist = initWorklist();
    ArrayList<SootMethod> visited = new ArrayList<>();
    
    while(!worklist.isEmpty()) {
      SootMethod src = worklist.remove(0);
      if(methodIsInvalid(src)) {
        continue;
      }

      visited.add(src);
      icfgMgr.addSootMethodToIcfg(src);
      Items<Edge> outEdges = new Items<>(callgraph.edgesOutOf(src));
      
      
      for(Edge edge : outEdges) {
        SootMethod tgt = edge.tgt();
        
        if(methodIsInvalid(tgt))
          continue;
        
        Body body = tgt.retrieveActiveBody();
        
        Unit callUnit = edge.srcUnit();
        Unit entryUnit = body.getUnits().getFirst();
        
        icfgMgr.addIcfgEdge(callUnit, src, entryUnit, tgt);
        
        if(!visited.contains(tgt)) {
          worklist.add(tgt);
        }
      }
    }
    
  }
  
    private boolean methodIsInvalid(SootMethod method) {
      return method == null || !method.isConcrete();
    }
  
    private ArrayList<SootMethod> initWorklist() {
      ArrayList<SootMethod> worklist = new ArrayList<>();
      Items<MethodOrMethodContext> sources = new Items<>(callgraph.sourceMethods());
      
      for(MethodOrMethodContext momCtx : sources) {
        if(momCtx == null)
          continue;
        
        SootMethod method = momCtx.method();
        if(method == null 
            || !method.isConcrete() 
            || !entryPoints.contains(method))
          continue;
        
        worklist.add(method);
      }
      
      return worklist;
    }
    
}
