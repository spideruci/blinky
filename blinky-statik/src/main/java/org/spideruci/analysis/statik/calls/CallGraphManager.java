package org.spideruci.analysis.statik.calls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.spideruci.analysis.statik.DebugUtil;
import org.spideruci.analysis.statik.Items;
import org.spideruci.analysis.statik.controlflow.Algorithms;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;
import org.spideruci.analysis.util.MyAssert;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CallGraphManager {
  
  private CallGraph callgraph;
  private Graph<SootMethod> shadowCallgraph;
  private ArrayList<SootMethod> entryPoints;
  
  /**
   * Initializes a static inter-proc flow graph, without using the main method
   * as an entry point.
   * @param cg
   * @param entryMethods
   * @return
   */
  public static CallGraphManager init(CallGraph cg, List<SootMethod> entryMethods) {
    return init(cg, entryMethods, false);
  }
  
  public static CallGraphManager init(CallGraph cg, 
      List<SootMethod> entryMethods, 
      boolean useMainAsEntry) {
    
    CallGraphManager cgm = new CallGraphManager(cg);
    cgm.makeShadowGraph();

    for(SootMethod method : entryMethods) {
      cgm.addEntryPoint(method);
    }
   
    if(useMainAsEntry) {
      SootMethod main = Scene.v().getMainClass().getMethodByName("main");
      cgm.addEntryPoint(main);
    }

    return cgm;
  }
  
    private CallGraphManager(CallGraph cg) {
      this.callgraph = cg;
      this.entryPoints = new ArrayList<>();
      this.shadowCallgraph = Graph.create();
    }

  public CallGraph getCallgraph() {
    return this.callgraph;
  }
  
  public ArrayList<SootMethod> getEntryPoints() {
    return this.entryPoints;
  }
    
  public void addEntryPoint(SootMethod method) {
    entryPoints.add(method);
    Node<SootMethod> node = getShadowMethod(method);
    this.shadowCallgraph.startPointsTo(node);
  }
  
  public boolean methodExists(SootMethod method) {
    final Node<SootMethod> node = getShadowMethod(method);
    final boolean methodExists = node != null;

    if(methodExists)
      MyAssert.assertThat(method.equivHashCode() == node.getBody().equivHashCode());
    
    return methodExists;
  }

  public Iterable<SootMethod> getMethodCallers(SootMethod method) {
    
    if(methodExists(method)) {
      final Node<SootMethod> node = getShadowMethod(method);
      ArrayList<Node<SootMethod>> shadowCallers = node.getPredecessors();
      Iterable<SootMethod> callers = fromShadowMethods(shadowCallers);
      return callers;
    }
    
    return Collections.emptyList();
  }
  
  public Iterable<SootMethod> getMethodCallsFrom(SootMethod method) {
    
    if(methodExists(method)) {
      final Node<SootMethod> node = getShadowMethod(method);
      ArrayList<Node<SootMethod>> shadowCallers = node.pointsTo();
      Iterable<SootMethod> callers = fromShadowMethods(shadowCallers);
      return callers;
    }
    
    return Collections.emptyList();
  }
  
  public HashMap<Node<SootMethod>, Node<SootMethod>> getStrongComponents() {
    return Algorithms.getStronglyConnectedComponents(shadowCallgraph);
  }
  
  public Iterable<SootMethod> getBottomupTopology() {
    
    HashMap<Node<SootMethod>, Node<SootMethod>> leaders = 
        Algorithms.collapseStrongComponents(shadowCallgraph);
    
    System.out.println(new HashSet<>(leaders.values()).size());
    
    ArrayList<Node<SootMethod>> topology = 
        Algorithms.bottomUpTopologicalSort(shadowCallgraph);
    Iterable<SootMethod> methodTopology = fromShadowMethods(topology);
    
    makeShadowGraph();
    
    return methodTopology;
  }
  
  public Iterable<SootMethod> getDfsTraversal() {
    ArrayList<Node<SootMethod>> traversal = Algorithms.traverseDfs(shadowCallgraph);
    Iterable<SootMethod> methods = fromShadowMethods(traversal);
    return methods;
  }
  
  public Iterable<SootMethod> getBfsTraversal() {
    Collection<Node<SootMethod>> traversal = shadowCallgraph.getNodes();// Algorithms.traverseBfs(shadowCallgraph);
    System.out.println(shadowCallgraph.getNodes().size());
//    System.out.println(traversal.size());
    Iterable<SootMethod> methods = fromShadowMethods(traversal);
    return methods;
  }
  
  public void printGraph() {
    Collection<Node<SootMethod>> shadowMethodNodes = shadowCallgraph.getNodes();

    for(Node<SootMethod> visitedNode : shadowMethodNodes) {
      if(visitedNode == shadowCallgraph.startNode()
          || visitedNode == shadowCallgraph.endNode())
        continue;
      
      SootMethod m = visitedNode.getBody();
      System.out.println(visitedNode.getLabel());
      
      if(!m.isConcrete())
        continue;
      
      ArrayList<Node<SootMethod>> neighbors = visitedNode.pointsTo();
      
      for(Node<SootMethod> neighbor : neighbors) {
        System.out.format("\t↪ %s%n", neighbor.getLabel());
      }
    }
  }


  private void makeShadowGraph() {
    Iterator<Edge> edges = callgraph.iterator();

    for(Edge edge : Items.items(edges)) {
      SootMethod srcMethod = edge.src();
      SootMethod tgtMethod = edge.tgt();

      DebugUtil.printfln("Original Call Edge: %s => %s", srcMethod, tgtMethod);

      Node<SootMethod> srcNode = getValidatedShadowNode(srcMethod);
      if(srcNode == null)
        continue;
      
      Node<SootMethod> tgtNode = getValidatedShadowNode(tgtMethod);
      if(tgtNode == null)
        continue;

      srcNode.pointsTo(tgtNode);
      DebugUtil.printfln("Shadow Call Edge: %s => %s", srcNode, tgtNode);
    }
  }
  
  private Node<SootMethod> getValidatedShadowNode(SootMethod srcMethod) {
    if(srcMethod == null || !srcMethod.isConcrete()) {
      return null;
    }

    Node<SootMethod> srcNode = getShadowMethod(srcMethod);
    if(srcNode == null) {
      return null;
    }

    MyAssert.assertThat(
        srcMethod.equivHashCode() == srcNode.getBody().equivHashCode(),
        String.format(
            "Method Mismatch: Actual: %s, Shadow: %s", 
            srcMethod, srcNode.getBody()));
    
    return srcNode;
  }

  private Node<SootMethod> getShadowMethod(SootMethod method) {
    String label = method.getSignature();

    Node<SootMethod> node = null;
    if(shadowCallgraph.contains(label)) {
      node = shadowCallgraph.node(label);
    } else {
      node = Node.create(label, method, this.shadowCallgraph);
      this.shadowCallgraph.nowHas(node);
    }

    return node;
  }
  
  private Iterable<SootMethod> fromShadowMethods(Iterable<Node<SootMethod>> shadowMethods) {
    ArrayList<SootMethod> methods = new ArrayList<>();
    for(Node<SootMethod> shadowMethod : shadowMethods) {
      SootMethod method = shadowMethod.getBody();
      if(method == null) {
        continue;
      }

      methods.add(method);
    }

    return methods;
  }
  
}
