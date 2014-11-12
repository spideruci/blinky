package org.spideruci.analysis.dynamic.tests;

public class Namaste {

  public static void main(String[] args) {
    final String hello = "hello world"; 
    System.out.println(hello);
    
    for(int i = 0; i < 10; i += 1) {
      System.out.println(i + ":: " + hello);
    }

  }

}
