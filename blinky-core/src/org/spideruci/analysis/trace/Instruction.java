package org.spideruci.analysis.trace;

public interface Instruction {
  
  public int getInsnLine();
  public int getInsnByteIndex();
  
  /**
   * TODO RENAME TO getInsnMethodDeclId
   * @return
   */
  public int getInsnDeclHostId();
  public int getInsnOpcode();
  public String getInsnOperand1();
  public String getInsnOperand2();
  public String getInsnOperand3();

}
