package org.spideruci.analysis.util;

public class MyAssert {
  
  public static void assertThat(boolean condition) {
    if(!condition) {
      throw new RuntimeException();
    }
  }
  
  public static void assertThat(boolean condition, String message) {
    if(!condition) {
      throw new RuntimeException(message);
    }
  }

}
