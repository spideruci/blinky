package org.spideruci.analysis.statik.controlflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

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
  
  public Accumulator<T> with(Node<T>[] nodes) {
    for(Node<T> node : nodes) {
      and(node);
    }
    return this;
  }
  
  public Accumulator<T> with(Collection<Node<T>> nodes) {
    for(Node<T> node : nodes) {
      and(node);
    }
    return this;
  }
  
  public void forEach(Consumer<Node<T>> c) {
    accumulated.forEach(c);
  }
  
}