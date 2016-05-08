package org.spideruci.analysis.trace.events.props;

public enum VarInsnExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  CALLDEPTH,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE,
  VAR_ID;
  
  public static final VarInsnExecPropNames[] values = VarInsnExecPropNames.values();
}
