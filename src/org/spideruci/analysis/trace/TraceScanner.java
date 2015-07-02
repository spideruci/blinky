package org.spideruci.analysis.trace;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class TraceScanner implements Iterable<Event> {
  
  private final File file;

  public TraceScanner(final File file) {
    this.file = file;
  }
  
  @Override
  public Iterator<Event> iterator() {
    Scanner scanner = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
    return new TraceIterator(scanner);
  }
  
  public static void main(String[] args) {
    if(args.length == 0 || args[0] == null || args[0].length() == 0) {
      throw new RuntimeException("Specify file path as argument");
    }
    File file = new File(args[0]);
    if(!file.exists()) {
      throw new RuntimeException("Specified file path does not point to an exisiting file.");
    }
    
    if(!file.isFile()) {
      throw new RuntimeException("Specified file path is not pointing to a file.");
    }
    
    TraceScanner scanner = new TraceScanner(file);
    for(Event event : scanner) {
      if(!event.isSourceEvent()) {
        continue;
      }
      
      String sourceLine = event.toSourceString();
      System.out.println(sourceLine);
    }
  }

}
