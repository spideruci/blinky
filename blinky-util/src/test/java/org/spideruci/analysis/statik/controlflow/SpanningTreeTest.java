package org.spideruci.analysis.statik.controlflow;

import static org.spideruci.jump.GraphAssertions.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.spideruci.jump.Graphs;

public class SpanningTreeTest {
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldThrowExceptionWithNullFlowGraph() {
    // given
    Graph<Integer> flowGraph = null;
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDfsSpanningTree(flowGraph);
  }
  
  @Test
  public void shouldReturnThrowupWhenFlowGraphIsEmpty() {
    // given
    Graph<Integer> flowGraph = Graph.createEmptyGraph();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDfsSpanningTree(flowGraph);
  }
  
  @Test
  public void shouldReturnthrowUpWithNullStartNode() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDfsSpanningTree(flowGraph);
  }

  @Test
  public void endShouldPointToStartInGraphWithNoOtherNodes() {
    // given
    Graph<Integer> flowGraph = Graphs.justStartAndEnd();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> spanningTree = Algorithms.getDfsSpanningTree(flowGraph);
    System.out.println("yo");
    System.out.println(spanningTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, spanningTree);
  }

  @Test
  public void testSpanningTreeForSimpleIfStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleIfStructure();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> spanningTree = Algorithms.getDfsSpanningTree(flowGraph);
    
    System.out.println(spanningTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, spanningTree);
    assert1pointsTo2(Graph.START, "branch", spanningTree);
    assert1pointsTo2("branch", "then", spanningTree);
    assert1pointsTo2("branch", "join", spanningTree);
    assert1pointsTo2("join", "next", spanningTree);
    // ...and
    assert1doesNotPointTo2("next", Graph.END, spanningTree);
    assert1doesNotPointTo2(Graph.START, "join", spanningTree);
    assert1doesNotPointTo2(Graph.START, "next", spanningTree);
  }
  
  @Test
  public void testSpanningTreeForSimpleIfElseStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleIfElseStructure();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> spanningTree = Algorithms.getDfsSpanningTree(flowGraph);
    
    System.out.println(spanningTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, spanningTree);
    assert1pointsTo2(Graph.START, "branch", spanningTree);
    assert1pointsTo2("branch", "then", spanningTree);
    assert1pointsTo2("branch", "else", spanningTree);
    assert1pointsTo2("else", "join", spanningTree);
    assert1pointsTo2("join", "next", spanningTree);
    // ...and
    assert1doesNotPointTo2("branch", "join", spanningTree);
    assert1doesNotPointTo2("next", Graph.END, spanningTree);
    assert1doesNotPointTo2(Graph.START, "join", spanningTree);
    assert1doesNotPointTo2(Graph.START, "next", spanningTree);
  }
  
  @Test
  public void testForSimpleLoopStructure() {
    // given
    Graph<Integer> flowGraph = Graphs.simpleLoopStruture();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> spanningTree = Algorithms.getDfsSpanningTree(flowGraph);
    
    System.out.println(spanningTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, spanningTree);
    assert1pointsTo2(Graph.START, "head", spanningTree);
    assert1pointsTo2("head", "body", spanningTree);
    assert1pointsTo2("head", "tail", spanningTree);
    //... and
    assert1doesNotPointTo2("head", Graph.END, spanningTree);
    assert1doesNotPointTo2(Graph.START, "tail", spanningTree);
  }
  
  /**
   * <a href="http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf">http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf
   */
  @Test
  public void testSpanningTreeFischerLectureExample() {
    // given
    Graph<Integer> flowGraph = Graphs.fisher();
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> spanningTree = Algorithms.getDfsSpanningTree(flowGraph);
    
    //then
    System.out.println(spanningTree);
    
    assert1pointsTo2(Graph.START, "a", spanningTree);
    assert1pointsTo2("a", "b", spanningTree);
    assert1pointsTo2("a", "c", spanningTree);
    assert1pointsTo2("c", "d", spanningTree);
    assert1pointsTo2("d", "e", spanningTree);
    assert1pointsTo2("e", "f", spanningTree);
    assert1pointsTo2("f", Graph.END, spanningTree);
  }
}
