package org.spideruci.analysis.statik.instrumentation;

import java.util.ArrayList;

public class Deputy {
  public static final ArrayList<String> exclusionList;
  public static final String PROFILER_NAME = "org/spideruci/analysis/dynamic/Profiler";
  public static final String PROFILER_METHODENTER = "printLnMethodEnterLog";
  public static final String PROFILER_METHODEXIT = "printLnMethodExitLog";
  public static final String PROFILER_LINENUMER = "printLnLineNumber";
  public static final String PROFILER_GETHASH = "getHash";
  
  public static final String STRING_DESC = "Ljava/lang/String;";
  public static final String OBJECT_DESC = "Ljava/lang/Object;";
  public static final String INT_TYPEDESC = "I";
  public static final String FLOAT_TYPEDESC = "F";
  public static final String CHAR_TYPEDESC = "C";
  public static final String BOOLEAN_TYPEDESC = "Z";
  public static final String BYTE_TYPEDESC = "B";
  public static final String DOUBLE_TYPEDESC = "D";
  public static final String LONG_TYPEDESC = "J";
    
  public static final String PROFILER_GETHASH_DESC = "(" + OBJECT_DESC + ")" + STRING_DESC;
  
  public static final String ENTER = "$enter$";
  public static final String ATHORW = "$athrow$";
  public static final String RETURN = "$return$";
  public static final String LINE = "$sourcelinenumber$";
  
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
  
  public static String joinStrings(String[] strings, String sep) {
    int length = strings.length;
    if(length == 0) return "";
    
    StringBuffer buffer = new StringBuffer();
    buffer.append(strings[0]);
    for(int i = 1; i<length; i++) {
      buffer.append(sep);
      buffer.append(strings[i]);     
    }
    return buffer.toString();
  }
  
  public static String desc2type(String desc) {
    if(desc.charAt(0) != 'L') return desc;
    String type = desc.substring(1, desc.length() - 1);
    return type;
  }
  
}
