package org.spideruci.analysis.statik.controlflow;

import java.util.ArrayList;

import org.junit.Test;
import org.spideruci.jump.Graphs;
import org.spideruci.jump.Traversal;

public class PostOrderTraversalTest {
  
  private void traverseAndExpect(
    /*given:*/
    Node<Integer> root, 
    Traversal<Node<Integer>> expectedTraversal) {
    
    // when
    ArrayList<Node<Integer>> actualTraversal = Algorithms.traversePostOrder(root);
    
    // then
    expectedTraversal.compareWith(actualTraversal);
  }
  
  @Test
  public void nullRootShouldReturnEmptyTraversal() {
    Node<Integer> root = null;
    Traversal<Node<Integer>> expectedTraversal = new Traversal<>();
    
    traverseAndExpect(root, expectedTraversal);
  }
  
  @Test
  public void testJustStartAndEnd() {
    Graph<Integer> flow = Graphs.justStartAndEnd();
    
    Traversal<Node<Integer>> expectedTraversal = 
        Traversal.add(flow.endNode()).and(flow.startNode());
    
    traverseAndExpect(flow.startNode(), expectedTraversal);
  }
  
  @Test
  public void testIfStructure() {
    Graph<Integer> flow = Graphs.simpleIfStructure();
    
    Traversal<Node<Integer>> expectedTraversal = 
        Traversal.add(flow.endNode())
        .and(flow.node("next"))
        .and(flow.node("join"))
        .and(flow.node("then"))
        .and(flow.node("branch"))
        .and(flow.startNode());
    
    traverseAndExpect(flow.startNode(), expectedTraversal);
  }
  
  @Test
  public void testIfElseStructure() {
    Graph<Integer> flow = Graphs.simpleIfElseStructure();
    
    Traversal<Node<Integer>> traversal = Traversal.add(flow.endNode())
        .and(flow.node("next"))
        .and(flow.node("join"))
        .inAnyOrder(
            flow.node("else")
            .and(flow.node("then"))
            .period())
        .and(flow.node("branch"))
        .and(flow.startNode());
    
    traverseAndExpect(flow.startNode(), traversal);
  }
  
  @Test
  public void testLoopStructure() {
    Graph<Integer> flow = Graphs.simpleLoopStruture();
    
    Traversal<Node<Integer>> expectedTraversal = Traversal.add(flow.endNode())
        .inAnyOrder(
            flow.node("body")
            .and(flow.node("tail"))
            .period())
        .and(flow.node("head"))
        .and(flow.startNode());
    
    traverseAndExpect(flow.startNode(), expectedTraversal);
  }
  
  @Test
  public void testFisherGraph() {
    Graph<Integer> flow = Graphs.fisher();
    
    Traversal<Node<Integer>> expectedTraversal = 
        Traversal.add(flow.endNode())
        .and(flow.node("f"))
        .and(flow.node("e"))
        .and(flow.node("d"))
        .inAnyOrder(
            flow.node("b")
            .and(flow.node("c"))
            .period())
        .and(flow.node("a"))
        .and(flow.startNode());
        ;
    
    traverseAndExpect(flow.startNode(), expectedTraversal);
  }

  @Test
  public void testBinaryTree() {
    Graph<Integer> flow = Graphs.simpleBinaryTree();
    
    Traversal<Node<Integer>> expectedTraversal = 
        Traversal.addInAnyOrder(
            flow.node("1")
            .and(flow.node("4"))
            .period())
        .and(flow.node("5"))
        .and(flow.node("6"))
        .and(flow.node("2"))
        .and(flow.node("3"))
        .and(flow.startNode());
        ;
    
    traverseAndExpect(flow.startNode(), expectedTraversal);
  }
}
