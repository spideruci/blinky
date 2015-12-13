package org.spideruci.analysis.util;

import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;

public class ErrorLogManager {
  
  public static final int SUXES = 1;
  public static final int SKIPD = 0;
  public static final int FAILD = -1;
  
  public static void logClassTxStatus(String className, boolean isRuntime, int status) {
    final String dynTxTag = isRuntime ? "runtime-" : "";
    switch(status) {
      case SUXES:
        REAL_ERR.println(dynTxTag + "instrumentation successful for " + className);
        break;
      case SKIPD:
        REAL_ERR.println(dynTxTag + "instrumentation skipped for " + className);
        break;
      case FAILD:
        REAL_ERR.println(dynTxTag + "instrumentation failed for " + className);
        break;
      default:
        throw new RuntimeException("Unknonwn status code about class transform:"
            + "status:" + status
            + "className:" + className);
    }
  }
  
}
