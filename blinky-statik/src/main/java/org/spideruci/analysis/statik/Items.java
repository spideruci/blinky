package org.spideruci.analysis.statik;

import java.util.Iterator;

public class Items<T> implements Iterable<T> {

  private Iterator<T> iter;
  
  public Items(Iterator<T> iter) {
    this.iter = iter;
  }
  
  @Override
  public Iterator<T> iterator() {
    return iter;
  }

}
