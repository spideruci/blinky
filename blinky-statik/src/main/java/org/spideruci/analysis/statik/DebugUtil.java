package org.spideruci.analysis.statik;

import java.util.Date;

public class DebugUtil {
  
  public static boolean IS_DEBUG = true;
  
  public static void printfln(String format, Object ... args) {
    if(IS_DEBUG) {
      final String tag = "[BLINKY STATIK " + new Date().toString() + "] ";
      
      System.out.printf(tag + format + "\n", args);
    }
  }

}
