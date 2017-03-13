package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;
import static org.spideruci.analysis.dynamic.TraceLogger.handleLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleVarLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleArgLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleArrayLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleEnterLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleFieldLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleInvokeLog;

import java.lang.reflect.Array;

import org.spideruci.analysis.statik.instrumentation.Config;
import org.spideruci.analysis.trace.EventType;

public class ProfilerB {
  
  public static final String ACTIVE_FLAG_NAME = "isActive";
  public static boolean isActive = false;

  private static boolean $guard1$ = true;
  
  public static boolean log = true;
  
  public static String entryMethod = null;
  public static String entryClass = null;
  public static boolean stopAppInsn = false;
  public static boolean rtOnly = false;
  public static boolean callDepth = false;
  public static boolean useSourcefileName = false;
  
  private static long thread = -1;
  
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
    
    if(Profiler.logMethodEnter) {
      handleEnterLog(instruction, tag, EventType.$enter$);
    }
    $guard1$ = guard;
  }
  
  public static final String METHODEXIT = "printLnMethodExitLog";
  synchronized static public void printLnMethodExitLog(String className, 
      String methodName, String instruction, String tag) {
    if($guard1$) return;
    
    boolean guard = guard();
    
    if(Profiler.logMethodExit) {
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
    if(Profiler.logMethodInvoke) {
      handleInvokeLog(instruction, tag, EventType.$invoke$);
    }
    reguard(guard);
  }
  
  public static final String COMPLETE = "printlnCompleteLog";
  synchronized static public void printlnCompleteLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logMethodInvoke) {
      handleLog(instruction, tag, EventType.$complete$);
    }
    reguard(guard);
  }
  
  public static final String VAR = "printlnVarLog";
  synchronized static public void printlnVarLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleLog(instruction, tag, EventType.$var$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnVarLog(String varId, String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleVarLog(instruction, tag, varId);
    }
    reguard(guard);
  }
  
  public static final String FIELD = "printlnField";
  synchronized static public void printlnField(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logField) {
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
    if(Profiler.logField) {
      handleFieldLog(instruction, tag, fieldId, fieldOwnerId);
    }
    reguard(guard);
  }
  
  public static final String ZERO_OP = "printlnZeroOpLog";
  synchronized static public void printlnZeroOpLog(
      String instruction, String tag, String type) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logZero) {
      handleLog(instruction, tag, EventType.valueOf(type));
    }
    reguard(guard);
  }
  
  public static final String CONSTANT = "printlnConstantLog";
  synchronized static public void printlnConstantLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logConstant) {
      handleLog(instruction, tag, EventType.$constant$);
    }
    reguard(guard);
  }
  
  public static final String TYPE = "printlnTypeLog";
  synchronized static public void printlnTypeLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logType) {
      handleLog(instruction, tag, EventType.$type$);
    }
    reguard(guard);
  }
  
  public static final String SWITCH = "printlnSwtichLog";
  synchronized static public void printlnSwitchLog(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logSwitch) {
      handleLog(instruction, tag, EventType.$switch$);
    }
    reguard(guard);
  }  
  
  public static final String ARRAY = "printlnArrayLog";
  synchronized static public void printlnArrayLog(Object arrayref, int index,
      String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logZero) {
      
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
    if(Profiler.logZero) {
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
    if(Profiler.logJump) {
      handleLog(instruction, tag, EventType.$jump$);
    }
    reguard(guard);
  }
  
  public static final String IINC = "printlnIinc";
  synchronized static public void printlnIinc(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleLog(instruction, tag, EventType.$iinc$);
    }
    reguard(guard);
  }
  
  public static final String LINENUMER = "printLnLineNumber";
  public static final String LINENUMER_DESC = "(" + Config.STRING_DESC + Config.STRING_DESC + ")V";
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logSourceLineNumber) {
      handleLog(instruction, tag, EventType.$line$);
    }
    reguard(guard);
  }
  
  public static final String ARGLOG = "printLnArgLog";
  synchronized static public void printLnArgLog(String argType, String index, 
      boolean isFirst, boolean isLast) {
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logEnterRuntimeSign) {
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
    if(Profiler.logInvokeRuntimeSign) {
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
  
  public static final String GETHASH = "getHash";
  public static final String GETHASH_DESC = "(" + Config.OBJECT_DESC + ")" + Config.STRING_DESC;
  synchronized static public 
  String getHash(Object obj) {
    int id = System.identityHashCode(obj);
    return String.valueOf(id);
  }
  
  public static final String GETTYPENAME = "getTypeName";
  public static final String GETTYPENAME_DESC = "(" + Config.OBJECT_DESC + Config.STRING_DESC + ")" + Config.STRING_DESC;
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
    TraceLogger.time = System.currentTimeMillis();
    $guard1$ = false;
  }

  synchronized static public void setGuard1() {
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

}