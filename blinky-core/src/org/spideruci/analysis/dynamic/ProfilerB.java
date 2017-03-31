package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;

import static org.spideruci.analysis.dynamic.Profiler.$guard1$;
import static org.spideruci.analysis.dynamic.Profiler.reguard;
import static org.spideruci.analysis.dynamic.Profiler.guard;

import static org.spideruci.analysis.dynamic.TraceLogger.handleLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleVarLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleArgLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleArrayLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleEnterLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleFieldLog;
import static org.spideruci.analysis.dynamic.TraceLogger.handleInvokeLog;

import java.lang.reflect.Array;

import org.spideruci.analysis.trace.EventType;

public class ProfilerB {
  
  public static final String ACTIVE_FLAG_NAME = "isActive";
  public static final ThreadedBool isActive = new ThreadedBool();

  synchronized static public void printLnMethodEnterLog(String className, 
      String methodName, String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }

    if($guard1$) return;
    boolean guard = guard();
    
    if(Profiler.logMethodEnter) {
      handleEnterLog(instruction, tag, EventType.$enter$);
    }
    $guard1$ = guard;
  }
  
  synchronized static public void printLnMethodExitLog(String className, 
      String methodName, String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    
    boolean guard = guard();
    
    if(Profiler.logMethodExit) {
      handleLog(instruction, tag, EventType.$exit$);
    }
    $guard1$ = guard;
  }
  
  synchronized static public void printlnInvokeLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logMethodInvoke) {
      handleInvokeLog(instruction, tag, EventType.$invoke$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnCompleteLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logMethodInvoke) {
      handleLog(instruction, tag, EventType.$complete$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnVarLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleLog(instruction, tag, EventType.$var$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnVarLog(String varId, String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleVarLog(instruction, tag, varId);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnField(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logField) {
      handleLog(instruction, tag, EventType.$field$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnField(String fieldOwnerId, 
      String fieldId, String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }

    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logField) {
      handleFieldLog(instruction, tag, fieldId, fieldOwnerId);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnZeroOpLog(
      String instruction, String tag, String type) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logZero) {
      handleLog(instruction, tag, EventType.valueOf(type));
    }
    reguard(guard);
  }
  
  synchronized static public void printlnConstantLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logConstant) {
      handleLog(instruction, tag, EventType.$constant$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnTypeLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logType) {
      handleLog(instruction, tag, EventType.$type$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnSwitchLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logSwitch) {
      handleLog(instruction, tag, EventType.$switch$);
    }
    reguard(guard);
  }  
  
  synchronized static public void printlnArrayLog(Object arrayref, int index,
      String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
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
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logZero) {
      int length = Array.getLength(arrayref);
      String arrayType = arrayref.getClass().getName();
      if(arrayType.length() == 2) {
        elementId = "0";
      }
      
      synchronized (REAL_OUT) {
        REAL_OUT.println("arraylength: " + length);
      }
      
      handleArrayLog(instruction, tag, EventType.$arraystore$,
          System.identityHashCode(arrayref), index, elementId, length);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnJumpLog(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logJump) {
      handleLog(instruction, tag, EventType.$jump$);
    }
    reguard(guard);
  }
  
  synchronized static public void printlnIinc(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logVar) {
      handleLog(instruction, tag, EventType.$iinc$);
    }
    reguard(guard);
  }
  
  synchronized static public void printLnLineNumber(String instruction, String tag) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logSourceLineNumber) {
      handleLog(instruction, tag, EventType.$line$);
    }
    reguard(guard);
  }
  
  synchronized static public void printLnArgLog(String argType, String index, 
      boolean isFirst, boolean isLast) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logEnterRuntimeSign) {
      handleArgLog(argType, index, EventType.$argtype$, true, true);
    }
    reguard(guard);
  }
  
  synchronized static public void printLnInvokeArgLog(String argType, String index) {
    if(!isActive.get()) {
      return;
    }
    
    if($guard1$) return;
    boolean guard = guard();
    if(Profiler.logInvokeRuntimeSign) {
      handleArgLog(argType, index, EventType.$invokeargtype$, true, true);
    }
    reguard(guard);
  }
  
  synchronized static public String getHash(Object obj) {
    int id = System.identityHashCode(obj);
    return String.valueOf(id);
  }
  
  synchronized static public String getTypeName(Object obj, String staticType) {
    if(obj == null) {
      return staticType + "#0";
    } else {
      return obj.getClass().getName() + "#" + System.identityHashCode(obj);
    }
  }
  
  synchronized static public String getArrayTypeName(Object array, String staticType) {
    if(array == null) {
      return staticType + "#0";
    } else {
      return staticType + "#" + System.identityHashCode(array);
    }
  }

  public static final String ACTIVATE = "activate";
  synchronized static public void activate() {
    isActive.set(true);
  }
  
  public static final String DEACTIVATE = "deactivate";
  synchronized static public void deactivate() {
    isActive.set(false);
  }
}