package org.spideruci.analysis.statik.controlflow;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.spideruci.jump.Graphs;
import org.spideruci.jump.Traversal;

public class BottomUpTopologicalSortTest {
  
  private void computeTopologyAndExpect(
      // given
      Graph<Integer> flowGraph, 
      Traversal<Node<Integer>> expectedBottomUpTopology) {
    
    // when
    ArrayList<Node<Integer>> bottomUpTopology = 
        Algorithms.bottomUpTopologicalSort(flowGraph);
    
    // then
    expectedBottomUpTopology.compareWith(bottomUpTopology);
  }

  @Test
  public void testJustStartAndEnd() {
    Graph<Integer> flow = Graphs.justStartAndEnd();
    
    Traversal<Node<Integer>> expectedBottomUpTopology = 
        Traversal.add(flow.endNode())
        .and(flow.startNode());
    
    computeTopologyAndExpect(flow, expectedBottomUpTopology);
  }
  
  @Ignore
  @Test
  public void testStartAndEndInACycle() {
    Graph<Integer> flow = Graphs.startAndEndWithACycle();
    
    Algorithms.collapseStrongComponents(flow);
    ArrayList<Node<Integer>> bottomUpTopology = 
        Algorithms.bottomUpTopologicalSort(flow);
    
    System.out.println(bottomUpTopology);
    
    Traversal<Node<Integer>> expectedBottomUpTopology = 
        Traversal.addInAnyOrder(flow.startNode().and(flow.endNode()).period());
    
    computeTopologyAndExpect(flow, expectedBottomUpTopology);
  }
  
  @Test
  public void testSimpleIfStructure() {
    Graph<Integer> flow = Graphs.simpleIfStructure();
    
    Traversal<Node<Integer>> expectedBottomUpTopology = 
        Traversal.add(flow.endNode())
        .and(flow.node("next"))
        .and(flow.node("join"))
        .and(flow.node("then"))
        .and(flow.node("branch"))
        .and(flow.startNode());
    
    computeTopologyAndExpect(flow, expectedBottomUpTopology);
  }
  
  @Test
  public void testSimpleIfElseStructure() {
    Graph<Integer> flow = Graphs.simpleIfElseStructure();
    
    Traversal<Node<Integer>> expectedBottomUpTopology = 
        Traversal.add(flow.endNode())
        .and(flow.node("next"))
        .and(flow.node("join"))
        .inAnyOrder(
            flow.node("then")
            .and(flow.node("else"))
            .period())
        .and(flow.node("branch"))
        .and(flow.startNode());
    
    computeTopologyAndExpect(flow, expectedBottomUpTopology);
  }
  
  @Test
  public void testBinaryTree() {
    Graph<Integer> flow = Graphs.simpleBinaryTree();
    
    Traversal<Node<Integer>> expectedBottomUpTopology = 
        Traversal.addInAnyOrder(
            flow.node("1").and(flow.node("4")).and(flow.node("6")).andAlso(flow.endNode()))
        .inAnyOrder(
            flow.node("5").and(flow.node("2")).period())
        .and(flow.node("3"))
        .and(flow.startNode());
    
    computeTopologyAndExpect(flow, expectedBottomUpTopology);
  }

}
