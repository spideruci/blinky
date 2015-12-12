package org.spideruci.analysis.dynamic;

import java.io.PrintStream;
import java.lang.reflect.Array;

import org.spideruci.analysis.statik.instrumentation.ClassInstrumenter;
import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;

public class Profiler {

  public static int latestLineNumber = 0;
  public static boolean $guard1$ = false;
  
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
  
  public static final boolean log = true;
  
  public static final PrintStream REAL_OUT = System.out;
  public static final PrintStream REAL_ERR = System.err;
  
  public static String entryMethod = null;
  public static String entryClass = null;
  
  private static long thread = -1;
  private static int count = 0;
  private static long time = 0;
  
  synchronized static private void setLogFlags(final boolean value) {
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
        logSwitch = value;
  }

  synchronized static public void initProfiler(String args) {
    
    if(args == null || args.isEmpty()) {
      setLogFlags(true);
      Deputy.checkInclusionList = false;
      return;
    }
    
    System.out.println(args);
    String[] split = args.split(",");
    
    
    String profileConfig = split[0];
    
    if(profileConfig.startsWith("0")) {
      setLogFlags(false);
      Deputy.checkInclusionList = false;
      return;
    }
    
    if(profileConfig.equals("A")) {
      setLogFlags(true);
      profileConfig = "";
    }
    
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
    displayBits(bits);

    for(int count = 1; count < split.length; count += 1) {
      String arg = split[count]; 
      if(arg == null || arg.length() == 0) {
        continue;
      }
      String[] arg_split = arg.split("=");
      String arg_name = arg_split[0];
      String arg_value = arg_split.length == 1 ? "" : arg_split[1].trim().toLowerCase();
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
        Deputy.allowRetransform = true;
        break;
       default:
         break;
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
  
  public static final String PROFILER_METHODENTER = "printLnMethodEnterLog";
  synchronized static public void 
  printLnMethodEnterLog(String className, String methodName, String instruction,
      String tag) {
    if(getUnsetGuardCondition(className, methodName)) {
//        || methodName.startsWith("main")) {
      unsetGuard1();
    }

    if($guard1$) return;
    boolean guard = guard();
    
    if(logMethodEnter) {
      handleLog(instruction, tag, EventType.$enter$);
    }
    $guard1$ = guard;
  }
  
  public static final String METHODEXIT = "printLnMethodExitLog";
  synchronized static public void 
  printLnMethodExitLog(String className, String methodName, String instruction, 
      String tag) {
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
      handleLog(instruction, tag, EventType.$invoke$);
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
  
  synchronized static public void printlnField(String fieldId, 
      String fieldOwnerId, String instruction, String tag) {
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
      REAL_OUT.println("arraylength: " + length);
      String elementId;
      
      String arrayType = arrayref.getClass().getName();
      if(arrayType.length() == 2) {
        elementId = String.valueOf(element);
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
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if($guard1$) return;
    boolean guard = guard();
    if(logSourceLineNumber) {
      handleLog(instruction, tag, EventType.$line$);
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
  
    synchronized static private void handleLog(String insnId, String tag,
        EventType insnType) {
      long threadId = Thread.currentThread().getId();
      long time = System.currentTimeMillis() - Profiler.time;
      TraceEvent event = EventBuilder.buildInsnExecEvent(++count, 
          threadId, tag, insnId, insnType, time);
      REAL_OUT.println(event.getLog());
    }
    
    synchronized static private void handleArrayLog(String insnId, String tag,
        EventType insnType, int arrayrefId, int index, String elementId, int length) {
      
      long threadId = Thread.currentThread().getId();
      long time = System.currentTimeMillis() - Profiler.time;
      TraceEvent event = EventBuilder.buildArrayInsnExecEvent(++count, threadId, 
          tag, insnId, insnType, time, arrayrefId, index, elementId, length);
      REAL_OUT.println(event.getLog());
    }
    
    synchronized static private void handleVarLog(String insnId, String tag, 
        String varId) {
      long threadId = Thread.currentThread().getId();
      long time = System.currentTimeMillis() - Profiler.time;
      TraceEvent event = EventBuilder.buildVarInsnExecEvent(++count, threadId, 
          tag, insnId, EventType.$var$, time, varId);
      REAL_OUT.println(event.getLog());
    }
    
    synchronized static private void handleFieldLog(String insnId, String tag,
        String fieldId, String fieldOwnerId) {
      long threadId = Thread.currentThread().getId();
      long time = System.currentTimeMillis() - Profiler.time;
      TraceEvent event = EventBuilder.buildFieldInsnExecEvent(++count, threadId, 
          tag, insnId, EventType.$field$, time, fieldId, fieldOwnerId);
      REAL_OUT.println(event.getLog());
    }
}
