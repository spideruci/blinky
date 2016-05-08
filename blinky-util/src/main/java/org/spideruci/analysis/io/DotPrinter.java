package org.spideruci.analysis.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

public class DotPrinter {
  
  private final BufferedWriter printer;
  
  
  public static DotPrinter getInstance(PrintStream _printer) {
    return new DotPrinter(new BufferedWriter(new PrintWriter(_printer)));
  }
  
  public static DotPrinter create(OutputStream out) {
    return new DotPrinter(new BufferedWriter(new OutputStreamWriter(out)));
  }
  
  static DotPrinter singleton = null;
  public static DotPrinter singleton() {
    if(singleton == null) {
      singleton = new DotPrinter(new BufferedWriter(new PrintWriter(System.out)));
    }
    return singleton;
  }
  
  private DotPrinter(BufferedWriter _printer) {
    printer = _printer;
  }
  
  public void printStrictStart(String graphName) {
    String format = "strict digraph %s {\n node [margin=0.2 shape=box] \n";
    String start = String.format(format, graphName);
    try {
      printer.write(start);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printStart(String graphName) {
    String format = "digraph %s {\n node [margin=0.2 shape=box] \n";
    String start = String.format(format, graphName);
    try {
      printer.write(start);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printEnd() {
    try {
      printer.write("}\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printOneLineComment(String comment) {
    try {
      printer.write("//" + comment.replaceAll("\\n", " ") + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printEdge(Object node1, Object node2) {
    StringBuilder edge = new StringBuilder();
    edge.append(node1).append(" -> ").append(node2).append("\n");
    try {
      printer.write(edge.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printEdge(Object node1, Object node2, double penwidth) {
    String hexString = doubleToHex(penwidth);
    StringBuilder edge = new StringBuilder();
    edge.append(node1).append(" -> ").append(node2)
    .append("[style=\"filled\", ").append("penwidth=").append(penwidth * 4).append(",")
    .append("label=\"").append(penwidth).append("\",")
    .append("color=\"").append(defaultColor).append(hexString).append("\"")
    .append("];").append("\n");
    try {
      printer.write(edge.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void appendEdge(Object node1, Object node2) {
    StringBuilder edge = new StringBuilder();
    edge.append(node1).append(" -> ").append(node2).append("\n");
    try {
      printer.append(edge.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void printNode(Object node, String label) {
    StringBuilder nodedef = new StringBuilder();
    nodedef.append(node)
    .append("[label=\"").append(label.replaceAll("\\n", "\\\\l"))
    .append("\"]\n");
    try {
      printer.write(nodedef.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  static final String defaultColor = "#AA0000";
  static final String  niceColor = "#804146";
  public void printNode(Object node, String label, double colorWt) {
    String hexString = doubleToHex(colorWt);
    
    StringBuilder nodedef = new StringBuilder();
    nodedef.append(node)
    .append("[style=\"filled\", label=\"")
    .append(label.replaceAll("\\n", "\\\\l"))
    .append("\", ")
    .append("fillcolor=\"")
    .append(defaultColor)
    .append(hexString)
    .append("\"")
    .append("]\n");
    try {
      printer.write(nodedef.toString());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private String doubleToHex(double colorWt) {
    if(colorWt < 0 || colorWt > 1) 
      throw new RuntimeException("Unsupported colorWt value: " + colorWt);
    int wt;
//    colorWt = (colorWt * 0.625) + 0.375;
    colorWt = (colorWt - 0.35) / 0.65;
    if(colorWt <= 0) colorWt = 0.0;
    wt = (int) (colorWt * 255);
//    if(colorWt >= 0.5) {
//      wt = (int) (colorWt * 255);
//    } else {
//      wt = (int) (colorWt * colorWt * 255);
//    }
    return Integer.toHexString(wt).toUpperCase();
  }
  
  public void close() {
    try {
      printer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void flush() {
    try {
      printer.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
