package org.spideruci.analysis.statik.controlflow;

import static org.junit.Assert.*;
import static org.spideruci.analysis.statik.controlflow.StronglyConnectedComponentsTest.assertExpectedAndActualLeadersAreSame;

import java.util.HashMap;

import org.junit.Test;
import org.spideruci.jump.GraphAssertions;
import org.spideruci.jump.Graphs;

public class CollapsingStrongComponentsTest {

  @Test
  public void collapsingStartAndEndWithCycleShouldLeaveStartAlone() {
    // given
    Graph<Integer> flowGraph = Graphs.startAndEndWithACycle();
    Node<Integer> startNode = flowGraph.startNode();
    Node<Integer> endNode = flowGraph.endNode();

    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    expectedLeaders.put(startNode, startNode);
    expectedLeaders.put(endNode, startNode);

    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders =
        Algorithms.collapseStrongComponents(flowGraph);

    // then
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);

    // and
    assertTrue(startNode.pointsToNone() && startNode.isPointedByNone());
    assertTrue(endNode.pointsToNone() && endNode.isPointedByNone());
  }

  @Test
  public void collapsingSscWithThreeNodesShouldLeaveStartAlone() {
    // given
    Graph<Integer> flowGraph = Graphs.threeNodesWithACycle();

    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();

    Node<Integer> startNode = flowGraph.startNode();
    Node<Integer> endNode = flowGraph.endNode();
    Node<Integer> node = flowGraph.node("node");

    expectedLeaders.put(startNode, startNode);
    expectedLeaders.put(node, startNode);
    expectedLeaders.put(endNode, startNode);

    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.collapseStrongComponents(flowGraph);

    // then
    assertTrue(startNode.pointsToNone() && startNode.isPointedByNone());
    assertTrue(endNode.pointsToNone() && endNode.isPointedByNone());
    assertTrue(node.pointsToNone() && node.isPointedByNone());

    // and
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
  }
  
  @Test
  public void collapseLoopBodyIntoHeader() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleLoopStruture();
    
    Node<Integer> head = flowGraph.node("head");
    Node<Integer> body = flowGraph.node("body");
    Node<Integer> tail = flowGraph.node("tail");
    
    System.out.println(flowGraph);
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();
    
    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());
    expectedLeaders.put(head, head);
    expectedLeaders.put(body, head);
    expectedLeaders.put(tail, tail);
    expectedLeaders.put(flowGraph.endNode(), flowGraph.endNode());
    
    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.collapseStrongComponents(flowGraph);
    
    // then
    GraphAssertions.assert1pointsTo2(Graph.START, "head", flowGraph);
    GraphAssertions.assert1pointsTo2("head", "tail", flowGraph);
    GraphAssertions.assert1pointsTo2("tail", Graph.END, flowGraph);
    GraphAssertions.assertNodeIsSolitary(body);
    
    // and
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
    
    System.out.println(flowGraph);
  }

  @Test
  public void collapseShouldJoinStart_One_Nine_EightAndLeaveRestAlone() {
    // given
    Graph<Integer> flowGraph = Graphs.threeStronglyConnectedComponents();
    Node<Integer> node1 = flowGraph.node("1");
    Node<Integer> node8 = flowGraph.node("8");
    Node<Integer> node9 = flowGraph.node("9");
    
    System.out.println(flowGraph + "\n");
    
    // and
    HashMap<Node<Integer>, Node<Integer>> expectedLeaders = new HashMap<>();

    expectedLeaders.put(flowGraph.startNode(), flowGraph.startNode());

    expectedLeaders.put(node1, node1);
    expectedLeaders.put(flowGraph.node("4"), node1);
    expectedLeaders.put(flowGraph.node("7"), node1);

    expectedLeaders.put(node8, node8);
    expectedLeaders.put(flowGraph.node("2"), node8);
    expectedLeaders.put(flowGraph.node("5"), node8);

    expectedLeaders.put(node9, node9);
    expectedLeaders.put(flowGraph.node("3"), node9);
    expectedLeaders.put(flowGraph.node("6"), node9);

    // when
    HashMap<Node<Integer>, Node<Integer>> actualLeaders = 
        Algorithms.collapseStrongComponents(flowGraph);

    // then
    GraphAssertions.assert1pointsTo2(Graph.START, "1", flowGraph);
    GraphAssertions.assert1pointsTo2("1", "9", flowGraph);
    GraphAssertions.assert1pointsTo2("9", "8", flowGraph);
    
    // and
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("4"));
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("7"));
    
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("2"));
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("5"));
    
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("3"));
    GraphAssertions.assertNodeIsSolitary(flowGraph.node("6"));
    
    // and
    assertExpectedAndActualLeadersAreSame(actualLeaders, expectedLeaders);
    
    System.out.println(flowGraph);
  }

}
