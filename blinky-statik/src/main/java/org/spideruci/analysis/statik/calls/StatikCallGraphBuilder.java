package org.spideruci.analysis.statik.calls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.spideruci.analysis.statik.AnalysisConfig;
import org.spideruci.analysis.statik.DebugUtil;
import org.spideruci.analysis.statik.SootCommander;

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
  
  private final String graphId;
  private final CallGraphAlgorithm cgAlgo;
  
  /**
   * className => [methodname]
   */
  private TreeMap<String, HashSet<String>> entrypoints = new TreeMap<>(); 
  
  public static StatikCallGraphBuilder build(String graphId, AnalysisConfig config) {
    final String cgAlgoOpt = config.get(AnalysisConfig.CALL_GRAPH_ALGO);
    
    CallGraphAlgorithm cgAlgo = (cgAlgoOpt == null) ? 
        CallGraphAlgorithm.CHA : CallGraphAlgorithm.fromString(cgAlgoOpt);
    
    StatikCallGraphBuilder scgBuilder = 
        new StatikCallGraphBuilder(graphId, cgAlgo);
    
    scgBuilder.addEntryPoint(
        config.get(AnalysisConfig.ENTRY_CLASS), 
        config.get(AnalysisConfig.ENTRY_METHOD));
    
    { // hook up SCG Builder with Soot
      Transform cgBuilderTrans = new Transform("wjtp.cgbuilder", scgBuilder);
      PackManager.v().getPack("wjtp").add(cgBuilderTrans);
    }
    
    String[] args = config.getArgs();
    SootCommander.RUN_SOOT(args);
    
    return scgBuilder;
  }
  
  private StatikCallGraphBuilder(String graphId, CallGraphAlgorithm cgAlgo) {
    this.graphId = graphId;
    this.cgAlgo = cgAlgo;
  }
  
  public String graphId() {
    return graphId;
  }

  public void addEntryPoint(String classname, String methodname) {
    HashSet<String> methods = entrypoints.get(classname);
    if(methods == null) {
      methods = new HashSet<>();
      entrypoints.put(classname, methods);
    }
    
    methods.add(methodname);
  }
  
  public CallGraph buildCallGraph() {
    
    return SootCommander.GET_CALLGRAPH();
  }

  public CallGraph getCallGraph() {
    return SootCommander.GET_CALLGRAPH();
  }
  
  @SuppressWarnings("unused")
  @Override
  protected void internalTransform(
      String phaseName, 
      @SuppressWarnings("rawtypes") Map options) {
    setupEntryPoints();
    
    COMPUTER_LINEMAP: {
      // TODO issue #27 Blinky Statik: Add bytecode offset to Soot Units
    }
    
    COMPUTE_CALL_GRAPH: {
      switch (this.cgAlgo) {
      case CHA:
        CHATransformer.v().transform();
        break;
        
      case SPARK:
        setSparkPointsToAnalysis();
        break;
      }
    }
  }
  
  // private method
  
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