package org.spideruci.analysis.trace;

import java.io.File;

public class MethodInvokeScanner {

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
      if(!event.isMethodInvokeEvent()) {
        continue;
      }
      
      String sourceLine = event.toSourceString();
      System.out.println(sourceLine + ":" + event.invokedMethod());
    }
    
    System.out.println("done");

  }

}
