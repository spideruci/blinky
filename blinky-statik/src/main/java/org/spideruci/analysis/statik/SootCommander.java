package org.spideruci.analysis.statik;

import java.util.List;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * This class contains static utilities that talk to Soot's global datastructures.
 * It's a way of separating Soot's globalness and other idiosyncrasies from 
 * Blinky Statik.
 * Infact, ideally we would like to limit the use of these utilities from the 
 * StatikClassGraphBuilder.
 * @author vpalepu
 *
 */
public class SootCommander {
  
  public static void RUN_SOOT(String[] args) {
    soot.Main.main(args);
  }
  
  public static List<SootMethod> GET_ENTRY_METHODS() {
    return Scene.v().getEntryPoints();
  }
  
  public static UnitGraph GET_UNIT_GRAPH(SootMethod method) {
    Body body = method.retrieveActiveBody();
    UnitGraph flowGraph = new ExceptionalUnitGraph(body);

    return flowGraph;
  }
  
  public static CallGraph GET_CALLGRAPH() {
    return Scene.v().getCallGraph();
  }
  
  public static SootClass GET_MAIN_CLASS() {
    return Scene.v().getMainClass();
  }
  
  public static SootMethod GET_MAIN_METHOD() {
    return Scene.v().getMainMethod();
  }

}
