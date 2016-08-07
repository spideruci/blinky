package org.spideruci.analysis.statik;

import java.util.HashMap;
import java.util.List;

import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

import soot.Body;
import soot.PatchingChain;
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

        Unit tgt = target.getBody();
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

  public void addIcfgEdge(Unit src, SootMethod srcMethod, Unit tgt, SootMethod tgtMethod) {
    Node<Unit> source = addIcfgNode(src, srcMethod);
    if(source == null)
      return;
    
    unitToMethod.put(source.getLabel(), srcMethod.toString());

    Node<Unit> target = addIcfgNode(tgt, tgtMethod);
    if(target == null)
      return;

    unitToMethod.put(target.getLabel(), tgtMethod.toString());
    
    source.pointsTo(target);
  }

  public void addSootMethodToIcfg(SootMethod method) {
    UnitGraph graph = getMethodFlowGraph(method);
    Items<Unit> units = new Items<Unit>(graph.iterator());
    for(Unit unit : units) {
      List<Unit> succs = graph.getSuccsOf(unit);
      for(Unit succ : succs) {
        addIcfgEdge(unit, method, succ, method);
      }
    }
  }

  private Node<Unit> addIcfgNode(Unit unit, SootMethod method) {
    if(unit == null)
      return null;
    
    final String unitLabel = getUnitLabel(unit, method);
    Node<Unit> unitNode = null;
    if(icfg.contains(unitLabel)) {
      unitNode = icfg.node(unitLabel);
    } else {
      unitNode = Node.create(unitLabel, unit, icfg);
      icfg.nowHas(unitNode);
    }

    return unitNode;
  }
  
  private String getUnitLabel(Unit unit, SootMethod method) {
    Body methodBody = method.retrieveActiveBody();
    int unitIndex = indexOf(methodBody.getUnits(), unit);
    String unitLabel = method.toString() + unitIndex;
    unitLabel = unit.toString();
    return unitLabel;
  }
  
  private int indexOf(PatchingChain<Unit> units, Unit unit) {
    int index = -1;
    
    Items<Unit> items = new Items<>(units.iterator());
    
    for(Unit item : items) {
      index += 1;
      if(item.equals(unit))
        return index;
    }
    
    return -1;
  }



  public UnitGraph getMethodFlowGraph(SootMethod method) {

    Body body = method.retrieveActiveBody();
    UnitGraph flowGraph = new ExceptionalUnitGraph(body);

    return flowGraph;
  }

}
