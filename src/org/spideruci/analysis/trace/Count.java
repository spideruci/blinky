package org.spideruci.analysis.trace;

import org.spideruci.analysis.statik.instrumentation.OfflineInstrumenter;

public class Count {
  private static int CLASS_COUNT = 0;
  private static int METHOD_COUNT = 0;
  private static int INSN_COUNT = 0;
  private static int FLOW_COUNT = 0;
  
  public static int flowCount() {
    return FLOW_COUNT;
  }
  
  public static int anotherFlow() {
    int count = ++FLOW_COUNT;
    return handleForOffline(count);
  }
  
  public static int methodCount() {
    return METHOD_COUNT;
  }
  
  public static int anotherClass() {
    int count = ++CLASS_COUNT;
    return handleForOffline(count);
  }
  
  public static int classCount() {
    return CLASS_COUNT;
  }
  
  public static int anotherMethod() {
    int count = ++METHOD_COUNT;
    return handleForOffline(count);
  }
  
  public static int getLastInsnProbeId() {
    return INSN_COUNT;
  }
  
  public static int anotherInsn() {
    int count = ++INSN_COUNT;
    return handleForOffline(count);
  }

  /**
   * @param count
   * @return
   */
  private static int handleForOffline(int count) {
    if(OfflineInstrumenter.isActive) {
      count *= -1;
    }
    return count;
  }

}
