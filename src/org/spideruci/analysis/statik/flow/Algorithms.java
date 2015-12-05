package org.spideruci.analysis.statik.flow;

import static org.spideruci.analysis.util.caryatid.Helper.Collections2.emptyArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

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
    if(trav == null || trav.isEmpty()) return emptyArrayList();
    ArrayList<Node<T>> revTrav = new ArrayList<>(trav);
    Collections.reverse(revTrav);
    return revTrav;
  }
  
  public static <T> Graph<T> computeDomTree(Node<T> root) {
    Graph<T> domTree = Graph.createEmptyGraph();
    ArrayList<Node<T>> postOrder = traversePostOrder(root);
    if(postOrder.isEmpty()) {
      return null;
    }
    
    ArrayList<Node<T>> revPostOrder = reverseTraversalOrder(postOrder);
    
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