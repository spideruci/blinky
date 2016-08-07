package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.spideruci.analysis.statik.controlflow.Graph;

import soot.ArrayType;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.JastAddJ.VoidType;
import soot.javaToJimple.JimpleBodyBuilder;
import soot.jimple.Jimple;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * @author vpalepu
 */
public class Statik {
  
  static {
    System.out.println( "Welcome to ``Blinky Statik``, a static analysis framework for JVM executables."
        + "\nThis framework currently uses algorithms from Soot and "
        + "\nHeros to carry out the static analysis.\n" );
  }
  
  public static final String CP_SEP = System.getProperty("path.separator");
  public static final String FP_SEP = System.getProperty("file.separator");
  public static final String JRE7_LIB;
  
  public static final String RTJAR = "/rt.jar";
  public static final String JCEJAR = "/jce.jar";
  
  static {
    JRE7_LIB = System.getProperty("user.home") +  
        FP_SEP + "open-source/java/golden/jre1.7.0_60.jre/Contents/Home/lib";
    System.out.println(JRE7_LIB);
  }

  public static void main( String[] args ) {
    

    List<String> argsList = new ArrayList<>(Arrays.asList(args));
    argsList.addAll(Arrays.asList(new String[] {
        "--keep-line-number",
        "-cp",
        JRE7_LIB + "/rt.jar:" + JRE7_LIB + "/jce.jar:" + "target/test-classes/",
        "-w",
        "org.spideruci.analysis.statik.subjects.A"//argument classes
    }));
    
    DummyMainManager.setupDummyMain();
    
    StatikCallGraphBuilder cgBuilder =  StatikCallGraphBuilder.create("call-graph");
    cgBuilder.addEntryPoint("org.spideruci.analysis.statik.subjects.A", "foo");
    
    Transform cgBuilderTrans = new Transform("wjtp.cgbuilder", cgBuilder);
    PackManager.v().getPack("wjtp").add(cgBuilderTrans);

    args = argsList.toArray(new String[0]);
    soot.Main.main(args);
    
    CallGraph cg = cgBuilder.cg();
    
    StatikFlowGraph flowGraph = StatikFlowGraph.init(cg);
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
