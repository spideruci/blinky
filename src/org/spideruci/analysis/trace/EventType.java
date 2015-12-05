package org.spideruci.analysis.trace;

public enum EventType {
  $$$ (1),
  
  $enter$ (2),
  $exit$ (2),
  $line$ (2),
  $athrow$ (2),
  $return$ (2),
  $invoke$ (2),
  $var$ (2),
  $jump$ (2),
  $field$ (2),
  $iinc$ (2),
  $zero$ (2),
  $constant$ (2),
  $arrayload$ (2),
  $arraystore$ (2),
  $math$ (2),
  $stack$ (2),
  $type$ (2),
  $compare$ (2),
  $arraylen$ (2),
  $monitor$ (2),
  
  $$class$$ (4),
  $$method$$ (4);
  
  private final int kind;
  
  EventType(final int kind) {
    this.kind = kind;
  }
  
  public boolean isDecl() {
    return (kind & 4) == 4;
  }

  public boolean isInsn() {
    return (kind & 2) == 2;
  }
  
  public boolean isExec() {
    return (kind & 1) == 1;
  }
}
