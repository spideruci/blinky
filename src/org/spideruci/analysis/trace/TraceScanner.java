package org.spideruci.analysis.trace;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class TraceScanner implements Iterable<TraceEvent> {
  
  private final File file;
  private final TraceEvent[] events;

  public TraceScanner(final File file) {
    this.file = file;
    this.events = null;
  }
  
  public TraceScanner(final TraceEvent[] events) {
    this.file = null;
    this.events = events;
  }
  
  @Override
  public Iterator<TraceEvent> iterator() {
    return traceIterator();
  }
  
  public TraceIterator traceIterator() {
    if(file != null) {
      Scanner scanner = null;
      try {
        scanner = new Scanner(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
      return new TraceIterator(scanner);
    } else {
      return new TraceIterator(this.events);
    }
  }
}
