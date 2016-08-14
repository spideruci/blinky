package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.util.Chain;

public final class StatikCallGraphBuilder extends SceneTransformer {
  
  private CallGraph cg;
  private final String graphId;
  private TreeMap<String, HashSet<String>> entrypoints = new TreeMap<>(); 
  
  public static StatikCallGraphBuilder create(String graphId) {
    return new StatikCallGraphBuilder(graphId);
  }
  
  private StatikCallGraphBuilder(String graphId) {
    this.graphId = graphId;
  }
  
  public String graphId() {
    return graphId;
  }
  
  public CallGraph getCallGraph() {
    return cg;
  }
  
  @SuppressWarnings("unused")
  @Override
  protected void internalTransform(
      String phaseName, 
      @SuppressWarnings("rawtypes") Map options) {
    setupEntryPoints();
    
    COMPUTER_LINEMAP: {
      
    }
    
    COMPUTE_CALL_GRAPH: {
      CHATransformer.v().transform();
      this.cg = Scene.v().getCallGraph();
    }
  }
  
  public void hookupWithSoot() {
    Transform cgBuilderTrans = new Transform("wjtp.cgbuilder", this);
    PackManager.v().getPack("wjtp").add(cgBuilderTrans);
  }
  
  public void addEntryPoint(String classname, String methodname) {
    HashSet<String> methods = entrypoints.get(classname);
    if(methods == null) {
      methods = new HashSet<>();
      entrypoints.put(classname, methods);
    }
    
    methods.add(methodname);
  }
  
  private void setupEntryPoints() {
    List<SootMethod> entryPoints = new ArrayList<>();
    
    Chain<SootClass> classes = Scene.v().getClasses();
    for(SootClass sootClass : classes) {
      String className = sootClass.getName();
      
      HashSet<String> entrymethods = this.entrypoints.get(className);
      if(entrymethods == null || entrymethods.isEmpty()) {
        continue;
      }
      
      List<SootMethod> methods = sootClass.getMethods();
      for(SootMethod method : methods) {
        String methodName = method.getName();
        
        if(entrymethods.contains(methodName)) {
          DebugUtil.printfln("Entry Candidate (method): %s", method.toString());
          entryPoints.add(method);
        }
      }
    }
    
    Scene.v().setEntryPoints(entryPoints);
  }
  
  /**
   * Use this method to do perform call graph generation using 
   * a more precise points-to analysis than what class hierarchy gives you.
   */
  @SuppressWarnings("unused")
  private void setSparkPointsToAnalysis() {
    System.out.println("[spark] Starting analysis ...");
        
    HashMap<String, String> opt = new HashMap<>();
    opt.put("enabled","true");
    opt.put("verbose","true");
    opt.put("ignore-types","false");
    opt.put("force-gc","false");
    opt.put("pre-jimplify","false");
    opt.put("vta","false");
    opt.put("rta","false");
    opt.put("field-based","false");
    opt.put("types-for-sites","false");
    opt.put("merge-stringbuffer","true");
    opt.put("string-constants","false");
    opt.put("simulate-natives","true");
    opt.put("simple-edges-bidirectional","false");
    opt.put("on-fly-cg","true");
    opt.put("simplify-offline","false");
    opt.put("simplify-sccs","false");
    opt.put("ignore-types-for-sccs","false");
    opt.put("propagator","worklist");
    opt.put("set-impl","double");
    opt.put("double-set-old","hybrid");
    opt.put("double-set-new","hybrid");
    opt.put("dump-html","false");
    opt.put("dump-pag","false");
    opt.put("dump-solution","false");
    opt.put("topo-sort","false");
    opt.put("dump-types","true");
    opt.put("class-method-var","true");
    opt.put("dump-answer","false");
    opt.put("add-tags","false");
    opt.put("set-mass","false");
    
    opt.put("geom-pta", "true");
    opt.put("geom-encoding", "Geom");
    opt.put("geom-worklist", "PQ");
    opt.put("geom-runs", "2");
    opt.put("geom-app-only", "false");
    
    SparkTransformer.v().transform("",opt);
    
    System.out.println("[spark] Done!");
  }
}