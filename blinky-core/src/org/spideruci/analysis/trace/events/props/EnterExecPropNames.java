package org.spideruci.analysis.trace.events.props;

public enum EnterExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  CALLDEPTH,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE,
  RUNTIME_SIGNATURE;
  
  public static final EnterExecPropNames[] values = EnterExecPropNames.values();
}