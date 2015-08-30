package org.spideruci.analysis.trace;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class TraceScanner implements Iterable<TraceEvent> {
  
  private final File file;

  public TraceScanner(final File file) {
    this.file = file;
  }
  
  @Override
  public Iterator<TraceEvent> iterator() {
    Scanner scanner = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
    return new TraceIterator(scanner);
  }
}
