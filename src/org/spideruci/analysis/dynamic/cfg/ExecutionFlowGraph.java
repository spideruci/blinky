package org.spideruci.analysis.dynamic.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import linloglayout.Edge;
import linloglayout.Node;
import linloglayout.OptimizerModularity;

public class ExecutionFlowGraph implements Iterable<Edge> {
  private final List<Node> nodes;
  private final List<Edge> edges;
  
  public static ExecutionFlowGraph create() {
    return new ExecutionFlowGraph();
  }
  
  private ExecutionFlowGraph() {
    nodes = new ArrayList<>();
    edges = new ArrayList<>();
  }
  
  private void addNode(Node node) {
    if(nodes.contains(node)) {
      return;
    }
    this.nodes.add(node);
  }
  
  public void addEdge(Edge edge) {
    int index = edges.indexOf(edge);
    if(index != -1) {
      
      this.changeWeight(edge, edges.get(index).weight + 1);
      return;
    }
    
    this.addNode(edge.startNode);
    this.addNode(edge.endNode);
    this.edges.add(edge);
  }
  
  /**
   * If {@code edge} exists in the graph, then replace it with a new edge
   * with the specified {@code weight}.
   * @param edge
   * @param weight
   * @return The new edge, if the weight got updated, or {@code null} if the 
   * original {@code edge} never existed to begin with.
   */
  public Edge changeWeight(Edge edge, double weight) {
    if(edges.contains(edge)) {
      Edge copyEdge = Edge.copyWithNewWt(edge, weight);
      edges.add(copyEdge);
      edges.remove(edge);
      return copyEdge;
    }
    return null;
  }
  
  public Map<Integer, List<Node>> modularize() {
    OptimizerModularity modularity = new OptimizerModularity();
    boolean ignoreLoops = false;
    Map<Node, Integer> moduledNodes = 
        modularity.execute(this.nodes, this.edges, ignoreLoops);
    Map<Integer, List<Node>> modules = new HashMap<>();
    
    for(Node node : moduledNodes.keySet()) {
      int module = moduledNodes.get(node);
//      System.out.println(node + " " + module);
      if(modules.containsKey(module)) {
        List<Node> nodes = modules.get(module);
        if(!nodes.contains(node))
          nodes.add(node);
      } else {
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        modules.put(module, nodes);
      }
    }
    
    return modules;
  }

  @Override
  public Iterator<Edge> iterator() {
    return edges.iterator();
  }

}
