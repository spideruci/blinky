package org.spideruci.analysis.statik.controlflow;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import org.spideruci.jump.Graphs;

public class StronglyConnectedComponentsTest {

  private void assesrtNoStronglyConnectedComponents(
    // given
    Graph<Integer> flowGraph) {
    
    // when
    HashMap<Node<Integer>, Node<Integer>> leaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    for(Node<Integer> node : leaders.keySet()) {
      Node<Integer> leader = leaders.get(node);
      assertEquals(node, leader);
    }
  }
  
  public static void assertExpectedAndActualLeadersAreSame(
      HashMap<Node<Integer>, Node<Integer>> actualLeaders,
      HashMap<Node<Integer>, Node<Integer>> expectedLeaders) {
    
    for(Node<Integer> node : actualLeaders.keySet()) {
      Node<Integer> actualLeader = actualLeaders.get(node);
      Node<Integer> expectedLeader = expectedLeaders.get(node);
      assertEquals("For node: " + node, expectedLeader, actualLeader);
    }
  }
  
  @Test
  public void testJustStartAndEnd() {
    assesrtNoStronglyConnectedComponents(Graphs.justStartAndEnd());
  }

  @Test
  public void testIfStructure() {
    assesrtNoStronglyConnectedComponents(Graphs.simpleIfElseStructure());
  }
  
  @Test
  public void testBinaryTree() {
    assesrtNoStronglyConnectedComponents(Graphs.simpleBinaryTree());
  }
  
  @Test
  public void testIfElseStructure() {
    assesrtNoStronglyConnectedComponents(Graphs.simpleIfElseStructure());
  }

  @Test
  public void testLoopStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleLoopStruture();
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.node("head"), flowGraph.node("head"));
    expectedLeaders.put(flowGraph.node("body"), flowGraph.node("head"));
    expectedLeaders.put(flowGraph.node("tail"), flowGraph.node("tail"));
    expectedLeaders.put(flowGraph.endNode(), flowGraph.endNode());
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void testGraphFromFisher() {
    // given
    Graph<Integer> flowGraph = Graphs.fisher();
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.node("a"), flowGraph.node("a"));
    expectedLeaders.put(flowGraph.node("b"), flowGraph.node("b"));
    expectedLeaders.put(flowGraph.node("c"), flowGraph.node("c"));
    expectedLeaders.put(flowGraph.node("d"), flowGraph.node("d"));
    expectedLeaders.put(flowGraph.node("e"), flowGraph.node("e"));
    expectedLeaders.put(flowGraph.node("f"), flowGraph.node("e"));
    expectedLeaders.put(flowGraph.endNode(), flowGraph.endNode());
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void testBinaryTreeWithBackEdges() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleBinaryTree();
    
    // and
    flowGraph.node("1").pointsTo(
        flowGraph.node("3")
        .and(flowGraph.node("5"))
        .and(flowGraph.node("4")));
    flowGraph.node("6").pointsTo(flowGraph.node("2"));
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.node("1"), flowGraph.node("3"));
    expectedLeaders.put(flowGraph.node("2"), flowGraph.node("2"));
    expectedLeaders.put(flowGraph.node("3"), flowGraph.node("3"));
    expectedLeaders.put(flowGraph.node("4"), flowGraph.node("4"));
    expectedLeaders.put(flowGraph.node("5"), flowGraph.node("3"));
    expectedLeaders.put(flowGraph.node("6"), flowGraph.node("2"));
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void testThreeStronglyConnectedComponents() {
    // given
    Graph<Integer> flowGraph = Graphs.threeStronglyConnectedComponents();
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.node("1"), flowGraph.node("1"));
    expectedLeaders.put(flowGraph.node("4"), flowGraph.node("1"));
    expectedLeaders.put(flowGraph.node("7"), flowGraph.node("1"));

    expectedLeaders.put(flowGraph.node("8"), flowGraph.node("8"));
    expectedLeaders.put(flowGraph.node("2"), flowGraph.node("8"));
    expectedLeaders.put(flowGraph.node("5"), flowGraph.node("8"));

    expectedLeaders.put(flowGraph.node("9"), flowGraph.node("9"));
    expectedLeaders.put(flowGraph.node("3"), flowGraph.node("9"));
    expectedLeaders.put(flowGraph.node("6"), flowGraph.node("9"));
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void testJustStartAndEndWithACycle() {
    // given
    Graph<Integer> flowGraph = Graphs.startAndEndWithACycle();
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.endNode(), flowGraph.startNode());
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void testThreeNodesWithOneCycle() {
    // given
    Graph<Integer> flowGraph = Graphs.threeNodesWithACycle();
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(flowGraph.node("node"), flowGraph.startNode());
    expectedLeaders.put(flowGraph.endNode(), flowGraph.startNode());
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.getStronglyConnectedComponents(flowGraph);
    
    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }

}
