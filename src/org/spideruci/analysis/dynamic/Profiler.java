package org.spideruci.analysis.dynamic;

import java.io.PrintStream;

import org.spideruci.analysis.statik.instrumentation.Deputy;

public class Profiler {

  public static int latestLineNumber = 0;
  public static boolean $guard1$ = false;
  
  public static boolean logMethodEnter = false;
  public static boolean logMethodExit = false;
  public static boolean logMethodInvoke = false;
  public static boolean logSourceLineNumber = false;
  
  public static final PrintStream REAL_OUT = System.out;
  public static final PrintStream REAL_ERR = System.err;
  
  private static long thread = -1;
  private static long count = 0;
  private static long time = 0;

  synchronized static public void initProfiler(String args) {
    
    if(args == null || args.isEmpty()) {
      logMethodEnter = logMethodExit = logMethodInvoke = logSourceLineNumber = true;
      Deputy.checkInclusionList = false;
      return;
    }
    
    String[] split = args.split(",");
    
    String profileConfig = split[0];
    
    char[] processedArgs = profileConfig.trim().toLowerCase().toCharArray();
    
    int bits = 0;
    
    for(char arg : processedArgs) {
      int index = ((int)arg) - 97;
       
      if(getBit(bits, index) == 1) {
        continue;
      } else {
        bits = setBit(bits, index);
      }
      
      displayBits(bits);
      
      switch(arg) {
      case 'e':
        logMethodEnter = true;
        continue;
      case 'x':
        logMethodExit = true;
        continue;
      case 'l':
        logSourceLineNumber = true;
        continue;
      case 'i': 
        logMethodInvoke = true;
        continue;
      default: continue;
      }
    }
    displayBits(bits);
    
    for(int count = 1; count < split.length; count += 1) {
      String arg = split[count]; 
      if(arg == null || arg.length() == 0) {
        continue;
      }
      String[] arg_split = arg.split("=");
      String arg_name = arg_split[0];
      String arg_value = arg_split[1].trim().toLowerCase();
      REAL_OUT.printf("'%s':%s\n", arg_name , arg_value);
      
      switch(arg_name) {
      case "whitelist":
      {
        if(arg_value.equals("true")) {
          Deputy.checkInclusionList = true;
        } else {
          Deputy.checkInclusionList = false;
        }
      }
      }
    }
  }
  
    synchronized private static int getBit(int bits, int index) {
      int mask = 1 << index;
      return ((bits & mask) == 0) ? 0 : 1;
    }
    
    synchronized private static int setBit(int bits, int index) {
      int mask = 1 << index;
      return (bits | mask);
    }
    
    synchronized private static void displayBits(int bits) {
      REAL_OUT.println(Integer.toBinaryString(bits));
    }
  
  synchronized static public void reguard(boolean guard) {
    $guard1$ = false;
  }

  synchronized static public boolean guard() {
    boolean guard = $guard1$;
    $guard1$ = true;
    return guard;
  }
  
  synchronized static public void 
  printLnMethodEnterLog(String className, String methodName, String instruction,
      String tag) {
    if(getUnsetGuardCondition(methodName)) 
      unsetGuard1();
    if(methodName.startsWith("main")) {
      unsetGuard1();
    }

    if($guard1$) return;
    boolean guard = guard();
    
    if(logMethodEnter) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    $guard1$ = guard;
  }

  synchronized static public void 
  printLnMethodExitLog(String className, String methodName, String instruction, 
      String tag) {
    if($guard1$) return;
    
    boolean guard = guard();    
    
    if(logMethodExit) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    $guard1$ = guard;

    if(getSetGuardCondition(className, methodName)) {
      setGuard1();
    }
  }
  
  synchronized static public void printlnInvokeLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if((Thread.currentThread().getId() == thread || Thread.currentThread().getId() == 12) && logMethodInvoke) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    reguard(guard);
  }
  
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
//    System.out.println(Thread.currentThread().getId());
    if(logSourceLineNumber) {
      String log = "*" + Thread.currentThread().getId() + "*" + tag + "," 
          + instruction + "\n";
      handleLog(log);
    }
    reguard(guard);
  }
  
  synchronized static private boolean threadCheck() {
    return Thread.currentThread().getId() == thread 
          || Thread.currentThread().getId() == 13 
          || Thread.currentThread().getId() == 11;
  }
  
  synchronized static public void printTraceCount() {
    $guard1$ = true;
    REAL_OUT.println("Trace Size:" + count);
    long t = System.currentTimeMillis() - time;
    REAL_OUT.println("Time Taken:" + t);
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
  getUnsetGuardCondition(String methodName) {
    boolean regCondition = methodName.equals("main([Ljava/lang/String;)V");
    if(regCondition) {
      REAL_OUT.println(regCondition);
    }
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
  getSetGuardCondition(String ownerName, String methodName) {
    boolean regular = methodName.equals("main([Ljava/lang/String;)V") 
        || methodName.equals("realMain([Ljava/lang/String;)V");
    regular =  (methodName.equals("run()V") &&
        ownerName.equals("net/percederberg/tetris/Game$GameThread"));
    return regular;
  }
  
  /**************************Trace Logging**************************/
  
    synchronized static private void handleLog(String log) {
      log = modifyLog(log, ++count);
      REAL_OUT.print(log);
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
