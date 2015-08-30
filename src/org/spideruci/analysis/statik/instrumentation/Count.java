package org.spideruci.analysis.statik.instrumentation;

public class Count {
  private static int CLASS_COUNT = 0;
  private static int METHOD_COUNT = 0;
  private static int LOG_COUNT = 0;
  
  public static int anotherClass() {
    return ++CLASS_COUNT;
  }
  
  public static int anotherMethod() {
    return ++METHOD_COUNT;
  }
  
  public static int anotherInsn() {
    return ++LOG_COUNT;
  }

}
