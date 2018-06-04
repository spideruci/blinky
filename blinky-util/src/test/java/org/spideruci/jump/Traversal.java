package org.spideruci.jump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Traversal<T> {
  
  private ArrayList<Step<T>> traversal = new ArrayList<>();
  
  public static <T> Traversal<T> add(T item) {
    Traversal<T> t = new Traversal<>();
    t.and(item);
    return t;
  }
  
  public static <T> Traversal<T> addInAnyOrder(ArrayList<T> items) {
    Traversal<T> t = new Traversal<>();
    t.inAnyOrder(items);
    return t;
  }
  
  public Traversal<T> and(T item) {
    Step<T> step = Step.step(item);
    traversal.add(step);
    return this;
  }
  
  public Traversal<T> inAnyOrder(ArrayList<T> items) {
    Step<T> step = Step.steps(items);
    traversal.add(step);
    return this;
  }
  
  @Override
  public String toString() {
    return this.traversal.toString();
  }
  
  public void compareWith(ArrayList<T> actualTraversal) {
    
    for(int i = 0, j = 0; i < actualTraversal.size();) {
      
      if(j >= traversal.size()) {
        throw new RuntimeException(
            String.format("actual traversal: %s; expected traversal: %s", 
                actualTraversal.toString(), 
                this.toString()));
      }
      Step<T> step = traversal.get(j); 
      if(step.isKnown()) {
        T actualItem = actualTraversal.get(i);
        assertEquals("@position " + i, step.item, actualItem);
        i += 1;
      } else {
        ArrayList<T> itemsInAnyOrder = step.items;
        
        for(int k = i; k < i + itemsInAnyOrder.size(); k += 1) {
          T actualItem = actualTraversal.get(k);
          String errMsg = String.format(
              "expected actual item -- %s -- to be one of the following: %s", 
              actualItem, itemsInAnyOrder);
          assertTrue(errMsg, itemsInAnyOrder.contains(actualItem));
        }
        
        i += itemsInAnyOrder.size();
      }
      
      j += 1;
    }
  }
  
  public static class Step<T> {
     private final T item;
     private final ArrayList<T> items;
     
     
     public static <T> Step<T> steps(T[] items) {
       Step<T> step = new Step<>(Collections.emptyList());
       
       for(T item : items) {
         step.items.add(item);
       }
       
       return step;
     }
     
     public static <T> Step<T> steps(Collection<T> items) {
       return new Step<>(items);
     }
     
     public static <T> Step<T> step(T item) {
       return new Step<>(item);
     }
     
     private Step(T item) {
       this.item = item;
       this.items = null;
     }
     
     private Step(Collection<T> items) {
       this.item = null;
       
       this.items = new ArrayList<>();
       for(T it : items) {
         this.items.add(it);
       }

     }
     
     public boolean isKnown() {
       return this.item != null;
     }
     
     @Override
     public String toString() {
       if(isKnown()) {
         return this.item.toString();
       } else {
         return this.items.toString();
       }
     }
  }

}
