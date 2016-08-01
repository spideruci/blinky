package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

import soot.Body;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.MHGDominatorTree;

/**
 * @author vpalepu
 */
public class Statik {
  
  static {
    System.out.println( "Welcome to ``Blinky Statik``, a static analysis framework for \nJVM executables."
        + "\nThis framework currently uses algorithms from Soot and "
        + "\nHeros to carry out the static analysis.\n" );
  }
  
  public static final String JRE7_LIB = "/Users/vpalepu/open-source/java/golden/jre1.7.0_60.jre/Contents/Home/lib";

  public static void main( String[] args ) {
    

    List<String> argsList = new ArrayList<>(Arrays.asList(args));
    argsList.addAll(Arrays.asList(new String[] {
        "-cp",
        JRE7_LIB + "/rt.jar:" + JRE7_LIB + "/jce.jar:" + "target/test-classes/",
        "-w",
        "-main-class",
        "org.spideruci.analysis.statik.subjects.CallGraphSubject",//main-class
        "org.spideruci.analysis.statik.subjects.CallGraphSubject",//argument classes
        "org.spideruci.analysis.statik.subjects.A"      //
    }));

    
    StatikCallGraphBuilder cgBuilder =  StatikCallGraphBuilder.create("call-graph");
    Transform cgBuilderTrans = new Transform("wjtp.cgbuilder", cgBuilder);
    PackManager.v().getPack("wjtp").add(cgBuilderTrans);

    args = argsList.toArray(new String[0]);
    soot.Main.main(args);
    
    CallGraph cg = cgBuilder.cg();
    
    StatikFlowGraph flowGraph = new StatikFlowGraph(cg);
    SootMethod main = Scene.v().getMainClass().getMethodByName("main");
    flowGraph.addEntryPoint(main);
    
    ArrayList<SootMethod> visitedNodes = flowGraph.visitMethodsTopDown();
    
    System.out.println("---- Call Graph ----");
    for(SootMethod visitedNode : visitedNodes) {
      SootMethod m = visitedNode;
      System.out.println(m);
      
      if(!m.isConcrete())
        continue;
      
      Iterator<Edge> edges = cg.edgesOutOf(m);
      
      while(edges.hasNext()) {
        Edge edge = edges.next();
        
        Unit sourceUnit = edge.srcUnit();
        SootMethod targetMethod = edge.tgt();
        System.out.format("\t%s, calls: %s\n", sourceUnit, targetMethod);
      }
    }
    
    System.out.println("---- I C F G ----");
    
    flowGraph.buildIcfg();
    Graph<Unit> icfg = flowGraph.getIcfg();
    System.out.println(icfg);
    
    System.out.println("---- Java Source Icfg ----");
    System.out.println(flowGraph.icfgMgr.icfgJavaSourceLines());
  }

}
