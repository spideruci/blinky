package org.spideruci.analysis.trace.io;

import java.io.File;

import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;

public class LegacySourcelineTraceWriter {
  
  public static void main(String[] args) {
    File file = new File(args[0]);
    if(file == null || !file.exists() || !file.isFile() || !file.getName().endsWith(".trc")) {
      throw new RuntimeException("File invalid.");
    }
    
    TraceReader reader = new TraceReader(file);
    while(true) {
      TraceEvent event = reader.readNextExecutedEvent();
      if(event == null) {
        break;
      }
      if(event.getExecInsnType() != EventType.$line$) {
        continue;
      }
      //*1*net/n3/nanoxml/XMLParserFactory:createDefaultXMLParser()Lnet/n3/nanoxml/IXMLParser;:L85
      String ownerClass = reader.getExecutedEventOwnerClass(event);
      String ownerMethod = reader.getExecutedEventOwnerMethod(event);
      String threadId = event.getExecThreadId();
      int lineNumber = reader.getExecutedEventSourceLine(event);
      System.out.printf("*%s*%s:%s:L%d\n", threadId, ownerClass, ownerMethod, lineNumber);
    }
    
  }
  
}
