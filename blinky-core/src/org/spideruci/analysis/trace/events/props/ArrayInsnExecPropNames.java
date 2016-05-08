package org.spideruci.analysis.trace.events.props;

public enum ArrayInsnExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  CALLDEPTH,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE,
  ARRAYREF_ID,
  ELEMENT_INDEX,
  ARRAY_ELEMENT,
  ARRAY_LENGTH;
  
  public static final ArrayInsnExecPropNames[] values = ArrayInsnExecPropNames.values();
}
