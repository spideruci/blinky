package org.spideruci.analysis.trace;

import java.util.Iterator;
import java.util.Scanner;

public class TraceIterator implements Iterator<TraceEvent> {

  private final Scanner scanner;
  private TraceEvent event;
  
  public TraceIterator(Scanner scanner) {
    this.scanner = scanner;
    this.event = null;
  }
  
  @Override
  public boolean hasNext() {
    while(scanner.hasNextLine()) {
      String eventString = scanner.nextLine();
      
      if(!eventString.startsWith("$")) {
        continue;
      }
      
      this.event = TraceEvent.valueOf(eventString);
      if(this.event != null) {
        return true;
      }
    }
    
    return false;
  }

  @Override
  public TraceEvent next() {
    return this.event;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("not supported.");
  }

}
