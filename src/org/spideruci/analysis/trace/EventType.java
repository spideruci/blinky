package org.spideruci.analysis.trace;

public enum EventType {
  $enter$,
  $exit$,
  $line$,
  $athrow$,
  $return$,
  $invoke$,
  $$class$$,
  $$method$$;
  
  public boolean isStatic() {
    switch(this) {
    case $$class$$:
    case $$method$$:
      return true;
    case $athrow$:
    case $enter$:
    case $invoke$:
    case $line$:
    case $return$:
      return false;
    default:
      throw new UnsupportedOperationException();
    }
  }
}
