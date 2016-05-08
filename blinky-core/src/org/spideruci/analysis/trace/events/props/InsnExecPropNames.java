package org.spideruci.analysis.trace.events.props;

public enum InsnExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  CALLDEPTH,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE;
  
  public static final InsnExecPropNames[] values = InsnExecPropNames.values();
}