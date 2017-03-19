package org.spideruci.analysis.trace;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

public class TraceIterator implements Iterator<TraceEvent> {

  private final Scanner scanner;
  private final TraceEvent[] events;
  private int counter = 0;
  private TraceEvent currentEvent = null;
  private TraceEvent previousEvent = null;
  
  public TraceIterator(Scanner scanner) {
    this.scanner = scanner;
    this.events = null;
  }
  
  public TraceIterator(TraceEvent[] events) {
    this.scanner = null;
    this.events = events;
  }
  
  @Override
  public boolean hasNext() {
    if(currentEvent != null && currentEvent != previousEvent) {
      return true;
    }
    
    if(this.events != null) {
      for(int i = this.counter; i < this.events.length; i += 1) {
        this.counter += 1;
        if(this.events[i] == null) {
          continue;
        }
        this.previousEvent = this.currentEvent;
        this.currentEvent = events[i];
        return true;
      }
      return false;
    }
    
    while(scanner.hasNextLine()) {
      String eventString = scanner.nextLine();
      
      if(!eventString.startsWith("$")) {
        continue;
      }
      
      if(eventString.startsWith("$$,")) {
        int indx = eventString.lastIndexOf("$$$,");
        eventString = eventString.substring(indx);
      }
      
      TraceEvent event = TraceEvent.valueOf(eventString);
      
      
      if(event == null) {
        continue;
      }
      
      if(event.getType().isDecl()) {
        System.err.println(eventString);
      }

      
      this.previousEvent = this.currentEvent;
      this.currentEvent = event;
      return true;
    }
    
    return false;
  }

  @Override
  public TraceEvent next() {
    if(!hasNext()) {
      throw new RuntimeException("Out of Trace-events!");
    }
    this.previousEvent = this.currentEvent;
    return this.currentEvent;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("not supported.");
  }

}
