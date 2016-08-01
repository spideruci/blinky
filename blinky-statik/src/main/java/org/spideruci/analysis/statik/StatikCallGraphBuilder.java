package org.spideruci.analysis.statik;

import java.util.HashMap;
import java.util.Map;

import soot.Scene;
import soot.SceneTransformer;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

public final class StatikCallGraphBuilder extends SceneTransformer {
  
  private CallGraph cg;
  private final String graphId;
  
  public static StatikCallGraphBuilder create(String graphId) {
    return new StatikCallGraphBuilder(graphId);
  }
  
  private StatikCallGraphBuilder(String graphId) {
    this.graphId = graphId;
  }
  
  public String graphId() {
    return graphId;
  }
  
  public CallGraph cg() {
    return cg;
  }

  @Override
  protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
    
    // Compute call graph
    CHATransformer.v().transform();
//    setSparkPointsToAnalysis();
    this.cg = Scene.v().getCallGraph();
  }
  
  static void setSparkPointsToAnalysis() {
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