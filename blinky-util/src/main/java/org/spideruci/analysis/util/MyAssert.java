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
  
  public static void quietlyAssertThat(boolean condition, String message) {
    if(!condition) {
      new RuntimeException(message).printStackTrace();
    }
  }

}