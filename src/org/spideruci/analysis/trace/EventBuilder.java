package org.spideruci.analysis.trace;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.Count;
import org.spideruci.analysis.trace.eventprops.ArrayInsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.DeclPropNames;
import org.spideruci.analysis.trace.eventprops.InsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.InsnPropNames;
import org.spideruci.analysis.trace.eventprops.VarInsnExecPropNames;

public class EventBuilder {
  
  public static TraceEvent buildInsnExecEvent(int id, long threadId, 
      String dynamicHostId, String insnId, EventType insnType, long timestamp) {
    TraceEvent event = TraceEvent.createInsnExecEvent(id);
    event.setProp(InsnExecPropNames.DYN_HOST_ID, dynamicHostId);
    event.setProp(InsnExecPropNames.INSN_EVENT_ID, insnId);
    event.setProp(InsnExecPropNames.THREAD_ID, String.valueOf(threadId));
    event.setProp(InsnExecPropNames.TIMESTAMP, String.valueOf(timestamp));
    event.setProp(InsnExecPropNames.INSN_EVENT_TYPE, insnType.toString());
    return event;
  }
  
  public static TraceEvent buildArrayInsnExecEvent(int id, long threadId,
      String dynamicHostId, String insnId, EventType insnType, long timestamp,
      int arrayRefId, int index, String arrayElement, int arraylength) {
    TraceEvent event = TraceEvent.createArrayInsnExecEvent(id);
    event.setProp(ArrayInsnExecPropNames.DYN_HOST_ID, dynamicHostId);
    event.setProp(ArrayInsnExecPropNames.INSN_EVENT_ID, insnId);
    event.setProp(ArrayInsnExecPropNames.THREAD_ID, String.valueOf(threadId));
    event.setProp(ArrayInsnExecPropNames.TIMESTAMP, String.valueOf(timestamp));
    event.setProp(ArrayInsnExecPropNames.INSN_EVENT_TYPE, insnType.toString());
    event.setProp(ArrayInsnExecPropNames.ARRAY_ELEMENT, arrayElement);
    event.setProp(ArrayInsnExecPropNames.ARRAYREF_ID, String.valueOf(arrayRefId));
    event.setProp(ArrayInsnExecPropNames.ELEMENT_INDEX, String.valueOf(index));
    event.setProp(ArrayInsnExecPropNames.ARRAY_LENGTH, String.valueOf(arraylength));
    return event;
  }
  
  public static TraceEvent buildVarInsnExecEvent(int id, long threadId,
      String dynamicHostId, String insnId, EventType insnType, long timestamp,
      String varId) {
    TraceEvent event = TraceEvent.createArrayInsnExecEvent(id);
    event.setProp(VarInsnExecPropNames.DYN_HOST_ID, dynamicHostId);
    event.setProp(VarInsnExecPropNames.INSN_EVENT_ID, insnId);
    event.setProp(VarInsnExecPropNames.THREAD_ID, String.valueOf(threadId));
    event.setProp(VarInsnExecPropNames.TIMESTAMP, String.valueOf(timestamp));
    event.setProp(VarInsnExecPropNames.INSN_EVENT_TYPE, insnType.toString());
    event.setProp(VarInsnExecPropNames.VAR_ID, varId);
    return event;
  }
  
  public static TraceEvent buildMethodDecl(String className, int access, String name) {
    TraceEvent methodDecl = 
        TraceEvent.createDeclEvent(Count.anotherMethod(), EventType.$$method$$);
    methodDecl.setProp(DeclPropNames.NAME, name);
    methodDecl.setProp(DeclPropNames.ACCESS, String.valueOf(access));
    methodDecl.setProp(DeclPropNames.OWNER, className);
    return methodDecl;
  }
  
  public static String buildInstructionLog(int lineNum, EventType type, 
      int declHostId, int opcode) {
    return buildInstructionLog(lineNum, type, opcode, declHostId, null, null);
  }
  
  public static String buildInstructionLog(int lineNum, EventType type, 
      int declHostId, int opcode, String operand) {
    return buildInstructionLog(lineNum, type, declHostId, opcode, operand, null);
  }
  
  public static String buildInstructionLog(int lineNum, EventType type,
      int declHostId, int opcode, String op1, String op2) {
    final int insnId = Count.anotherInsn();
    TraceEvent insnEvent = TraceEvent.createInsnEvent(insnId, type);
    insnEvent.setProp(InsnPropNames.DECL_HOST_ID, String.valueOf(declHostId));
    insnEvent.setProp(InsnPropNames.LINE_NUMBER, String.valueOf(lineNum));
    insnEvent.setProp(InsnPropNames.OPCODE, String.valueOf(opcode));
    insnEvent.setProp(InsnPropNames.OPERAND1, op1);
    insnEvent.setProp(InsnPropNames.OPERAND2, op2);

    String instructionLog = insnEvent.getLog();
    if(Profiler.log) {
      Profiler.REAL_OUT.println(instructionLog);
    }
    
    return String.valueOf(insnId);
  }
}
