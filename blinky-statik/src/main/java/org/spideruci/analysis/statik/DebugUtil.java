package org.spideruci.analysis.statik;

import java.util.Date;

public class DebugUtil {
  
  public static boolean IS_DEBUG = true;
  
  /**
   * Print formatted newline
   * @param format
   * @param args
   */
  public static void printfln(String format, Object ... args) {
    if(IS_DEBUG) {
      logfln(format, args);
    }
  }
  
  public static void printfln() {
    if(IS_DEBUG) {
      System.out.println();
    }
    
  }
  
  /**
   * Log formatted newline
   * @param format
   * @param args
   */
  public static void logfln(String format, Object ... args) {
      final String tag = "[BLINKY STATIK " + new Date().toString() + "] ";
      System.out.printf(tag + format + "\n", args);
  }



}
