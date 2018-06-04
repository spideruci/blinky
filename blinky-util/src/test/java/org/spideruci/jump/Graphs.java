package org.spideruci.jump;

import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

public class Graphs {
  
  public static Graph<Integer> justStartAndEnd() {
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    return flowGraph;
  }
  
  public static Graph<Integer> startAndEndWithACycle() {
    Graph<Integer> flowGraph = Graphs.justStartAndEnd();
    return flowGraph.endPointsTo(flowGraph.startNode());
  }
  
  public static Graph<Integer> threeNodesWithACycle() {
    Graph<Integer> flowGraph = Graph.create();
    
    Node<Integer> node = Node.create("node", flowGraph);
    flowGraph.nowHas(node);
    flowGraph.startPointsTo(node.pointsTo(flowGraph.endNode()));
    flowGraph.endPointsTo(flowGraph.startNode());
    
    return flowGraph;
  }
  
  public static Graph<Integer> simpleIfStructure() {
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> branch = Node.create("branch", flowGraph);
    Node<Integer> then = Node.create("then", flowGraph);
    Node<Integer> join = Node.create("join", flowGraph);
    Node<Integer> next = Node.create("next", flowGraph);
    flowGraph.nowHas(branch.and(then).and(join).and(next));
    
    flowGraph.startNode().pointsTo(branch);
    branch.pointsTo(then.and(join));
    then.pointsTo(join);
    join.pointsTo(next);
    next.pointsTo(flowGraph.endNode());
    
    return flowGraph;
  }
  
  public static Graph<Integer> simpleIfElseStructure() {
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> branch = Node.create("branch", flowGraph);
    Node<Integer> then = Node.create("then", flowGraph);
    Node<Integer> elze = Node.create("else", flowGraph);
    Node<Integer> join = Node.create("join", flowGraph);
    Node<Integer> next = Node.create("next", flowGraph);
    flowGraph.nowHas(branch.and(then).and(elze).and(join).and(next));
    
    flowGraph.startNode().pointsTo(branch);
    branch.pointsTo(then.and(elze));
    then.pointsTo(join);
    elze.pointsTo(join);
    join.pointsTo(next);
    next.pointsTo(flowGraph.endNode());
    
    return flowGraph;
  }
  
  public static Graph<Integer> simpleLoopStruture() {
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> head = Node.create("head", flowGraph);
    Node<Integer> body = Node.create("body", flowGraph);
    Node<Integer> tail = Node.create("tail", flowGraph);
    flowGraph.nowHas(head.and(body).and(tail));
    
    flowGraph.startNode().pointsTo(head);
    head.pointsTo(body.and(tail));
    body.pointsTo(head);
    tail.pointsTo(flowGraph.endNode());
    
    return flowGraph;
  }
  
  /**
   * <a href="http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf">http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf
   */
  public static Graph<Integer> fisher() {
    Graph<Integer> flowGraph = Graph.create("fischer");
    
    Node<Integer> a = Node.create("a", flowGraph);
    Node<Integer> b = Node.create("b", flowGraph);
    Node<Integer> c = Node.create("c", flowGraph);
    Node<Integer> d = Node.create("d", flowGraph);
    Node<Integer> e = Node.create("e", flowGraph);
    Node<Integer> f = Node.create("f", flowGraph);
    flowGraph.nowHas(a.and(b).and(c).and(d).and(e).and(f));
    
    flowGraph.startNode().pointsTo(a);
    a.pointsTo(b.and(c));
    b.pointsTo(d);
    c.pointsTo(d);
    d.pointsTo(e);
    e.pointsTo(f);
    f.pointsTo(flowGraph.endNode().and(e));
    return flowGraph;
  }
  
  public static Graph<Integer> simpleBinaryTree() {
    Graph<Integer> flowGraph = Graph.create("binary");
    
    Node<Integer> n1 = Node.create("1", flowGraph);
    Node<Integer> n2 = Node.create("2", flowGraph);
    Node<Integer> n3 = Node.create("3", flowGraph);
    Node<Integer> n4 = Node.create("4", flowGraph);
    Node<Integer> n5 = Node.create("5", flowGraph);
    Node<Integer> n6 = Node.create("6", flowGraph);
    
    flowGraph.nowHas(n1.and(n2).and(n3).and(n4).and(n5).and(n6));
    
    flowGraph.startPointsTo(n3);
    n3.pointsTo(n5.and(n2));
    n5.pointsTo(n1.and(n4));
    n2.pointsTo(n6);
    
    return flowGraph;
    
  }

  public static Graph<Integer> threeStronglyConnectedComponents() {
    Graph<Integer> flowGraph = Graph.create("3scc");
    
    Node<Integer> n1 = Node.create("1", flowGraph);
    Node<Integer> n2 = Node.create("2", flowGraph);
    Node<Integer> n3 = Node.create("3", flowGraph);
    Node<Integer> n4 = Node.create("4", flowGraph);
    Node<Integer> n5 = Node.create("5", flowGraph);
    Node<Integer> n6 = Node.create("6", flowGraph);
    Node<Integer> n7 = Node.create("7", flowGraph);
    Node<Integer> n8 = Node.create("8", flowGraph);
    Node<Integer> n9 = Node.create("9", flowGraph);
    
    flowGraph.nowHas(n1.and(n2).and(n3).and(n4).and(n5).and(n6).and(n7).and(n8).and(n9));
    
    flowGraph.startPointsTo(n1);
    
    n1.pointsTo(n7);
    n4.pointsTo(n1);
    n7.pointsTo(n4.and(n9));
    n9.pointsTo(n6);
    n3.pointsTo(n9);
    n6.pointsTo(n3.and(n8));
    n8.pointsTo(n2);
    n2.pointsTo(n5);
    n5.pointsTo(n8);
    
    
    return flowGraph;
  }

}
