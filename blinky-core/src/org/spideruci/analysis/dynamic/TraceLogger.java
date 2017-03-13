package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;

import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;

public class TraceLogger {

  public static final int THREAD_ID = 0;
  public static final int TIMESTAMP = 1;
  public static final int CALLDEPTH = 2;
  
  private static int count = 0;

  synchronized static public void printTraceCount() {
    REAL_OUT.println("Trace Size:" + count);
    long t = System.currentTimeMillis() - TraceLogger.time;
    REAL_OUT.println("Time Taken:" + t);
    TraceLogger.time = System.currentTimeMillis();
  }
  
  /**
   * @return new long[] {time-stamp, current-thread-id, calldepth}
   */
  synchronized private static long[] getVitalExecState() {
    Thread currentThread = Thread.currentThread();

    long time = System.currentTimeMillis() - TraceLogger.time;
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

  synchronized static public void handleEnterLog(String insnId, String tag,
      EventType insnType) {
    long[] vitalState = getVitalExecState();

    final String runtimeSignature = RuntimeTypeProfiler.getEnterRuntimeSignature(null);
    TraceEvent event = EventBuilder.buildEnterExecEvent(++count, tag, insnId, 
        insnType, vitalState, runtimeSignature);
    printEventlog(event);
  }

  synchronized static public void handleInvokeLog(String insnId, String tag,
      EventType insnType) {
    long[] vitalState = getVitalExecState();

    final String runtimeSignature = 
        RuntimeTypeProfiler.getInvokeRuntimeSignature();
    TraceEvent event = EventBuilder.buildInvokeInsnExecEvent(++count, tag, 
        insnId, insnType, vitalState, runtimeSignature);
    printEventlog(event);
  }

  synchronized static public void handleLog(String insnId, String tag,
      EventType insnType) {
    long[] vitalState = getVitalExecState();

    TraceEvent event = EventBuilder.buildInsnExecEvent(++count, tag, insnId, 
        insnType, vitalState);
    printEventlog(event);
  }

  synchronized static public void handleArrayLog(String insnId, String tag,
      EventType insnType, int arrayrefId, int index, String elementId, int length) {
    long[] vitalState = getVitalExecState();

    TraceEvent event = EventBuilder.buildArrayInsnExecEvent(++count, tag, 
        insnId, insnType, vitalState, arrayrefId, index, elementId, length);
    printEventlog(event);
  }

  synchronized static public void handleVarLog(String insnId, String tag, 
      String varId) {
    long[] vitalState = getVitalExecState();

    TraceEvent event = EventBuilder.buildVarInsnExecEvent(++count, tag, 
        insnId, EventType.$var$, vitalState, varId);
    printEventlog(event);
  }

  synchronized static public void handleFieldLog(String insnId, String tag,
      String fieldId, String fieldOwnerId) {
    long[] vitalState = getVitalExecState();

    TraceEvent event = EventBuilder.buildFieldInsnExecEvent(++count, tag, 
        insnId, EventType.$field$, vitalState, fieldId, fieldOwnerId);
    printEventlog(event);
  }

  synchronized static public void handleArgLog(String argType, String index, 
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

  public static long time = 0;

}
