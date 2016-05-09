package org.spideruci.analysis.trace;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.events.props.ArrayInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.DeclPropNames;
import org.spideruci.analysis.trace.events.props.EnterExecPropNames;
import org.spideruci.analysis.trace.events.props.FieldInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.InsnExecPropNames;
import org.spideruci.analysis.trace.events.props.InsnPropNames;
import org.spideruci.analysis.trace.events.props.InvokeInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.VarInsnExecPropNames;

public class EventBuilder {
 
  private static TraceEvent setupBasicExecProperties(TraceEvent event, int id, 
      String dynamicHostId, String insnId, long[] vitalState, 
      EventType insnType) {
    
    long threadId = vitalState[Profiler.THREAD_ID];
    long timestamp = vitalState[Profiler.TIMESTAMP];
    long calldepth = vitalState[Profiler.CALLDEPTH];
    
    event.setExecInsnDynHost(dynamicHostId);
    event.setExecInsnEventId(insnId);
    event.setExecThreadId(String.valueOf(threadId));
    event.setExecTimestamp(String.valueOf(timestamp));
    event.setExecCalldepth(String.valueOf(calldepth));
    event.setExecInsnType(insnType);
    
    return event;
  }
  
  public static TraceEvent buildInsnExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState) {
    TraceEvent event = TraceEvent.createInsnExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    return event;
  }
  
  public static TraceEvent buildEnterExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState, String runtimeSignature) {
    TraceEvent event = TraceEvent.createEnterExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    event.setProp(EnterExecPropNames.RUNTIME_SIGNATURE, runtimeSignature);
    return event;
  }
  
  public static TraceEvent buildInvokeInsnExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState, String runtimeSignature) {
    TraceEvent event = TraceEvent.createInvokeInsnExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    event.setProp(InvokeInsnExecPropNames.RUNTIME_SIGNATURE, runtimeSignature);
    return event;
  }
  
  public static TraceEvent buildArrayInsnExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState,
      int arrayRefId, int index, String arrayElement, int arraylength) {
    TraceEvent event = TraceEvent.createArrayInsnExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    
    event.setProp(ArrayInsnExecPropNames.ARRAY_ELEMENT, arrayElement);
    event.setProp(ArrayInsnExecPropNames.ARRAYREF_ID, String.valueOf(arrayRefId));
    event.setProp(ArrayInsnExecPropNames.ELEMENT_INDEX, String.valueOf(index));
    event.setProp(ArrayInsnExecPropNames.ARRAY_LENGTH, String.valueOf(arraylength));
    return event;
  }
  
  public static TraceEvent buildVarInsnExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState, String varId) {
    TraceEvent event = TraceEvent.createVarInsnExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    event.setProp(VarInsnExecPropNames.VAR_ID, varId);
    return event;
  }
  
  public static TraceEvent buildFieldInsnExecEvent(int id, String dynamicHostId, 
      String insnId, EventType insnType, long[] vitalState,
      String fieldId, String fieldOwnerId) {
    TraceEvent event = TraceEvent.createFieldInsnExecEvent(id);
    event = setupBasicExecProperties(event, id, dynamicHostId, insnId, vitalState, insnType);
    event.setProp(FieldInsnExecPropNames.FIELD_ID, fieldId);
    event.setProp(FieldInsnExecPropNames.FIELD_OWNER_ID, fieldOwnerId);
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
  
  public static String buildInstructionLog(int byteIndex, int lineNum, EventType type, 
      int opcode, int declHostId) {
    return buildInstructionLog(byteIndex, lineNum, type, opcode, declHostId, null, null);
  }
  
  public static String buildInstructionLog(int byteIndex, int lineNum, EventType type, 
      int opcode, int declHostId, String operand) {
    return buildInstructionLog(byteIndex, lineNum, type, opcode, declHostId, operand, null);
  }
  
  public static String buildInstructionLog(int byteIndex, int lineNum, EventType type,
      int opcode, int declHostId, String op1, String op2) {
    return buildInstructionLog(byteIndex, lineNum, type, opcode, declHostId, op1, op2, null);
  }
  
  public static String buildInstructionLog(int byteIndex, int lineNum, EventType type,
      int opcode, int declHostId, String op1, String op2, String op3) {
    final int insnId = Count.anotherInsn();
    TraceEvent insnEvent = TraceEvent.createInsnEvent(insnId, type);
    insnEvent.setProp(InsnPropNames.DECL_HOST_ID, String.valueOf(declHostId));
    insnEvent.setProp(InsnPropNames.LINE_NUMBER, String.valueOf(lineNum));
    insnEvent.setProp(InsnPropNames.BYTECODE_INDEX, String.valueOf(byteIndex));
    insnEvent.setProp(InsnPropNames.OPCODE, String.valueOf(opcode));
    insnEvent.setProp(InsnPropNames.OPERAND1, op1);
    insnEvent.setProp(InsnPropNames.OPERAND2, op2);
    insnEvent.setProp(InsnPropNames.OPERAND3, op3);

    String instructionLog = insnEvent.getLog();
    if(Profiler.log) {
      Profiler.REAL_OUT.println(instructionLog);
    }
    
    return String.valueOf(insnId);
  }
}
