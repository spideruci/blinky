package org.spideruci.analysis.trace;

import java.util.Arrays;

import org.spideruci.analysis.trace.events.props.ArrayInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.ControlFlowPropNames;
import org.spideruci.analysis.trace.events.props.DeclPropNames;
import org.spideruci.analysis.trace.events.props.EnterExecPropNames;
import org.spideruci.analysis.trace.events.props.FieldInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.InsnExecPropNames;
import org.spideruci.analysis.trace.events.props.InsnPropNames;
import org.spideruci.analysis.trace.events.props.InvokeInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.VarInsnExecPropNames;
import org.spideruci.analysis.util.MyAssert;

/**
 * TODO: rename to Event.
 * @author vpalepu
 *
 */
@SuppressWarnings("rawtypes")
public class TraceEvent implements MethodDecl, Instruction {
  
  private static final String SEP = ",";
  private static final String IPD_SEP = "|";
  
  private final int id;
  private final EventType type;
  private final String[] propValues;
  private final Enum[] propNames;
  private String ipd;
  
  public static TraceEvent copy(TraceEvent e) {
    TraceEvent copy = new TraceEvent(e.id, e.type, e.propNames);
    
    for(int i = 0; i < e.propValues.length; i += 1) {
      copy.propValues[i] = e.propValues[i];
    }
    
    return copy;
  }
  
  public static TraceEvent createControlFlowEvent(int id) {
    return new TraceEvent(id, EventType.$flow$, ControlFlowPropNames.values);
  }
  
  public static TraceEvent createInvokeInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, InvokeInsnExecPropNames.values);
  }
  
  public static TraceEvent createEnterExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, EnterExecPropNames.values);
  }
  
  public static TraceEvent createFieldInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, FieldInsnExecPropNames.values);
  }
  
  public static TraceEvent createVarInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, VarInsnExecPropNames.values);
  }
  
  /**
   * 
   * @param id
   * @return
   */
  public static TraceEvent createArrayInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, ArrayInsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for the 
   * execution of a static instruction, and its properties are specified
   * with {@link InsnExecPropNames}.
   * @param id Unique id for the event.
   * @return
   */
  public static TraceEvent createInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, InsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a static instruction 
   * in a method definition, as specified by {@link InsnPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnEvent(int id, EventType type) {
    return new TraceEvent(id, type, InsnPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a method or class
   * declaration, as specified by {@link DeclPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createDeclEvent(int id, EventType type) {
    return new TraceEvent(id, type, DeclPropNames.values);
  }
  
  public static TraceEvent valueOf(final String eventSerString) {
    // Goop code to handle the IPD. Including the IPD permanently in the Insn
    // Prop names is a TODO for now, till the control flow analysis becomes a 
    // default option for analysis.
    final String ipdString;
    final String eventString;
    if(eventSerString.contains(IPD_SEP)) {
      String[] ipdSplit = eventSerString.split("\\" + IPD_SEP);
      eventString = ipdSplit[0];
      ipdString = (ipdSplit.length >= 2) ? ipdSplit[1] : null;
    } else {
      eventString = eventSerString;
      ipdString = null;
    }
    
    String[] split = eventString.split(SEP);
    String typeString = split[0];
    int id = Integer.parseInt(split[1]);
    
    final int offset = 2;
    
    TraceEvent event;
    EventType type = EventType.valueOf(typeString);
    if(type.isExec()) {
      int eventDescPos = offset + InsnExecPropNames.INSN_EVENT_TYPE.ordinal();
      String insnType = eventString.split(",")[eventDescPos];
      switch(EventType.valueOf(insnType)) {
      case $invoke$:
        event = TraceEvent.createInvokeInsnExecEvent(id);
        break;
      case $enter$:
        event = TraceEvent.createEnterExecEvent(id);
        break;
//      case $var$:
//        event = TraceEvent.createVarInsnExecEvent(id);
//        break;
      case $field$:
        event = TraceEvent.createFieldInsnExecEvent(id);
        break;
      case $arrayload$:
      case $arraystore$:
        event = TraceEvent.createArrayInsnExecEvent(id);
        break;
      default:
        event = TraceEvent.createInsnExecEvent(id);
      }
    } else if(type.isDecl()) {
      event = TraceEvent.createDeclEvent(id, type);
    } else if(type.isInsn()) {
      event = TraceEvent.createInsnEvent(id, type);
    } else if(type.isFlow()) {
      event = TraceEvent.createControlFlowEvent(id);
    } else {
      throw new RuntimeException("Unfamiliar event: " + eventString);
    }
    
    MyAssert.assertThat(event.getPropCount() <= (split.length  - offset), 
        String.valueOf(split.length) + " event-string:" + eventString);
    
    for(int i = offset; i < split.length; i += 1) {
      event.setProp(i - offset, split[i]);
    }
    
    // pick up the ipd for an Insn event, if it exists
    if(type.isInsn() && ipdString != null) {
      event.setIpd(ipdString);
    }
    
    return event;
  }
  
  private TraceEvent(int id, EventType type, Enum[] propNames) {
    if(propNames == null || propNames.length <= 0) {
      throw new UnsupportedOperationException();
    }
    
    this.id = id;
    this.type = type;
    this.propNames = propNames;
    this.propValues = new String[propNames.length];
    this.ipd = null;
  }

  public int getId() {
    return this.id;
  }

  public EventType getType() {
    return this.type;
  }

  public String getLog() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(type == null ? "" : type).append(SEP);
    buffer.append(id).append(SEP);
    int lastIndex = propValues.length - 1;
    for(int i = 0; i < lastIndex; i += 1) {
      String value = propValues[i];
      buffer.append(value == null ? "" : value).append(SEP);
    }
    buffer.append(propValues[lastIndex]);
    
    if(this.type.isInsn() && this.ipd != null) {
      buffer.append(IPD_SEP).append(this.ipd);
    }
    
    return buffer.toString();
  }
  
  public int getPropCount() {
    return this.propValues.length;
  }

  public String getProp(int index) {
    return propValues[index];
  }

  public String getProp(final Enum propName) {
    if(!enumChecksOut(propName)) 
      return null;
    int index = propName.ordinal();
    return propValues[index];
  }

  public String getPropName(int index) {
    return propNames[index].toString();
  }

  public void setProp(int index, String propValue) {
    if(index >= propValues.length) {
      throw new RuntimeException(
          String.format("index: %d, propvalue: %s, propnames: %s, eventId: %d", 
              index, propValue, Arrays.toString(this.propNames), this.id));
    }
    propValues[index] = propValue;
  }

  public void setProp(final Enum propName, final String propValue) {
    if(enumChecksOut(propName))
      setProp(propName.ordinal(), propValue);
  }
  
    private boolean enumChecksOut(final Enum propName) {
      if(propName == null
          || propName.getDeclaringClass() != propNames[0].getDeclaringClass())
        return false;
      return true;
    }
    
  public String getDeclName() {
    return getProp(DeclPropNames.NAME);
  }
  
  public String getDeclAccess() {
    return getProp(DeclPropNames.ACCESS);
  }
  
  public String getDeclOwner() {
    return getProp(DeclPropNames.OWNER);
  }
  
  public int getInsnLine() {
    return Integer.parseInt(getProp(InsnPropNames.LINE_NUMBER));
  }
  
  public int getInsnByteIndex() {
    return Integer.parseInt(getProp(InsnPropNames.BYTECODE_INDEX));
  }
  
  public int getInsnDeclHostId() {
    return Integer.parseInt(getProp(InsnPropNames.DECL_HOST_ID));
  }
  
  public int getInsnOpcode() {
    return Integer.parseInt(getProp(InsnPropNames.OPCODE));
  }
  
  public String getInsnOperand1() {
    return getProp(InsnPropNames.OPERAND1);
  }
  
  public String getInsnOperand2() {
    return getProp(InsnPropNames.OPERAND2);
  }
  
  public String getInsnOperand3() {
    return getProp(InsnPropNames.OPERAND3);
  }
  
  public String getInsnFieldOwner() {
    return getInsnOperand1();
  }
  
  public String getInsnFieldName() {
    return getInsnOperand2();
  }
  
  public String getInsnFieldDesc() {
    return getInsnOperand3();
  }
  
  public String getInsnNewType() {
    return getInsnOperand1();
  }
  
  private static final int THREAD_ID = InsnExecPropNames.THREAD_ID.ordinal();
  private static final int TIMESTAMP = InsnExecPropNames.TIMESTAMP.ordinal();
  private static final int CALLDEPTH = InsnExecPropNames.CALLDEPTH.ordinal();
  private static final int DYN_HOST_ID = InsnExecPropNames.DYN_HOST_ID.ordinal();
  private static final int INSN_EVENT_ID = InsnExecPropNames.INSN_EVENT_ID.ordinal();
  private static final int INSN_EVENT_TYPE = InsnExecPropNames.INSN_EVENT_TYPE.ordinal();
  
  public String getExecThreadId() {
    return getProp(THREAD_ID);
  }
  
  public void setExecThreadId(String threadId) {
    setProp(THREAD_ID, threadId);
  }
  
  
  public String getExecTimestamp() {
    return getProp(TIMESTAMP);
  }
  
  public void setExecTimestamp(String timestamp) {
    setProp(TIMESTAMP, timestamp);
  }
  
  public String getExecCalldepth() {
    return getProp(CALLDEPTH);
  }
  
  public void setExecCalldepth(String calldepth) {
    setProp(CALLDEPTH, calldepth);
  }

  
  public String getExecInsnDynHost() {
    return getProp(DYN_HOST_ID);
  }
  
  public void setExecInsnDynHost(String dynHostId) {
    setProp(DYN_HOST_ID, dynHostId);
  }
  
  public String getExecInsnEventId() {
    return getProp(INSN_EVENT_ID);
  }
  
  public void setExecInsnEventId(String insnId) {
    setProp(INSN_EVENT_ID, insnId);
  }
  
  public EventType getExecInsnType() {
    return EventType.valueOf(getProp(INSN_EVENT_TYPE));
  }
  
  public void setExecInsnType(EventType type) {
    setProp(INSN_EVENT_TYPE, type.toString());
  }
  
  public String getInvokeRuntimeSign() {
    return getProp(InvokeInsnExecPropNames.RUNTIME_SIGNATURE);
  }
  
  public String getEnterRuntimeSign() {
    return getProp(EnterExecPropNames.RUNTIME_SIGNATURE);
  }
  
  @Override
  public String toString() {
    return getLog();
  }

  public void setIpd(String ipd) {
    if(ipd != null && ipd.equals("null"))
      ipd = null;
    
    this.ipd = ipd;
  }
  
  public String getIpd() {
    return this.ipd;
  }

  

}
