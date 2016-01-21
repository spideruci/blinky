package org.spideruci.analysis.trace.eventprops;

public enum InvokeInsnExecPropNames {
  THREAD_ID,
  TIMESTAMP,
  DYN_HOST_ID,
  INSN_EVENT_ID,
  INSN_EVENT_TYPE,
  RUNTIME_SIGNATURE;
  
  public static final InvokeInsnExecPropNames[] values = InvokeInsnExecPropNames.values();
}
