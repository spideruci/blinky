package org.spideruci.analysis.trace;

public enum EventType {
  $$$,
  $enter$,
  $exit$,
  $line$,
  $athrow$,
  $return$,
  $invoke$,
  $$class$$,
  $$method$$;
  
  public boolean isDecl() {
    switch(this) {
    case $$class$$:
    case $$method$$:
      return true;
    case $athrow$:
    case $enter$:
    case $invoke$:
    case $line$:
    case $return$:
    case $$$:
      return false;
    default:
      throw new UnsupportedOperationException();
    }
  }
}
