package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;

import java.io.PrintStream;
import java.lang.reflect.Array;

import org.spideruci.analysis.statik.instrumentation.ClassInstrumenter;
import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;

public class Profiler {

  public static int latestLineNumber = 0;
  public static int latestBytecodeIndex = 0;
  public static boolean $guard1$ = true;
  
  public static boolean SAFEMODE = false;
  
  public static boolean logMethodEnter = false;
  public static boolean logMethodExit = false;
  public static boolean logMethodInvoke = false;
  public static boolean logSourceLineNumber = false;
  public static boolean logVar = false;
  public static boolean logZero = false;
  public static boolean logJump = false;
  public static boolean logField = false;
  public static boolean logConstant = false;
  public static boolean logType = false;
  public static boolean logSwitch = false;
  public static boolean logInvokeRuntimeSign = false;
  public static boolean logEnterRuntimeSign = false;
  
  public static boolean log = true;
  
  public static PrintStream REAL_OUT = System.out;
  public static PrintStream REAL_ERR = System.err;
  
  public static String entryMethod = null;
  public static String entryClass = null;
  public static boolean stopAppInsn = false;
  public static boolean rtOnly = false;
  public static boolean callDepth = false;
  public static boolean useSourcefileName = false;
  
  private static long thread = -1;
  private static int count = 0;
  private static long time = 0;

  
  synchronized static public void setLogFlags(final boolean value) {
    logMethodEnter = 
        logMethodExit = 
        logMethodInvoke = 
        logSourceLineNumber = 
        logVar = 
        logJump = 
        logZero = 
        logConstant = 
        logField = 
        logType = 
        logSwitch = 
//        logInvokeRuntimeSign = 
        logEnterRuntimeSign = 
        log = value;
  }

  synchronized static public void initProfiler(String args) {
    if(REAL_ERR == null) {
      REAL_ERR = System.err;
    }
    
    if(REAL_OUT == null) {
      REAL_OUT = System.out;
    }
    
    
    if(args == null || args.isEmpty()) {
      setLogFlags(true);
      Deputy.checkInclusionList = false;
      return;
    }
    
    System.out.println(args);
    String[] split = args.split(",");
    
    
    String profileConfig = split[0];
    
    if(profileConfig.equals("0")) {
      setLogFlags(false);
      profileConfig = "";
    } else if(profileConfig.equals("A")) {
      setLogFlags(true);
      profileConfig = "";
    }
    
    char[] processedArgs = profileConfig.trim().toCharArray();
    
    for(char arg : processedArgs) {
      switch(arg) {
      case 'E':
        logEnterRuntimeSign = true;
      case 'e':
        logMethodEnter = true;
        continue;
      case 'x':
        logMethodExit = true;
        continue;
      case 'l':
        logSourceLineNumber = true;
        continue;
      case 'I':
        logInvokeRuntimeSign = true;
      case 'i': 
        logMethodInvoke = true;
        continue;
      case 'v':
        logVar = true;
        continue;
      case 'z':
        logZero = true;
        continue;
      case 'j':
        logJump = true;
        continue;
      case 'f':
        logField = true;
        continue;
      case 'c':
        logConstant = true;
        continue;
      case 't':
        logType = true;
        continue;
      case 's':
        logSwitch = true;
        continue;
      default: continue;
      }
    }

    for(int count = 1; count < split.length; count += 1) {
      String arg = split[count]; 
      if(arg == null || arg.length() == 0) {
        continue;
      }
      String[] arg_split = arg.split("=");
      String arg_name = arg_split[0];
      String arg_value = arg_split.length == 1 ? "" : arg_split[1];
      REAL_OUT.printf("'%s':%s\n", arg_name , arg_value);
      
      switch(arg_name) {
      case "whitelist":
        Deputy.checkInclusionList = true;
        break;
      case "entry-method":
        entryMethod = arg_value;
        break;
      case "entry-class":
        entryClass = arg_value;
        break;
      case "frames":
        ClassInstrumenter.FRAMES = true;
        break;
      case "retransform":
        Premain.allowRetransform = true;
        break;
      case "stop-app-ins":
        Profiler.stopAppInsn = true;
        break;
      case "control":
        ClassInstrumenter.CONTROL_FLOW = true;
        break;
      case "safe":
        Profiler.SAFEMODE = true;
        break;
      case "calldepth":
        Profiler.callDepth = true;
        break;
      case "sourcename":
        Profiler.useSourcefileName = true;
       default:
         break;
      }
    }
  }
  
  public static final String REGUARD = "reguard";
  synchronized static public void reguard(boolean guard) {
    $guard1$ = false; // TODO shouldn't this be guard instead of false?
  }
  
  public static final String GUARD = "guard";
  synchronized static public boolean guard() {
    boolean guard = $guard1$;
    $guard1$ = true;
    return guard;
  }
  
  public static final String METHODENTER = "printLnMethodEnterLog";
  synchronized static public void printLnMethodEnterLog(String className, 
      String methodName, String instruction, String tag) {
    if(getUnsetGuardCondition(className, methodName)) {
//        || methodName.startsWith("main")) {
      unsetGuard1();
    }

    if($guard1$) return;
    boolean guard = guard();
    
    if(logMethodEnter) {
      handleEnterLog(instruction, tag, EventType.$enter$);
    }
    $guard1$ = guard;
  }
  
  public static final String METHODEXIT = "printLnMethodExitLog";
  synchronized static public void printLnMethodExitLog(String className, 
      String methodName, String instruction, String tag) {
    if($guard1$) return;
    
    boolean guard = guard();
    
    if(logMethodExit) {
      handleLog(instruction, tag, EventType.$exit$);
    }
    $guard1$ = guard;

    if(getSetGuardCondition(className, methodName)) {
      setGuard1();
    }
  }
  
  public static final String INVOKE = "printlnInvokeLog";
  synchronized static public void printlnInvokeLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logMethodInvoke) {
      handleInvokeLog(instruction, tag, EventType.$invoke$);
    }
    reguard(guard);
  }
  
  public static final String COMPLETE = "printlnCompleteLog";
  synchronized static public void printlnCompleteLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logMethodInvoke) {
      handleLog(instruction, tag, EventType.$complete$);
    }
    reguard(guard);
  }
  
  public static final String VAR = "printlnVarLog";
  synchronized static public void printlnVarLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logVar) {
      handleLog(instruction, tag, EventType.$var$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnVarLog(String varId, String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logVar) {
      handleVarLog(instruction, tag, varId);
    }
    reguard(guard);
  }
  
  public static final String FIELD = "printlnField";
  synchronized static public void printlnField(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logField) {
      handleLog(instruction, tag, EventType.$field$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnField(String fieldOwnerId, 
      String fieldId, String instruction, String tag) {
//  synchronized static public void printlnField(String fieldId, 
//      String fieldOwnerId, String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logField) {
      handleFieldLog(instruction, tag, fieldId, fieldOwnerId);
    }
    reguard(guard);
  }
  
  public static final String ZERO_OP = "printlnZeroOpLog";
  synchronized static public void printlnZeroOpLog(
      String instruction, String tag, String type) {
    if($guard1$) return;
    boolean guard = guard();
    if(logZero) {
      handleLog(instruction, tag, EventType.valueOf(type));
    }
    reguard(guard);
  }
  
  public static final String CONSTANT = "printlnConstantLog";
  synchronized static public void printlnConstantLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logConstant) {
      handleLog(instruction, tag, EventType.$constant$);
    }
    reguard(guard);
  }
  
  public static final String TYPE = "printlnTypeLog";
  synchronized static public void printlnTypeLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logType) {
      handleLog(instruction, tag, EventType.$type$);
    }
    reguard(guard);
  }
  
  public static final String SWITCH = "printlnSwtichLog";
  synchronized static public void printlnSwitchLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logSwitch) {
      handleLog(instruction, tag, EventType.$switch$);
    }
    reguard(guard);
  }  
  
  public static final String ARRAY = "printlnArrayLog";
  synchronized static public void printlnArrayLog(Object arrayref, int index,
      String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logZero) {
      
      Object element = Array.get(arrayref, index);
      int length = Array.getLength(arrayref);
//      REAL_OUT.println("arraylength: " + length);
      String elementId;
      
      String arrayType = arrayref.getClass().getName();
      if(arrayType.length() == 2) {
        elementId = "0";
      } else {
        elementId = String.valueOf(System.identityHashCode(element));
      }
      
      handleArrayLog(instruction, tag, EventType.$arrayload$,
          System.identityHashCode(arrayref), index, elementId, length);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnArrayLog(Object arrayref, int index,
      String elementId, String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logZero) {
      int length = Array.getLength(arrayref);
      String arrayType = arrayref.getClass().getName();
      if(arrayType.length() == 2) {
        elementId = "0";
      }
      
      REAL_OUT.println("arraylength: " + length);
      handleArrayLog(instruction, tag, EventType.$arraystore$,
          System.identityHashCode(arrayref), index, elementId, length);
    }
    reguard(guard);
  }
  
  public static final String JUMP = "printlnJumpLog";
  synchronized static public void printlnJumpLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logJump) {
      handleLog(instruction, tag, EventType.$jump$);
    }
    reguard(guard);
  }
  
  public static final String IINC = "printlnIinc";
  synchronized static public void printlnIinc(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logVar) {
      handleLog(instruction, tag, EventType.$iinc$);
    }
    reguard(guard);
  }
  
  public static final String LINENUMER = "printLnLineNumber";
  public static final String LINENUMER_DESC = "(" + Deputy.STRING_DESC + Deputy.STRING_DESC + ")V";
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logSourceLineNumber) {
      handleLog(instruction, tag, EventType.$line$);
    }
    reguard(guard);
  }
  
  public static final String ARGLOG = "printLnArgLog";
  synchronized static public void printLnArgLog(String argType, String index, 
      boolean isFirst, boolean isLast) {
    if($guard1$) return;
    boolean guard = guard();
    if(logEnterRuntimeSign) {
//      long[] vitalState = getVitalExecState();
//      long threadId = vitalState[THREAD_ID];
//      long time = vitalState[TIMESTAMP];
//      REAL_OUT.print("$$$," + ++count + "," + threadId + "," + time + ",");
      
      handleArgLog(argType, index, EventType.$argtype$, true, true);
    }
    reguard(guard);
  }
  
  public static final String INVOKE_ARGLOG = "printLnInvokeArgLog";
  synchronized static public void printLnInvokeArgLog(String argType, String index) {
    if($guard1$) return;
    boolean guard = guard();
    if(logInvokeRuntimeSign) {
      handleArgLog(argType, index, EventType.$invokeargtype$, true, true);
    }
    reguard(guard);
  }
  
  /**
   * Check for specific threads. Use only while debugging.
   * @return true if the current thread is one of the specified thread id's.
   */
  @SuppressWarnings("unused")
  synchronized static private boolean threadCheck(long ... threadIds) {
    long currentThreadId = Thread.currentThread().getId();
    
    if(currentThreadId == thread) {
      return true;
    }
    
    for(long threadId : threadIds) {
      if(currentThreadId == threadId) {
        return true;
      }
    }
    
    return false;
  }
  
  synchronized static public void printTraceCount() {
    $guard1$ = true;
    REAL_OUT.println("Trace Size:" + count);
    long t = System.currentTimeMillis() - time;
    REAL_OUT.println("Time Taken:" + t);
    time = System.currentTimeMillis();
  }
  
  public static final String GETHASH = "getHash";
  public static final String GETHASH_DESC = "(" + Deputy.OBJECT_DESC + ")" + Deputy.STRING_DESC;
  synchronized static public 
  String getHash(Object obj) {
    int id = System.identityHashCode(obj);
    return String.valueOf(id);
  }
  
  public static final String GETTYPENAME = "getTypeName";
  public static final String GETTYPENAME_DESC = "(" + Deputy.OBJECT_DESC + Deputy.STRING_DESC + ")" + Deputy.STRING_DESC;
  synchronized static public String getTypeName(Object obj, String staticType) {
    if(obj == null) {
      return staticType + "#0";
    } else {
      return obj.getClass().getName() + "#" + System.identityHashCode(obj);
    }
  }
  
  public static final String GET_ARRAYTYPENAME = "getArrayTypeName";
  synchronized static public String getArrayTypeName(Object array, String staticType) {
    if(array == null) {
      return staticType + "#0";
    } else {
      return staticType + "#" + System.identityHashCode(array);
    }
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
  getUnsetGuardCondition(String ownerName, String methodName) {
    if(entryMethod != null && entryClass != null) {
      return methodName.equals(entryMethod) && entryClass.equals(ownerName);
    } else if(entryMethod != null) {
      return methodName.equals(entryMethod);
    }
    
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
    if(entryMethod != null && entryClass != null) {
      return methodName.equals(entryMethod) && entryClass.equals(ownerName);
    } else if(entryMethod != null) {
      return methodName.equals(entryMethod);
    }
    
    boolean regular = methodName.equals("main([Ljava/lang/String;)V") 
        || methodName.equals("realMain([Ljava/lang/String;)V");
    regular =  (methodName.equals("run()V") &&
        ownerName.equals("net/percederberg/tetris/Game$GameThread"));
    return regular;
  }
  
  /**************************Trace Logging**************************/
  
        public static final int THREAD_ID = 0;
        public static final int TIMESTAMP = 1;
        public static final int CALLDEPTH = 2;
      
        /**
         * @return new long[] {time-stamp, current-thread-id, calldepth}
         */
        synchronized private static long[] getVitalExecState() {
          Thread currentThread = Thread.currentThread();
          
          long time = System.currentTimeMillis() - Profiler.time;
          long threadId = currentThread.getId();

          long calldepth = -1;
          if(Profiler.callDepth) {
            StackTraceElement[] x = currentThread.getStackTrace();
            /**
             * A subtraction of 4 from the stack trace length is done to 
             * account for the following 4 methods in the call stack:
             * - getStackTrace
             * - getVitalExecState
             * - handleXLog
             * - printlnXLog
             */
            calldepth = x.length - 4;
          }
          
          return new long[] { threadId, time, calldepth };
        }
    
    synchronized static private void handleEnterLog(String insnId, String tag,
        EventType insnType) {
      long[] vitalState = getVitalExecState();
      
      final String runtimeSignature = RuntimeTypeProfiler.getEnterRuntimeSignature(null);
      TraceEvent event = EventBuilder.buildEnterExecEvent(++count, tag, insnId, 
          insnType, vitalState, runtimeSignature);
      printEventlog(event);
    }

    synchronized static private void handleInvokeLog(String insnId, String tag,
        EventType insnType) {
      long[] vitalState = getVitalExecState();
      
      final String runtimeSignature = 
          RuntimeTypeProfiler.getInvokeRuntimeSignature();
      TraceEvent event = EventBuilder.buildInvokeInsnExecEvent(++count, tag, 
          insnId, insnType, vitalState, runtimeSignature);
      printEventlog(event);
    }
    
    synchronized static private void handleLog(String insnId, String tag,
        EventType insnType) {
      long[] vitalState = getVitalExecState();

      TraceEvent event = EventBuilder.buildInsnExecEvent(++count, tag, insnId, 
          insnType, vitalState);
      printEventlog(event);
    }
    
    synchronized static private void handleArrayLog(String insnId, String tag,
        EventType insnType, int arrayrefId, int index, String elementId, int length) {
      long[] vitalState = getVitalExecState();

      TraceEvent event = EventBuilder.buildArrayInsnExecEvent(++count, tag, 
          insnId, insnType, vitalState, arrayrefId, index, elementId, length);
      printEventlog(event);
    }
    
    synchronized static private void handleVarLog(String insnId, String tag, 
        String varId) {
      long[] vitalState = getVitalExecState();

      TraceEvent event = EventBuilder.buildVarInsnExecEvent(++count, tag, 
          insnId, EventType.$var$, vitalState, varId);
      printEventlog(event);
    }
    
    synchronized static private void handleFieldLog(String insnId, String tag,
        String fieldId, String fieldOwnerId) {
      long[] vitalState = getVitalExecState();

      TraceEvent event = EventBuilder.buildFieldInsnExecEvent(++count, tag, 
          insnId, EventType.$field$, vitalState, fieldId, fieldOwnerId);
      printEventlog(event);
    }
    
    synchronized static private void handleArgLog(String argType, String index, 
        EventType type, boolean isFirst, boolean isLast) {
      
      if(isFirst) {
        long[] vitalState = getVitalExecState();
        long threadId = vitalState[THREAD_ID];
        long timestamp = vitalState[TIMESTAMP];
        long calldepth = vitalState[CALLDEPTH];
        REAL_OUT.print("$$$," + ++count + "," 
            + threadId + "," + timestamp + "," + calldepth + ",");
      }
      
      REAL_OUT.print(argType + "," + index + "," + type.toString() + 
          (isLast? "\n" : ","));
      
    }
    
    synchronized static private void printEventlog(TraceEvent event) {
      int insnId = Integer.parseInt(event.getExecInsnEventId());
      if(Profiler.stopAppInsn && insnId >= 0) {
        return;
      }
      REAL_OUT.println(event.getLog());
    }
    
}