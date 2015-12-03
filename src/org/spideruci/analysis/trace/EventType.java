package org.spideruci.analysis.trace;

public enum EventType {
  $$$,
  $enter$,
  $exit$,
  $line$,
  $athrow$,
  $return$,
  $invoke$,
  $var$,
  $jump$,
  $field$,
  $iinc$,
  $zero$,
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
    case $var$:
    case $jump$:
    case $field$:
    case $iinc$:
    case $zero$:
    case $line$:
    case $return$:
    case $$$:
      return false;
    default:
      throw new UnsupportedOperationException();
    }
  }

  public boolean isInsn() {
    switch(this) {
    case $$class$$:
    case $$method$$:
      return false;
    case $athrow$:
    case $enter$:
    case $invoke$:
    case $var$:
    case $jump$:
    case $field$:
    case $iinc$:
    case $zero$:
    case $line$:
    case $return$:
      return true;
    case $$$:
      return false;
    default:
      throw new UnsupportedOperationException();
    }
  }
}
