package org.spideruci.analysis.trace;

import java.util.Iterator;
import java.util.Scanner;

public class TraceIterator implements Iterator<Event> {

  private final Scanner scanner;
  private Event event;
  
  public TraceIterator(Scanner scanner) {
    this.scanner = scanner;
    this.event = null;
  }
  
  @Override
  public boolean hasNext() {
    while(scanner.hasNextLine()) {
      String eventString = scanner.nextLine();
      this.event = Event.fromString(eventString);
      if(this.event != null) {
        return true;
      }
    }
    
    return false;
  }

  @Override
  public Event next() {
    return this.event;
  }

  @Override
  public void remove() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not supported.");
  }

}
