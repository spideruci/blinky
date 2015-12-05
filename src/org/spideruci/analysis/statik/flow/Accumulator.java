package org.spideruci.analysis.statik.flow;

import java.util.ArrayList;

/**
 * A collection that is designed to only add Node elements to a list
 * return such a list upon adding all desired Nodes.
 * @author vpalepu
 *
 * @param <T>
 */
public class Accumulator<T> {
  
  private ArrayList<Node<T>> accumulated;
  
  /**
   * Creates an Accumulator instance.
   * @return
   */
  public static <A> Accumulator<A> create() {
    return new Accumulator<A>();
  }
  
  private Accumulator() {
    accumulated = new ArrayList<>();
  }
  
  /**
   * Retrieves the current list of Nodes accumulated so far.
   * @return
   */
  public ArrayList<Node<T>> period() {
    return new ArrayList<Node<T>>(accumulated);
  }
  
  public ArrayList<Node<T>> andAlso(Node<T> node) {
    accumulated.add(node);
    return new ArrayList<>(accumulated);
  }
  
  public Accumulator<T> and(Node<T> node) {
    this.accumulated.add(node);
    return this;
  }
}