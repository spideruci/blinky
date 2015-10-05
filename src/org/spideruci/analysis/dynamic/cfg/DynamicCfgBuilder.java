package org.spideruci.analysis.dynamic.cfg;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.trace.TraceScanner;

import linloglayout.Edge;
import linloglayout.LinLogLayout;
import linloglayout.Node;

public class DynamicCfgBuilder {
  
  public static void main(String[] args) {
    ExecutionFlowGraph dynCfg = buildFromTrace(args[0]);
    
    for(Edge edge : dynCfg) {
      System.out.println(edge);
    }
    
//    System.exit(0);
    
    Map<Integer, List<Node>> modules = dynCfg.modularize();
    for(int module : modules.keySet()) {
      System.out.println(module + " " + modules.get(module).size());
      
//      for(Node node : modules.get(module)) {
//        System.out.println(node);
//      }
//      System.out.println("\n");
    }
  }
  
  public static ExecutionFlowGraph buildFromTrace(String filePath) {
    File file = new File(filePath);
    return buildFromTrace(file);
  }
  
  public static ExecutionFlowGraph buildFromTrace(File file) {
    if(file == null) {
      throw new RuntimeException("File is null.");
    }
    
    TraceScanner scanner = new TraceScanner(file);
    Node previousNode = null;
    String previousRawNode = null;
    Map<String,Map<String,Double>> rawGraph = new HashMap<>();
    
    for(TraceEvent event : scanner) {
      System.out.println(event);
      EventType type = event.getType();
      switch(type) {
      case $$$:
        String rawNode = event.getExecInsnId();
//        Node node = new Node(event.getExecInsnId(), 0);
        if(previousRawNode != null) {
          if(!rawGraph.containsKey(previousRawNode)) {
            rawGraph.put(previousRawNode, new HashMap<>());
          }
          
          Map<String, Double> destRawNodes = rawGraph.get(previousRawNode);
          if(destRawNodes == null) {
            continue;
          }
          
          if(destRawNodes.containsKey(rawNode)) {
            double edgeWt = destRawNodes.get(rawNode);
            destRawNodes.put(rawNode, edgeWt + 1);
          } else {
            destRawNodes.put(rawNode, 1.0);
          }
        }
        previousRawNode = rawNode;
        break;
      default: 
        continue;
      }
    }
    
    rawGraph = LinLogLayout.makeSymmetricGraph(rawGraph);
    
    ExecutionFlowGraph efg = ExecutionFlowGraph.create();
    for(String from : rawGraph.keySet()) {
      Map<String, Double> outgoingNodes = rawGraph.get(from); 
      if(from == null || outgoingNodes == null) {
        continue;
      }
      
      Node fromNode = new Node(from, 0);
      
      for(String to : outgoingNodes.keySet()) {
        Double weight = outgoingNodes.get(to);
        if(to == null || weight == null) {
          continue;
        }
        
        Node toNode = new Node(to, 0);
        Edge edge = new Edge(fromNode, toNode, weight);
        efg.addEdge(edge);
      }
    }
    
    
    return efg;
  }

}
