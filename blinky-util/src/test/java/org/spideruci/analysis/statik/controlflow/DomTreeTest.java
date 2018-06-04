package org.spideruci.analysis.statik.controlflow;

import static org.junit.Assert.*;
import static org.spideruci.jump.GraphAssertions.*;

import java.util.function.BiConsumer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.spideruci.jump.Graphs;

public class DomTreeTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void shouldThrowExceptionWithNullFlowGraph() {
    // given
    Graph<Integer> flowGraph = null;
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void shouldReturnThrowupWhenFlowGraphIsEmpty() {
    // given
    Graph<Integer> flowGraph = Graph.createEmptyGraph();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void shouldReturnthrowUpWithNullStartNode() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void endShouldPointToStartInGraphWithNoOtherNodes() {
    // given
    Graph<Integer> flowGraph = Graphs.justStartAndEnd();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assertTrue(domTree.node(Graph.START).pointsTo().contains(domTree.node(Graph.END)));
  }
  
  @Test
  public void testDomTreeForSimpleIfStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleIfStructure();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "branch", domTree);
    assert1pointsTo2("branch", "then", domTree);
    assert1pointsTo2("branch", "join", domTree);
    assert1pointsTo2("join", "next", domTree);
    assert1doesNotPointTo2("next", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "join", domTree);
    assert1doesNotPointTo2(Graph.START, "next", domTree);
  }
  
  @Test
  public void testDomTreeForSimpleIfElseStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleIfElseStructure();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "branch", domTree);
    assert1pointsTo2("branch", "then", domTree);
    assert1pointsTo2("branch", "else", domTree);
    assert1pointsTo2("branch", "join", domTree);
    assert1pointsTo2("join", "next", domTree);
    assert1doesNotPointTo2("next", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "join", domTree);
    assert1doesNotPointTo2(Graph.START, "next", domTree);
  }
  
  @Test
  public void testForSimpleLoopStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleLoopStruture();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "head", domTree);
    assert1pointsTo2("head", "body", domTree);
    assert1pointsTo2("head", "tail", domTree);
    assert1doesNotPointTo2("head", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "tail", domTree);
  }
  
  /**
   * <a href="http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf">http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf
   */
  @Test
  public void testDomTreeFischerLectureExample() {
 // given
    Graph<Integer> flowGraph = Graphs.fisher();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    //then
    System.out.println(domTree);
    
    assert1pointsTo2(Graph.START, "a", domTree);
    assert1pointsTo2("a", "b", domTree);
    assert1pointsTo2("a", "c", domTree);
    assert1pointsTo2("a", "d", domTree);
    assert1pointsTo2("d", "e", domTree);
    assert1pointsTo2("e", "f", domTree);
    assert1pointsTo2("f", Graph.END, domTree);
  }
  
  @Test
  public void testPostDomTreeFischerLectureExample() {
    // given
    Graph<Integer> flowGraph = Graphs.fisher();

    Graph<Integer> revFlowGraph = flowGraph.reverseEdges();
    
    System.out.println(revFlowGraph);

    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(revFlowGraph);

    //then
    System.out.println(domTree);

    assert1pointsTo2("a", Graph.START, domTree);
    assert1pointsTo2("d", "a", domTree);
    assert1pointsTo2("d", "b", domTree);
    assert1pointsTo2("d", "c", domTree);
    assert1pointsTo2("e", "d", domTree);
    assert1pointsTo2("f", "e", domTree);
    assert1pointsTo2(Graph.END, "f", domTree);
  }
  


}
