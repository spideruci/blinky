package org.spideruci.analysis.dynamic;

import org.spideruci.analysis.statik.instrumentation.MethodProperties;

public class Profiler {

  public static int latestLineNumber = 0;
  public static boolean $guard1$ = false;
  public static boolean $guard2$ = false;
  public static boolean $premainGuard$ = true;
  private static long thread = -1;
  private static long count = 0;
  private static long time = 0;

  synchronized static public void reguard(boolean guard) {
    //  System.out.println("guard1:" + $guard1$ + "; guard2:" + $guard2$);
    //  System.out.println("setting the guard1 to false.");
    $guard1$ = false;
  }

  synchronized static public boolean guard() {
    boolean guard = $guard1$;
    $guard1$ = true;
    return guard;
  }
  
  synchronized static public void 
  printLnMethodEnterLog(String className, String methodName, String methodDesc,
      String instruction, String tag) {
    if(getUnsetGuardCondition(className, methodName, methodDesc)) 
      unsetGuard1();
    if(methodName.startsWith("main")) {
      unsetGuard1();
    }

    if($guard1$) return;
    boolean guard = guard();
    
    if(Thread.currentThread().getId() == thread) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    $guard1$ = guard;
  }

  synchronized static public void 
  printLnMethodExitLog(String className, String methodName, String methodDesc, 
      String instruction, String tag) {
    if($guard1$) return;
    
    boolean guard = guard();    

    
    if(Thread.currentThread().getId() == thread) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    $guard1$ = guard;

    if(getSetGuardCondition(className, methodName, methodDesc)) {
      setGuard1();
    }
  }
  
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Thread.currentThread().getId() == thread) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    reguard(guard);
  }
  
  synchronized static public void printTraceCount() {
    $guard1$ = true;
    System.out.println("Trace Size:" + count);
    long t = System.currentTimeMillis() - time;
    System.out.println("Time Taken:" + t);
    time = System.currentTimeMillis();
  }
  
  
  synchronized static public 
  String getHash(Object obj) {
    return String.valueOf(System.identityHashCode(obj));
  }
  
  /**************************isMain(String[])?**************************/

  synchronized static public void unsetGuard1() {
    thread = Thread.currentThread().getId();
    time = System.currentTimeMillis();
    $guard1$ = false;
  }

  synchronized static public void setGuard1() {
    printTraceCount();
    $guard1$ = true;
  }
  
  synchronized private static boolean getUnsetGuardCondition(String className, 
      String methodName, String methodDesc) {
    MethodProperties mid = 
        new MethodProperties(className, methodName, 1, methodDesc);
    boolean condition = getUnsetGuardCondition(mid);
    return condition;
  }
  
  synchronized private static boolean getSetGuardCondition(String className,
      String methodName, String methodDesc) {
    MethodProperties mid = 
        new MethodProperties(className, methodName, 1, methodDesc);
    boolean condition = getSetGuardCondition(mid);
    return condition; 
  }
  
  /**
   * Checks if the method is main(String[]); If so it returns a true value 
   * suggesting that we unset the guard that prevents the execution of the 
   * probes that were placed via instrumentation.
   * @return 
   * True if current method is main(String[])
   * False if current method is not main(String[]) <br/>
   * current method is defined using mid.MethodName and mid.MethodDescription
   */
  synchronized public static boolean 
  getUnsetGuardCondition(MethodProperties mid) {
    
    boolean regCondition = mid.MethodName.equals("main")  &&
        mid.MethodDescription.equals("([Ljava/lang/String;)V");
    return regCondition;
    
    
  }

  /**
   * Checks if the method is main(String[]); If so it returns a true value
   * suggesting that we set the guard that prevents the execution of the
   * probes that were placed via instrumentation.
   * @return
   * True if current method is main(String[])
   * False if current method is not main(String[]) <br/>
   * current method is defined using mid.MethodName and mid.MethodDescription
   */
  synchronized public static boolean 
  getSetGuardCondition(MethodProperties mid) {
    boolean regular = (mid.MethodName.equals("main") 
        || mid.MethodName.equals("realMain"))  
        && mid.MethodDescription.equals("([Ljava/lang/String;)V");
    return regular;
  }
  
  /**************************Trace Logging**************************/
  
    synchronized static private void handleLog(String log) {
      log = modifyLog(log, ++count);
      System.out.print(log);
    }
    
    synchronized static private String modifyLog(String log, long count) {
      int firstAsterix = -1;
      int secondAsterix = -1;
      for(int i = 0; i < log.length(); i += 1) {
        if(log.charAt(i) != '*') 
          continue;
        
        if(firstAsterix == -1) {
          firstAsterix = i;
        } else {
          secondAsterix = i;
          break;
        }
      }
      
      long time = System.currentTimeMillis() - Profiler.time;
      
      String logPrefix = log.substring(firstAsterix, secondAsterix + 1);
      StringBuffer replacement = new StringBuffer();
      
      replacement.append(logPrefix)
                 .append(count).append(",")
                 .append(time).append(","); 
      return log.replace(logPrefix, replacement);
    }
  
}
