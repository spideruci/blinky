package org.spideruci.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Prints and reads in the error logs generated as a result of running Blinky.
 * The reading of the error logs is currently capable of the following:<br>
 * <li> Generating classes lists for successful (or otherwise) cases of 
 * instrumentation and indicating if the instrumentation was runtime; and,
 * <li> Suggesting if there were any exception messages with stack traces.
 * 
 * @author vpalepu
 *
 */
public class ErrorLogManager {
  
  public static final int SUXES = 1;
  public static final int SKIPD = 0;
  public static final int FAILD = -1;
  
  public static void logClassTxStatus(PrintStream REAL_ERR, String className, boolean isRuntime, int status) {
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
  
  public static void checkLogForStackTraces(PrintStream REAL_OUT,File errLog) {
    MyAssert.assertThat(errLog != null && errLog.exists() && errLog.isFile(), 
        "Malformed or non-existent error log file given as input.");
    
    Scanner scanner;
    
    try {
      scanner = new Scanner(errLog);
      int count = 0;
      final String causedByClause = "Caused by: ";
      final String exceptionInThreadClause = "Exception in thread";
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.trim();
        if(line.contains(causedByClause) 
            || line.contains(exceptionInThreadClause)) {
          REAL_OUT.println("Possible stack trace detected at: L" + count);
        }
        count += 1;
      }
    } catch(FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    
    scanner.close();
  }
  
  /**
   * Bootstrap classes are classes that were required by the JVM during the
   * bootstrapping phase (likely) and a bit after, all the way to just before the 
   * execution of the premain method.
   * 
   * Listing these classes is useful to identify if any classes should not be
   * instrumented in an offline, compile time instrumentation step, particularly
   * in the case of rt.jar.
   * @param errLog
   */
  public static void listBootstrapClasses(PrintStream REAL_ERR, PrintStream REAL_OUT, File errLog) {
    MyAssert.assertThat(errLog != null && errLog.exists() && errLog.isFile(), 
        "Malformed or non-existent error log file given as input.");
    
    Scanner scanner;
    
    try {
      scanner = new Scanner(errLog);
      final String runtimeInstrumentationClause = "runtime-instrumentation";
      final String forClause = "for ";
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.trim();
        if(line.startsWith(runtimeInstrumentationClause)) {
          String[] split = line.split(forClause);
          
          if(split[1] == null) {
            REAL_ERR.println(line);
          } else {
            String className = split[1].trim();
            REAL_OUT.println(className);
          }
          
        }
      }
    } catch(FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    
    scanner.close();
  }
  
  public static void listFailedInstrumentations(PrintStream REAL_ERR, PrintStream REAL_OUT, File errLog) {
    MyAssert.assertThat(errLog != null && errLog.exists() && errLog.isFile(), 
        "Malformed or non-existent error log file given as input.");
    
    Scanner scanner;
    
    try {
      scanner = new Scanner(errLog);
      final String failedInstrumentationClause = "instrumentation failed for ";
      final int clausesize = failedInstrumentationClause.length();
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.trim();
        if(line.startsWith(failedInstrumentationClause)) {
          String className = line.trim().substring(clausesize).trim();
          
          if(className == null || className.isEmpty()) {
            REAL_ERR.println(line);
          } else {
            REAL_OUT.println(className);
          }
          
        }
      }
    } catch(FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    
    scanner.close();
  }
  
  public static void listSkippedInstrumentations(PrintStream REAL_ERR, PrintStream REAL_OUT, File errLog) {
    MyAssert.assertThat(errLog != null && errLog.exists() && errLog.isFile(), 
        "Malformed or non-existent error log file given as input.");
    
    Scanner scanner;
    
    try {
      scanner = new Scanner(errLog);
      final String failedInstrumentationClause = "instrumentation skipped for ";
      final int clausesize = failedInstrumentationClause.length();
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.trim();
        if(line.startsWith(failedInstrumentationClause)) {
          String className = line.trim().substring(clausesize).trim();
          
          if(className == null || className.isEmpty()) {
            REAL_ERR.println(line);
          } else {
            REAL_OUT.println(className);
          }
          
        }
      }
    } catch(FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    
    scanner.close();
  }
  
  public static void listGoodInstrumentations(PrintStream REAL_ERR, PrintStream REAL_OUT, File errLog) {
    MyAssert.assertThat(errLog != null && errLog.exists() && errLog.isFile(), 
        "Malformed or non-existent error log file given as input.");
    
    Scanner scanner;
    
    try {
      scanner = new Scanner(errLog);
      final String failedInstrumentationClause = "instrumentation successful for ";
      final int clausesize = failedInstrumentationClause.length();
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.trim();
        if(line.startsWith(failedInstrumentationClause)) {
          String className = line.trim().substring(clausesize).trim();
          
          if(className == null || className.isEmpty()) {
            REAL_ERR.println(line);
          } else {
            REAL_OUT.println(className);
          }
          
        }
      }
    } catch(FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    
    scanner.close();
  }
  
  public static void main(String[] args) {
    String errlogPath = System.getProperty("errlog");
    File errlog = new File(errlogPath);
    String operation = System.getProperty("command");
    switch(operation) {
    case "locate-stack-trace":
      checkLogForStackTraces(System.err, errlog);
      return;
    case "ls-bootstrap":
      listBootstrapClasses(System.err, System.out, errlog);
      return;
    case "ls-fail":
      listFailedInstrumentations(System.err, System.out, errlog);
      return;
    case "ls-skip":
      listSkippedInstrumentations(System.err, System.out, errlog);
      return;
    case "ls-good":
      listGoodInstrumentations(System.err, System.out, errlog);
    default:
      throw new RuntimeException();
    }
  }
  
}
