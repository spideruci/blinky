package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.HashMap;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;

public class SourceLineMapper {
  
  private final HashMap<SootMethod, Integer> methods;
  private final ArrayList<HashMap<Unit, Integer>> unit2lineMaps;
  
  public static SourceLineMapper init() {
    return new SourceLineMapper();
  }
  
  private SourceLineMapper() {
    methods = new HashMap<>();
    unit2lineMaps = new ArrayList<>();
  }
  
  private HashMap<Unit, Integer> mapMethodSourceLines(SootMethod method) {
    
    Integer methodIndex = methods.get(method);
    
    if(methodIndex == null) {
      methods.put(method, unit2lineMaps.size());
      Body methodBody = method.retrieveActiveBody();
      PatchingChain<Unit> chain = methodBody.getUnits();
      
      HashMap<Unit, Integer> unit2lineMap = new HashMap<>();
      for(Unit unit : chain) {
        unit2lineMap.put(unit, unit.getJavaSourceStartLineNumber());
      }
      unit2lineMaps.add(unit2lineMap);
    }
    
    HashMap<Unit, Integer> units2lines = unit2lineMaps.get(methodIndex);
    return units2lines;
  }
  
  
  public int getSourceLineForUnit(SootMethod method, Unit unit) {
    HashMap<Unit, Integer> units2lines = mapMethodSourceLines(method);
    
    PatchingChain<Unit> units = method.retrieveActiveBody().getUnits();
    if(!units.contains(unit))
      throw new RuntimeException(
          unit.toString() + " not present in the body of " + method.toString());
    
    return units2lines.get(unit);
  }

}
