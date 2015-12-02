package org.spideruci.analysis.trace;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.statik.instrumentation.Count;

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
