package org.spideruci.analysis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotReader {
  private BufferedReader reader;
  private final File graphFile;
  private final InputStream graphIn;
  
  public static DotReader create(File file) {
    return new DotReader(getBufferedReader(file), file);
  }
  
  public static DotReader create(InputStream in) {
    return new DotReader(new BufferedReader(new InputStreamReader(in)), in);
  }
  
  private DotReader(BufferedReader reader, File file) {
    this.reader = reader;
    this.graphFile = file;
    this.graphIn = null;
  }
  
  private DotReader(BufferedReader reader, InputStream in) {
    this.reader = reader;
    this.graphFile = null;
    this.graphIn = in;
  }
  
  public void resetReader() {
    if(graphFile != null)
      reader = getBufferedReader(graphFile);
    else if(graphIn != null)
      reader = new BufferedReader(new InputStreamReader(graphIn));
    else {
      String message = "Where in the name of Gosling in the bloody source?!?";
      throw new RuntimeException(message);
    }
  }
  
      private static BufferedReader getBufferedReader(File file) {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e.getMessage());
        }
        return reader;
      }
  
  public String[] getNextEdge() {
    String[] edge = null;
    while(true) {
      try {
        edge = nextEdge();
      } catch (MalformedEdgeException e) {
        System.err.println(e.getMessage());
        continue;
      } catch (IOException e) {
        e.printStackTrace();
      }
      break;
    }
    return edge;
  }
  
      private String[] nextEdge() throws IOException {
        String edgeString = reader.readLine();
        if(edgeString == null) {
          reader.close();
          if(graphIn != null) graphIn.close();
          return null;
        }
        if(!edgeString.contains(" -> ")) {
          throw new MalformedEdgeException(edgeString);
        }
        Matcher intMatcher = Pattern.compile("(-?\\d+)").matcher(edgeString);
        if(!intMatcher.find()) 
          throw new MalformedEdgeException(edgeString);
        String nodea = edgeString.substring(intMatcher.start(), intMatcher.end());
        if(!intMatcher.find()) 
          throw new MalformedEdgeException(edgeString);
        String nodeb = edgeString.substring(intMatcher.start(), intMatcher.end());
        return new String[] {nodea, nodeb};
      }
  
  public String firstNode() {
    resetReader();
    String[] edge = getNextEdge();
    resetReader();
    if(edge == null) return "-1";
    return edge[0];
  }
  
  public static class MalformedEdgeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public MalformedEdgeException(String edgeString) {
      super("malformed edge: " + edgeString);
    }
  }
  
}
