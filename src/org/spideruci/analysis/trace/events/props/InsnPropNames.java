package org.spideruci.analysis.trace.events.props;

public enum InsnPropNames {
    DECL_HOST_ID, // method (or class)
    LINE_NUMBER,
    BYTECODE_INDEX,
    OPCODE, // JVM byte-code operation code.
    OPERAND1,
    OPERAND2,
    OPERAND3;
  
  public static final InsnPropNames[] values = InsnPropNames.values();
}
