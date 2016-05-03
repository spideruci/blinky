package org.spideruci.analysis.trace.events.props;

/**
 * Property Names for Control Flows between instructions.
 * @author vpalepu
 *
 */
public enum ControlFlowPropNames {
  DECL_HOST_ID, // method (or class)
  SRC_BYTECODE,
  SRC_OPCODE,
  DST_BYTECODE,
  DST_OPCODE,
  EXCEPTIONAL;
  
  public static final ControlFlowPropNames[] values = ControlFlowPropNames.values();
}
