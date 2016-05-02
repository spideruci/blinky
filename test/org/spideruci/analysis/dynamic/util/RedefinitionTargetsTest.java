package org.spideruci.analysis.dynamic.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spideruci.analysis.dynamic.RuntimeClassRedefiner.RedefinitionTargets;

public class RedefinitionTargetsTest {

  @Test
  public void test() {
    String className = "java/security/AccessControl";
    
    boolean shouldRedefine = RedefinitionTargets.isTarget(className)
    && !RedefinitionTargets.isException(className);
    
    assertFalse(shouldRedefine);
  }

}
