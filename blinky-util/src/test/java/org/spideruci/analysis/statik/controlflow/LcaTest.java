package org.spideruci.analysis.statik.controlflow;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LcaTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void firstNullInputsShouldThrowIllegalStateException() {
    //given
    Node<Integer> n1 = null;
    Node<Integer> n2 = Node.create("node2", null);

    // then
    thrown.expect(IllegalStateException.class);

    // when
    Algorithms.lowestCommonAncestor(n1, n2);
  }

  @Test
  public void secondNullInputsShouldThrowIllegalStateException() {
    //given
    Node<Integer> n1 = Node.create("node2", null);
    Node<Integer> n2 = null;

    // then
    thrown.expect(IllegalStateException.class);

    // when
    Algorithms.lowestCommonAncestor(n1, n2);
  }

  @Test
  public void bothNullInputsShouldThrowIllegalStateException() {
    //given
    Node<Integer> n1 = null;
    Node<Integer> n2 = null;

    // then
    thrown.expect(IllegalStateException.class);

    // when
    Algorithms.lowestCommonAncestor(n1, n2);
  }

  @Test
  public void shouldThrowIllegalStateExceptionWithNodesFromDifferentTrees() {
    //given
    Node<Integer> n1 = Node.create("node1", Graph.createEmptyGraph());
    Node<Integer> n2 = Node.create("node2", Graph.createEmptyGraph());

    // then
    thrown.expect(IllegalStateException.class);

    // when
    Algorithms.lowestCommonAncestor(n1, n2);
  }

  @Test
  public void shouldThrow_NoExceptions_With_Treeless_NonNull_Nodes() {
    //given
    Node<Integer> n1 = Node.create("node1", null);
    Node<Integer> n2 = Node.create("node2", null);

    // when
    Algorithms.lowestCommonAncestor(n1, n2);
  }

  @Test
  public void shouldReturnInputNodeWhenPassedAsBothArgs() {
    //given
    Node<Integer> n1 = Node.create("node1", null);
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(n1, n1);

    // then
    assertEquals(n1, lca);
  }

  @Test
  public void shouldReturnRootWith_BalancedThreeElementBinaryTree() {
    //given
    Graph<Integer> tree = Graph.create();
    Node<Integer> n1 = Node.create("node1", tree);
    Node<Integer> n2 = Node.create("node2", tree);
    tree.nowHas(n1.and(n2));
    // ...and
    tree.startNode().pointsTo(n1);
    tree.startNode().pointsTo(n2);
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(n1, n2);

    // then
    assertEquals(tree.startNode(), lca);
  }
  
  @Test
  public void shouldReturnN1With_UnBalancedThreeElementBinaryTree() {
    //given
    Graph<Integer> tree = Graph.create();
    Node<Integer> n1 = Node.create("n1", tree);
    Node<Integer> n2 = Node.create("n2", tree);
    tree.nowHas(n1.and(n2));
    // ...and
    tree.startNode().pointsTo(n1);
    n1.pointsTo(n2);
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(n1, n2);

    // then
    assertEquals(n1, lca);
  }
  
  @Test
  public void shouldReturnRootWith_UnbalancedTree() {
    //given
    Graph<Integer> tree = Graph.create();
    
    Node<Integer> n1 = Node.create("n1", tree);
    Node<Integer> n2 = Node.create("n2", tree);
    Node<Integer> n3 =Node.create("n3", tree);
    Node<Integer> n4 =Node.create("n4", tree);
    Node<Integer> n5 =Node.create("n5", tree);
    
    tree.nowHas(n1.and(n2).and(n3).and(n4).and(n5));
    // ...and
    tree.startNode().pointsTo(n1);
    tree.startNode().pointsTo(n2.pointsTo(n3.pointsTo(n4.pointsTo(n5))));
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(n5, n1);

    // then
    assertEquals(tree.startNode(), lca);
  }
  
  @Test
  public void shouldReturnRootWith_NaryTree() {
    //given
    Graph<Integer> tree = Graph.create();
    @SuppressWarnings("unchecked")
    Node<Integer>[] nodes = new Node[] {
        Node.create("n1", tree),
        Node.create("n2", tree),
        Node.create("n3", tree),
        };
    tree.nowHas(Accumulator.<Integer>create().with(nodes));
    // ...and
    tree.startNode().pointsTo(nodes[0]);
    tree.startNode().pointsTo(nodes[1]);
    tree.startNode().pointsTo(nodes[2]);
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(nodes[2], nodes[0]);

    // then
    assertEquals(tree.startNode(), lca);
  }
  
  @Test
  public void shouldReturnN3With_NaryTree_WithLongNeck() {
    //given
    Graph<Integer> tree = Graph.create();
    
    Node<Integer> n1 = Node.create("n1", tree);
    Node<Integer> n2 = Node.create("n2", tree);
    Node<Integer> n3 =Node.create("n3", tree);
    Node<Integer> n4 =Node.create("n4", tree);
    Node<Integer> n5 =Node.create("n5", tree);
    Node<Integer> n6 =Node.create("n6", tree);
    
    tree.nowHas(n1.and(n2).and(n3).and(n4).and(n5).and(n6));
    // ...and
    tree.startNode().pointsTo(n1.and(n2).and(n3));
    
    n3.pointsTo(n6.and(n4.pointsTo(n5)));
    
    // when
    Node<Integer> lca = Algorithms.lowestCommonAncestor(n6, n5);

    // then
    assertEquals(n3, lca);
  }
  
  @Test
  public void shouldThrowIllegalStateExceptionWhenNodesNotInTree() {
    //given
    Graph<Integer> tree = Graph.create();
    
    Node<Integer> n1 = Node.create("n1", tree);
    Node<Integer> n2 = Node.create("n2", tree);
    
    tree.nowHas(n1.and(n2));
    // ...and
    tree.startNode().pointsTo(n1.and(n2));
    tree.endIsPointedBy(n1.and(n2));
    
    //then
    thrown.expect(IllegalStateException.class);
    
    // when
    Algorithms.lowestCommonAncestor(n1, tree.endNode());
  }

}
