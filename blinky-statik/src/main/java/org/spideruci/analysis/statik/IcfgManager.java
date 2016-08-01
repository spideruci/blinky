package org.spideruci.analysis.statik;

import java.util.HashMap;
import java.util.List;

import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class IcfgManager {

  private Graph<Unit> icfg;
  private HashMap<String, String> unitToMethod;

  public IcfgManager() {
    this.icfg = Graph.create();
    unitToMethod = new HashMap<>();
  }
  
  public Graph<Unit> icfg() {
    return this.icfg;
  }
  
  public Graph<String> icfgJavaSourceLines() {
    Graph<String> javaIcfg = Graph.create();
    for(Node<Unit> source : icfg.getNodes()) {
      if(source == null)
        continue;
      
      for(Node<Unit> target : source.pointsTo()) {
        if(target == null)
          continue;
        
        Unit src = source.getBody();
        int srcLine = src.getJavaSourceStartLineNumber();
        String srcMethodName = unitToMethod.get(src.toString());
        
        Unit tgt = source.getBody();
        int tgtLine = tgt.getJavaSourceStartLineNumber();
        String tgtMethodName = unitToMethod.get(tgt.toString());
        
        Node<String> sourceJava = Node.create(srcLine + ":" + srcMethodName, javaIcfg);
        Node<String> targetJava = Node.create(tgtLine + ":" + tgtMethodName, javaIcfg);
        
        javaIcfg.nowHas(sourceJava.and(targetJava));
        sourceJava.pointsTo(targetJava);
      }
    }
    
    return javaIcfg;
  }

  public void addIcfgEdge(Unit src, String srcMethod, Unit tgt, String tgtMethod) {
    Node<Unit> source = addIcfgNode(src);
    if(source == null)
      return;
    
    unitToMethod.put(src.toString(), srcMethod);

    Node<Unit> target = addIcfgNode(tgt);
    if(target == null)
      return;

    unitToMethod.put(tgt.toString(), tgtMethod);
    
    source.pointsTo(target);
  }

  public void addSootMethodToIcfg(SootMethod method) {
    final String methodName = method.toString();
    UnitGraph graph = getMethodFlowGraph(method);
    Items<Unit> units = new Items<Unit>(graph.iterator());
    for(Unit unit : units) {
      List<Unit> succs = graph.getSuccsOf(unit);
      for(Unit succ : succs) {
        addIcfgEdge(unit, methodName, succ, methodName);
      }
    }
  }

  private Node<Unit> addIcfgNode(Unit unit) {
    if(unit == null)
      return null;

    final String unitLabel = unit.toString();
    Node<Unit> unitNode = null;
    if(icfg.contains(unitLabel)) {
      unitNode = icfg.node(unitLabel);
    } else {
      unitNode = Node.create(unitLabel, unit, icfg);
      icfg.nowHas(unitNode);
    }

    return unitNode;
  }



  public UnitGraph getMethodFlowGraph(SootMethod method) {

    Body body = method.retrieveActiveBody();
    UnitGraph flowGraph = new ExceptionalUnitGraph(body);

    return flowGraph;
  }

}
