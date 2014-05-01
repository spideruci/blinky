package org.spideruci.analysis.statik.instrumentation;

import java.util.ArrayList;

public class Deputy {
  public static ArrayList<String> exclusionList;
  
  static {
    exclusionList = new ArrayList<String>();
    exclusionList.add("java/");
    exclusionList.add("org/objectweb/asm");
    exclusionList.add("test/");
    exclusionList.add("Core/");
    exclusionList.add("Data/");
    exclusionList.add("dacapo");
    exclusionList.add("Harness");
    exclusionList.add("sun");
    exclusionList.add("com/sun");
    exclusionList.add("javax/");
    exclusionList.add("org/ietf");
    exclusionList.add("org/jcp");
    exclusionList.add("org/omg");
    exclusionList.add("org/w3c");
    exclusionList.add("org/xml");
    exclusionList.add("sunw");
    exclusionList.add("com/thoughtworks/xstream");
    exclusionList.add("junit");
    exclusionList.add("org/junit");
    exclusionList.add("junit/tests/framework/");
    exclusionList.add("junit/tests/runner/TextFeedbackTest");
    exclusionList.add("junit/tests/runner/TextRunnerTest");
    exclusionList.add("junit/tests/extensions");
    exclusionList.add("org/apache/commons/logging");
    exclusionList.add("org/apache/commons/logging");
    exclusionList.add("edu/uci/spiderlab/analysis");
    exclusionList.add("org/spideruci/analysis");
  }
}
