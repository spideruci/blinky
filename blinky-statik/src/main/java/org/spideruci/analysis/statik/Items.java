package org.spideruci.analysis.statik;

import java.util.Iterator;

public class Items<T> implements Iterable<T> {
  
  public static <U> Items<U> items(Iterator<U> iter) {
    return new Items<U>(iter);
  }

  private Iterator<T> iter;
  
  public Items(Iterator<T> iter) {
    this.iter = iter;
  }
  
  @Override
  public Iterator<T> iterator() {
    return iter;
  }

}
