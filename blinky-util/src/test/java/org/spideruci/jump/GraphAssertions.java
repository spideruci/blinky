package org.spideruci.jump;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

public class GraphAssertions {
  
  public static <T> void assert1pointsTo2(String one, String two, Graph<T> graph) {
    assertTrue("Expceted: " + one +  "--->" + two, 
        graph.node(one).pointsTo().contains(graph.node(two)));
  }
  
  public static <T> void assert1doesNotPointTo2(String one, String two, Graph<T> graph) {
    assertFalse("Expceted: " + one +  "-/->" + two,
        graph.node(one).pointsTo().contains(graph.node(two)));
  }
  
  public static <T> void assertNodeIsSolitary(Node<T> node) {
    assertTrue(node.isPointedByNone() && node.pointsToNone());
  }

}
