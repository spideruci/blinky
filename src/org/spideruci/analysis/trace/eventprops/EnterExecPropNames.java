package org.spideruci.analysis.trace.eventprops;

public enum EnterExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE,
  RUNTIME_SIGNATURE;
  
  public static final EnterExecPropNames[] values = EnterExecPropNames.values();
}