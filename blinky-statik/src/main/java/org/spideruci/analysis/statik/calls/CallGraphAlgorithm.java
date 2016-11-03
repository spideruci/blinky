package org.spideruci.analysis.statik.calls;

public enum CallGraphAlgorithm {
  
  CHA,
  SPARK;
    
  public static CallGraphAlgorithm fromString(String name) {
    switch(name) {
    case "cha":
      return CHA;
    case "spark":
      return SPARK;
    default:
      throw new RuntimeException();
    }
  }

}
