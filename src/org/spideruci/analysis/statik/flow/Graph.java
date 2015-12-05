package org.spideruci.analysis.statik.flow;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.spideruci.analysis.statik.flow.Accumulator;

public class Graph<G> {
  
  public static final String START = "START";
  public static final String END = "END";
  private final String uid;
  private Node<G> startNode;
  private Node<G> endNode;
  private HashMap<String, Node<G>> nodes;
  
  public static <T> Graph<T> createEmptyGraph() {
    return new Graph<>();
  }
  
  public static <T> Graph<T> create() {
    Graph<T> graph = new Graph<T>();
    graph.startNode = Node.<T>create(START, graph);
    graph.endNode = Node.<T>create(END, graph);
    graph.nowHas(graph.startNode.and(graph.endNode));
    return graph;
  }
  
  public static <T> Graph<T> create(String startLabel, String endLabel) {
    Graph<T> graph = new Graph<T>();
    graph.startNode = Node.<T>create(startLabel, graph);
    graph.endNode = Node.<T>create(endLabel, graph);
    graph.nowHas(graph.startNode.and(graph.endNode));
    return graph;
  }
  
  private Graph() {
    startNode = null;
    endNode = null;
    nodes = new HashMap<>();
    nodes.put(END, endNode);
    nodes.put(START, startNode);
    uid = UUID.randomUUID().toString();
  }
  
  public Node<G> startNode() {
    return startNode;
  }
  
  public Node<G> endNode() {
    return endNode;
  }
  
  public String uid() {
    return uid;
  }
  
  /*Util Methods*/

  public Collection<Node<G>> getNodes() {
    return nodes.values();
  }

  public Graph<G> startPointsTo(Accumulator<G> accumulator) {
    ArrayList<Node<G>> nodes = accumulator.period();
    if(nodes == null || nodes.size() == 0) return this;
    for(Node<G> node : nodes) {
      startPointsTo(node);
    }
    return this;
  }
  
  public Graph<G> startPointsTo(Node<G> node) {
    if(startNode == null) {
      startNode = Node.<G>create(START, this);
    }
    startNode.pointsTo(node);
    return this;
  }

  public Graph<G> endIsPointedBy(Accumulator<G> accumulator) {
    ArrayList<Node<G>> nodes = accumulator.period();
    if(nodes == null || nodes.size() == 0) return this;
    for(Node<G> node : nodes) {
      endIsPointedBy(node);
    }
    return this;
  }
  
  public Graph<G> endIsPointedBy(Node<G> node) {
    if(endNode == null) {
      endNode = Node.<G>create(END, this);
    }
    node.pointsTo(endNode);
    return this;
  }

  public Graph<G> nowHas(Accumulator<G> accumulator) {
    ArrayList<Node<G>> nodes = accumulator.period();
    if(nodes == null || nodes.size() == 0) return this;
    for(Node<G> node : nodes) {
      nowHas(node);
    }
    return this;
  }

  public Graph<G> nowHas(Node<G> node) {
    if(node == null || this.contains(node.getLabel())) return this;
    this.nodes.put(node.getLabel(), node);
    return this;
  }

  public Graph<G> nowHas(String ... nodeLabels) {
    if(nodeLabels == null || nodeLabels.length == 0) return this;
    for(String nodeLabel : nodeLabels) {
      if(nodeLabel == null) continue;
      this.nowHas(Node.<G>create(nodeLabel, this));
    }
    return this;
  }

  
  public Graph<G> copy() {
    Graph<G> copy = this.createGraphScaffold();
    // 4. set up the successors for the rest of the nodes. 
    for(Node<G> origNode : this.nodes.values()) {
      if(origNode.getLabel().equals(Graph.END)) {
        continue;
      }
      // "original node": the node from the original (this) graph 
      // "new node":  the new node in the copy graph corresponding to original node 
      // 1. get the label of the orignal node 
      String origGraph_node_label = origNode.getLabel();
      // 2. get the node in this graph with the matching label 
      Node<G> newNode = copy.node(origGraph_node_label);
      // 3. get the successors of the original node
      ArrayList<Node<G>> origGraph_node_succs = origNode.pointsTo();
      // 4. for each succesor of the original node
      for(Node<G> node2 : origGraph_node_succs) {
        // 1. get the node in this graph with the matching label
        Node<G> newNode2 = copy.node(node2.getLabel());
        // 2. add the obtained node as the successor of new node
        newNode.pointsTo(newNode2);
      }
    }
    return copy;
  }

  public Graph<G> reverseEdges() {
//    Graph<G> reversedGraph = new Graph<G>(); //= this.createGraphScaffold();
//    
//    for(Node<G> node : this.nodes.values()) {
//      Node<G> newNode = Node.<G>create(node.getLabel(), reversedGraph);
//      reversedGraph.nowHas(newNode); 
//    }
//    
//    reversedGraph.endNode = reversedGraph.node(END);
//    reversedGraph.startNode = reversedGraph.node(START);
    
    Graph<G> reversedGraph = this.createGraphScaffold();
    
    reversedGraph.startNode.clearSuccessors();

    for(Node<G> node : this.nodes.values()) {
      ArrayList<Node<G>> succs = node.pointsTo();
      if(succs == null || succs.size() == 0) continue;
      for(Node<G> succ : succs) {
        Node<G> temp = reversedGraph.node(succ.getLabel());
        temp.pointsTo(reversedGraph.node(node.getLabel()));
      }
    }

    checkState(Graph.START.equals(reversedGraph.startNode.getLabel()));
    checkState(Graph.END.equals(reversedGraph.endNode.getLabel()));

    Node<G> temp = reversedGraph.startNode;
    reversedGraph.startNode = reversedGraph.endNode;
    reversedGraph.endNode = temp;

    checkState(Graph.END.equals(reversedGraph.startNode.getLabel()));
    checkState(Graph.START.equals(reversedGraph.endNode.getLabel()));

    //		retGraph.endNode.label = Graph.END;
    //		retGraph.startNode.label = Graph.START;

    return reversedGraph;
  }
  
      private Graph<G> createGraphScaffold() {
        Graph<G> scaffold = Graph.<G>create();
        // 1. Create a new empty arraylist
        scaffold.nodes = new HashMap<String, Node<G>>();
        // 2. Copy the nodes of the original graph into the arraylist
        for(Node<G> node : this.nodes.values()) {
          // 1. the constructor creates bare nodes with just the labels no succs
          Node<G> newNode = Node.<G>create(node.getLabel(), scaffold);
          // 2. Add the newly created node in the array.
          scaffold.nodes.put(node.getLabel(), newNode);
        }
        // "matching label": the string that matches the label of the original node
        // 3. point the root of the new Graph object to a node in the node list of the new 
        // Graph, for a node that has a matching label.
        //      Node origGraph_root_succ = origGraph.startNode.getSuccessor(0);
        //      Node newGraph_root_succ = this.getNode(origGraph_root_succ.label);
        //      this.startNode.addSuccessors(newGraph_root_succ);
        scaffold.endNode = scaffold.node(END);
        scaffold.startNode = scaffold.node(START);
        return scaffold;
      }

  /**
   * @param label
   * @return Node object - whose label.equals(input);
   * null - if a Node object with matching label does not exist.
   */
  public Node<G> node(String label) {
    return nodes.get(label);
  }

  /**
   * @param label
   * @return 
   * true if there is some node n, 
   * s.t. label.equals(n.label)
   * false, otherwise
   */
  public boolean contains(String label) {
    return nodes.containsKey(label) && nodes.get(label) != null;
  }

  public ArrayList<String> getLabels() {
    return new ArrayList<String>(this.nodes.keySet());
  }

  public static <T> ArrayList<String> getLabels(ArrayList<Node<T>> nodes) {
    ArrayList<String> labels = new ArrayList<String>();
    for(Node<T> node : nodes) {
      labels.add(node.getLabel());
    }
    return labels;
  }

  @Override
  public String toString() {
    StringBuilder strBuilder = new StringBuilder();
    for(Node<G> node : nodes.values()) {
      if(node == null || node.pointsTo() == null || 
          node.pointsTo().size() == 0) continue;
      for(Node<G> succ : node.pointsTo()) {
        if(succ == null) continue;
        strBuilder.append(node.getLabel());
        strBuilder.append(" -> ");
        strBuilder.append(succ.getLabel());
        strBuilder.append(";");
      }
    }
    return strBuilder.toString();
  }
  
  public HashMap<String, ArrayList<String>> toMap() {
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    for(Node<G> node: nodes.values()) {
      if(node == null || node.pointsTo() == null || node.pointsToNone()) 
        continue;
      ArrayList<String> nodeStrings = new ArrayList<String>();
      for(Node<G> succ : node.pointsTo()) {
        if(succ == null) continue;
        nodeStrings.add(succ.getLabel());
      }
      map.put(node.getLabel(), nodeStrings);
    }
    return map;
  }

  /*Graph Analysis Methods*/

}