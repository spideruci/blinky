package org.spideruci.analysis.dynamic.util;

import java.io.PrintStream;

public class Arrays {
  
  public static PrintStream REAL_OUT;
  
  public static void setRealOut(PrintStream out) {
    REAL_OUT = out;
  }
  
  public static void printArray(float[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(long[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(double[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(short[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(byte[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(boolean[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(int[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(char[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i] + "~");
    }
    REAL_OUT.println();
  }
  
  public static void printArray(Object[] array) {
    for(int i = 0; i < array.length; i += 1) {
      REAL_OUT.print(array[i].toString() + "~");
    }
    REAL_OUT.println();
  }

}
