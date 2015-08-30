package org.spideruci.analysis.trace;

public enum InsnPropNames {
    DECL_HOST_ID, // method (or class)
    LINE_NUMBER,
    OPCODE, // JVM byte-code operation code.
    OPERAND1,
    OPERAND2;
  
  public static final InsnPropNames[] values = InsnPropNames.values();
}
