package org.spideruci.analysis.statik.controlflow;

import static org.spideruci.analysis.util.caryatid.Helper.Collections2.emptyArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import com.google.common.base.Preconditions;

public class Algorithms {
  
  public static <T> ArrayList<Node<T>> randomWalkBack(Node<T> initialNode) {
    ArrayList<Node<T>> randomWalkBack = new ArrayList<Node<T>>();
    Node<T> currentNode = initialNode;
    randomWalkBack.add(currentNode);
    while(!currentNode.isPointedByNone()) {
      ArrayList<Node<T>> predecessors = currentNode.getPredecessors();
      currentNode = predecessors.get(new Random().nextInt(predecessors.size()));
      randomWalkBack.add(currentNode);
    }
    return randomWalkBack;
  }
  
  public static <T> ArrayList<Node<T>> traverseReversePostOrder(Node<T> root) {
    return reverseTraversalOrder(traversePostOrder(root));
  }
  
  public static <T> ArrayList<Node<T>> traversePostOrder(Node<T> root) {
    if(root == null || root.pointsTo() == null || root.pointsToNone()) {
      return emptyArrayList();
    }
    
    Stack<Node<T>> stack  = new Stack<>();
    HashSet<Node<T>> visited = new HashSet<>();
    ArrayList<Node<T>> traversal = new ArrayList<>();
    stack.push(root);
    visited.add(root);
    
    while(!stack.isEmpty()) {
      Node<T> top = stack.peek();
      
      Node<T> unvisitedNeighbour = null;
      for(Node<T> item : top.pointsTo()) {
        if(!visited.contains(item)) {
          unvisitedNeighbour = item;
          break;
        }
      }
      
      if(unvisitedNeighbour != null) {
        visited.add(unvisitedNeighbour);
        stack.push(unvisitedNeighbour);
      } else {
        Node<T> popped = stack.pop();
        traversal.add(popped);
      }
    }
    
    return traversal;
  }
  
  public static <T> ArrayList<Node<T>> reverseTraversalOrder(ArrayList<Node<T>> trav) {
    if(trav == null || trav.isEmpty()) 
      return emptyArrayList();
    
    ArrayList<Node<T>> revTrav = new ArrayList<>(trav);
    Collections.reverse(revTrav);
    return revTrav;
  }
  
  public static <T> ArrayList<Node<T>> traverseBfs(Graph<T> graph) {
    ArrayList<Node<T>> bfsTraversal = new ArrayList<>();
    
    LinkedList<Node<T>> qu = new LinkedList<>();
    qu.addLast(graph.startNode());
    
    while(!qu.isEmpty()) {
      Node<T> front = qu.removeFirst();
      
      if(front == null)
        continue;
      
      bfsTraversal.add(front);
      
      ArrayList<Node<T>> succs = front.pointsTo();
      
      for(Node<T> succ : succs) {
        if(bfsTraversal.contains(succ))
          continue;
        
        qu.addLast(succ);
      }
    }
    
    return bfsTraversal;
  }
  
  public static <T> ArrayList<Node<T>> traverseDfs(Graph<T> graph) {
    ArrayList<Node<T>> dfsTraversal = new ArrayList<>();
    
    Stack<Node<T>> stack = new Stack<>();
    stack.push(graph.startNode());
    
    while(!stack.isEmpty()) {
      Node<T> popped = stack.pop();
      
      if(popped == null)
        continue;
      
      dfsTraversal.add(popped);
      
      ArrayList<Node<T>> succs = popped.pointsTo();
      
      for(Node<T> succ : succs) {
        if(dfsTraversal.contains(succ))
          continue;
        
        stack.push(succ);
      }
    }
    
    return dfsTraversal;
  }
  
  public static <T> ArrayList<Node<T>> traverseReversePostOrder(Graph<T> graph) {
    Graph<T> spanningTree = getDfsSpanningTree(graph);
    return traverseBfs(spanningTree);
  }
  
  public static <T> Graph<T> getDfsSpanningTree(Graph<T> flowGraph) {
    validateGraph(flowGraph);
    
    Graph<T> tree = Graph.createEmptyGraph();
    
    Stack<Node<T>> stack = new Stack<>();
    stack.push(flowGraph.startNode());
    
    while(!stack.isEmpty()) {
      Node<T> popped = stack.pop();
      if(popped == null)
        continue;
      
      final String poppedLabel = popped.getLabel();
      
      Node<T> poppedInTree = null;
      if(tree.contains(poppedLabel)) {
        poppedInTree = tree.node(poppedLabel);
      } else {
        poppedInTree = Node.create(poppedLabel, tree);
        tree.nowHas(poppedInTree);
      }

      ArrayList<Node<T>> succs = popped.pointsTo();
      for(Node<T> succ : succs) {
        final String succLabel = succ.getLabel();
        
        if(tree.contains(succLabel)) {
          continue;
        }
        
        Node<T> succInTree = Node.create(succLabel, tree);
        tree.nowHas(succInTree);
        poppedInTree.pointsTo(succInTree);
//        succInTree.po
        
        stack.push(succ);
      }
      
    }
    
    return tree;
  }
  
  public static <T> HashMap<Node<T>, Node<T>> getStronglyConnectedComponents(Graph<T> flowGraph) {
    validateGraph(flowGraph);
    
    Node<T> start = flowGraph.startNode();
    ArrayList<Node<T>> postOrderTraversal = traversePostOrder(start);
    
    HashMap<Node<T>, Node<T>> ringleaders = new HashMap<>();
    
    for(int i = postOrderTraversal.size() - 1; i >= 0; i -= 1) {
      Node<T> leader = postOrderTraversal.get(i);
      if(ringleaders.get(leader) != null) {
        continue;
      }
      
      Stack<Node<T>> stack = new Stack<>();
      stack.push(leader);
      while(!stack.isEmpty()) {
        Node<T> popped = stack.pop();
        if(popped == null) {
          continue;
        }
        
        if(ringleaders.get(popped) == null) {
          ringleaders.put(popped, leader);
        }
        
        ArrayList<Node<T>> preds = popped.getPredecessors();
        for(Node<T> pred : preds) {
          if(ringleaders.get(pred) == null) {
            stack.push(pred);
          }
        }
        
      }
    }
    
    return ringleaders;
  }
  
  /**
   * 
   * @param flowGraph
   * @return dictionary of nodes, mapped to their leaders for all the 
   * identified strongly connected components.
   */
  public static <T> HashMap<Node<T>, Node<T>> collapseStrongComponents(Graph<T> flowGraph) {
    validateGraph(flowGraph);
    
    HashMap<Node<T>, Node<T>> ringleaders = 
        getStronglyConnectedComponents(flowGraph);
    
    for(Node<T> node : ringleaders.keySet()) {
      Node<T> leader = ringleaders.get(node);
      if(node == leader) {
        continue;
      }

      ArrayList<Node<T>> succs = node.pointsTo();
      for(Node<T> succ : succs) {
        if(succ == null) 
          continue;
        Node<T> succLeader = ringleaders.get(succ);

        if(succLeader == leader)
          continue;

        if(!leader.containsSuccessor(succLeader.getLabel())) {
          leader.pointsTo(succLeader);
        }
      }
      
      ArrayList<Node<T>> preds = node.getPredecessors();
      for(Node<T> pred : preds) {
        if(pred == null) 
          continue;
        
        Node<T> predLeader = ringleaders.get(pred);
        
        if(predLeader == leader)
          continue;
        
        if(!predLeader.containsSuccessor(leader.getLabel())) {
          predLeader.pointsTo(leader);
        }
      }
      
      node.clearPredecessors();
      node.clearSuccessors();
    }
    
    return ringleaders;
  }
  

  /**
   * This method does not account for any strongly connected components 
   * in the input flowGraph. To avoid any issues due to SCCs (strongly 
   * connected components) use the method {@link Algorithms.collapseStrongComponents} 
   * to collapse the SCCs before calling this method. 
   * @param flowGraph
   * @return
   */
  public static <T> ArrayList<Node<T>> bottomUpTopologicalSort(Graph<T> flowGraph) {
    validateGraph(flowGraph);
    
    ArrayList<Node<T>> topologicalSort = new ArrayList<>();
    LinkedList<Node<T>> qu = new LinkedList<>(); 
    
    for(Node<T> node : flowGraph.getNodes()) {
      if(node.pointsToNone()) {
        qu.addLast(node);
      }
    }
    
    while(!qu.isEmpty()) {
      Node<T> front = qu.removeFirst();
      
      if(front == null)
        continue;
      
      topologicalSort.add(front);
      
      ArrayList<Node<T>> preds = front.getPredecessors();
      front.clearPredecessors();
      for(Node<T> pred : preds) {
        if(pred.pointsToNone()) {
          qu.addLast(pred);
        }
      }
      
    }
    
    return topologicalSort;
  }
  
  /**
   * 
   * @param flowGraph The control flow graph for which a dominator tree is constructed and returned.
   * @return The Dominatory Tree <a href="https://en.wikipedia.org/wiki/Dominator_(graph_theory)">(ref)</a>
   * for the input {@code flowgraph}.
   * @throws @{@link IllegalArgumentException} when: <br>
   * <li> {@code flowgraph} is null
   * <li> {@code flowgraph} is empty (lacks start and end nodes);
   * <li> START and/or END nodes are null
   * <li> START points to nothing
   * <li> END is pointed by nothing
   * 
   * @throws @{@link IllegalStateException} when Reverse post order traversal's 
   * node-count does not match {@code flowgraph}'s node count, typically because
   * the input {@code flowgraph} is actually a forest.
   */
  public static <T> Graph<T> getDominatorTree(Graph<T> flowGraph) {
    validateGraph(flowGraph);
    
    Node<T> root = flowGraph.startNode();
    ArrayList<Node<T>> revPostOrder = traverseReversePostOrder(root);
    Preconditions.checkState(flowGraph.getNodes().size() == revPostOrder.size(),
        flowGraph +  " " + revPostOrder);
    
    Graph<T> domTree = Graph.createEmptyGraph();
    String rootLabel = root.getLabel();
    Node<T> domTreeRoot = Node.create(rootLabel, domTree);
    domTree.nowHas(domTreeRoot);
    
    for(int i = 1; i < revPostOrder.size(); i += 1) {
      Node<T> flowNode = revPostOrder.get(i);
      
      ArrayList<Node<T>> flowPreds = flowNode.getPredecessors();
      
      Node<T> iDom = null;
      
      for(Node<T> flowPred : flowPreds) {
        String flowPredLabel = flowPred.getLabel();
        if(!domTree.contains(flowPredLabel))
          continue;
        
        // get the predecessor's idom
        Node<T> predInDomTree = domTree.node(flowPredLabel);
        iDom = (iDom == null) ? predInDomTree : lowestCommonAncestor(iDom, predInDomTree);
//        
//        Node<T> predIdom;
//        if(nodeInDomTree == domTreeRoot) {
//          predIdom = domTreeRoot;
//        } else {
//          ArrayList<Node<T>> idoms = nodeInDomTree.getPredecessors();
//          Preconditions.checkState(idoms.size() == 1, idoms.size());
//          predIdom = idoms.get(0);
//        }
        
        
      }
      
      String label = flowNode.getLabel();
      Node<T> newDomNode = Node.create(label, domTree);
      domTree.nowHas(newDomNode);
      iDom.pointsTo(newDomNode);
    }
    
    return domTree;
  }

  private static <T> void validateGraph(Graph<T> flowGraph) {
    Preconditions.checkArgument(flowGraph != null, "flowgraph is null");
    Preconditions.checkArgument(!flowGraph.getNodes().isEmpty(), "flowgraph is empty");
    Preconditions.checkArgument(flowGraph.startNode() != null, "flowgraph's start is null");
    Preconditions.checkArgument(flowGraph.endNode() != null, "flowgraph's end is null");
    Preconditions.checkArgument(flowGraph.startNode().getSuccessorsSize() != 0, 
        "flowgraph's start points to nothing");
    Preconditions.checkArgument(flowGraph.startNode().getSuccessorsSize() != 0,
        "nothing points to flowgraph's end");
  }
  
  /**
   * Finds the lowest common ancestor for the input Nodes n1 and n2.
   * Assumes that the nodes are a part of the same TREE, not GRAPH.
   * I.E., each node only as a single predecessor.
   * Given the TREE-assumption, while traversing parents of the nodes, it will
   * pick a node's first predecessor.
   * @param n1
   * @param n2
   * @return The lowest common ancestor for the input nodes
   * @throws @{@link IllegalStateException} in case {@code n1} and/or {@code n2} <br> 
   * <li> are null;
   * <li> do not belong to the same tree; or 
   * <li> if they do belong to a graph instead of a tree.  
   */
  public static <T> Node<T> lowestCommonAncestor(Node<T> n1, Node<T> n2) {
    Preconditions.checkState(n1 != null && n2 != null);
    Preconditions.checkState(n1.getContainerGraph() == n2.getContainerGraph());
    
    ArrayList<Node<T>> n1ToRoot = new ArrayList<>();
    ArrayList<Node<T>> n2ToRoot = new ArrayList<>();
    
    for(Node<T> ptr = n1; ptr != null; ptr = ptr.getParentInTree()) {
      if(ptr == n2)
        return n2;
      
      n1ToRoot.add(ptr);
    }
    
    for(Node<T> ptr = n2; ptr != null; ptr = ptr.getParentInTree()) {
      if(ptr == n1)
        return n1;
      
      n2ToRoot.add(ptr);
    }
    
    Node<T> lca = null;
    
    for(int i = n1ToRoot.size() - 1, j = n2ToRoot.size() - 1;
        j >= 0 && i >= 0 && n1ToRoot.get(i) == n2ToRoot.get(j); 
        i -= 1, j -= 1) {
      lca = n1ToRoot.get(i);
    }
    
    return lca;
  }
  
  @Deprecated
  public static <T> Graph<T> computeDomTree(Node<T> root) {
    
    ArrayList<Node<T>> postOrder = traversePostOrder(root);
    if(postOrder.isEmpty()) {
      return null;
    }
    
    ArrayList<Node<T>> revPostOrder = reverseTraversalOrder(postOrder);
    
    Graph<T> domTree = Graph.createEmptyGraph();
    
    for(Node<T> node : revPostOrder) {
      domTree.nowHas(Node.<T>create(node.getLabel(), domTree));
    }
    
    root.cloneIn(domTree).pointsTo(root.cloneIn(domTree));
    
    boolean domTreeChanged = true;
    
    
    HashMap<String, Integer> postOrderPositions = new HashMap<>();
    for(int i = 0; i < postOrder.size(); i += 1) {
      postOrderPositions.put(postOrder.get(i).getLabel(), i);
    }
    
    ArrayList<Node<T>> revPostOrderMinusStart = new ArrayList<>(revPostOrder);
    revPostOrderMinusStart.remove(root);
    
    while(domTreeChanged) {
      domTreeChanged = false;
      for(Node<T> node : revPostOrderMinusStart) {
        ArrayList<Node<T>> preds = node.getPredecessors();
        Node<T> newIdom = preds.get(0);
        for(int i = 1; i < preds.size(); i += 1) {
          Node<T> pred = preds.get(i);
          Node<T> predIdom = pred.cloneIn(domTree).pointsToNone() ? null : pred.cloneIn(domTree).pointsTo().get(0);
          if(predIdom == null) continue;
          newIdom = lca(pred.cloneIn(domTree), newIdom.cloneIn(domTree), postOrderPositions);
        }
        Node<T> nodeIdom = node.cloneIn(domTree).pointsToNone() ? null 
                                    : node.cloneIn(domTree).pointsTo().get(0);
        if(nodeIdom == newIdom.cloneIn(domTree)) continue;
        node.cloneIn(domTree).clearSuccessors();
        node.cloneIn(domTree).pointsTo(newIdom.cloneIn(domTree));
        domTreeChanged = true;
      }
    }
    
    root.cloneIn(domTree).clearPredecessors().clearSuccessors();
    return domTree;
  }
  
      @Deprecated
      private static <T> Node<T> lca(Node<T> b1, 
                          Node<T> b2, 
                          HashMap<String, Integer> positions) {
        Node<T> finger1 = b1;
        Node<T> finger2 = b2;
        while(finger1 != finger2) {
          while(positions.get(finger1.getLabel()) 
                  < positions.get(finger2.getLabel())) {
            if(finger1.pointsToNone()) break;
            finger1 = finger1.pointsTo().get(0);
          }
          while(positions.get(finger2.getLabel()) 
                  < positions.get(finger1.getLabel())) {
            if(finger2.pointsToNone()) break;
            finger2 = finger2.pointsTo().get(0);  
          }
          if(finger1.pointsToNone() || finger2.pointsToNone()) break;
        }
        return finger1;
      }
}