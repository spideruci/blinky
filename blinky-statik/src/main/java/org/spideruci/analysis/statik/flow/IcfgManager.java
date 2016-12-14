package org.spideruci.analysis.statik.flow;

import java.util.HashMap;
import java.util.List;

import org.spideruci.analysis.statik.DebugUtil;
import org.spideruci.analysis.statik.Items;
import org.spideruci.analysis.statik.SootCommander;
import org.spideruci.analysis.statik.Statik;
import org.spideruci.analysis.statik.UnitIdTag;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class IcfgManager {

  private Graph<Unit> icfg;
  private HashMap<String, String> unitToMethod;
  private HashMap<Unit, String> jimple2source;

  public IcfgManager() {
    this.icfg = Graph.create();
    unitToMethod = new HashMap<>();
    jimple2source = new HashMap<>();
  }
  
  public Graph<Unit> icfg() {
    return this.icfg;
  }
  
    private Node<String> getNode(Unit sootunit, Graph<String> javaIcfg) {
      
      int srcLine = sootunit.getJavaSourceStartLineNumber();
      if(srcLine <= 0) {
        return null;
      }
      
      UnitIdTag unitIdTag = (UnitIdTag) sootunit.getTag(UnitIdTag.UNIT_ID_TAG_NAME);
      final String sootunitLabel = unitIdTag.value();
      
      String srcMethodName = unitToMethod.get(sootunitLabel);
      final String srcLabel = srcLine + ":" + srcMethodName;
      
      Node<String> sourceJava;
      if(javaIcfg.contains(srcLabel)) {
        sourceJava = javaIcfg.node(srcLabel);
      } else {
        sourceJava = Node.create(srcLabel, javaIcfg);
        javaIcfg.nowHas(sourceJava);
      }
      
      jimple2source.put(sootunit, srcLabel);
      
      return sourceJava;
    }
  
  public Graph<String> icfgJavaSourceLines() {
    Graph<String> javaIcfg = Graph.create();
    for(Node<Unit> source : icfg.getNodes()) {
      if(source == null 
          || source == icfg.startNode() 
          || source == icfg.endNode())
        continue;
      
      Unit src = source.getBody();
      if(src == null) {
        System.out.printf("BLNKY STATIK DEBUG: source Node's body is null: label: %s%n",
            source.getLabel());
        continue;
      }
      
      Node<String> sourceJava = getNode(src, javaIcfg);
      if(sourceJava == null) {
        continue;
      }
      
      for(Node<Unit> target : source.pointsTo()) {
        if(target == null)
          continue;
        
        Unit tgt = target.getBody();
        
        if(tgt == null) {
          System.out.printf("BLNKY STATIK DEBUG: target Node's body is null: label: %s%n", 
              target.getLabel());
          continue;
        }
        
        Node<String> targetJava = getNode(tgt, javaIcfg);
        if(sourceJava == null) {
          continue;
        }
        
        if(sourceJava != targetJava)
          sourceJava.pointsTo(targetJava);
      }
    }
    
    logJimpleToSourceMapping();
    
    return javaIcfg;
  }

    private void logJimpleToSourceMapping() {
      if(!DebugUtil.IS_DEBUG)
        return;
      
      DebugUtil.printfln("\nJimple to Source Mapping");
      for(Unit jimple : jimple2source.keySet()) {
        UnitIdTag unitIdTag = (UnitIdTag) jimple.getTag(UnitIdTag.UNIT_ID_TAG_NAME);
        final String sootunitLabel = unitIdTag.value();
        String source = jimple2source.get(jimple);
        DebugUtil.printfln("%s %s \t\t %s%n", source, sootunitLabel, jimple.toString());
      }
      
      DebugUtil.printfln();
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
    
    UnitGraph graph = SootCommander.GET_UNIT_GRAPH(method);
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
    
    final int lineNumber = unit.getJavaSourceStartColumnNumber();
    final String methodString = method.toString();
    final String unitString = unit.toString();
    // TODO: issue #27
    // final int bytecodeOffset = -1;
    String unitLabel = methodString + "::" + lineNumber  + "::" + unitString;
    unit.addTag(new UnitIdTag(unitLabel));
    return unitLabel;
  }
  
  @SuppressWarnings("unused")
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

}
