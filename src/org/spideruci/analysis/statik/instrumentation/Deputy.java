package org.spideruci.analysis.statik.instrumentation;

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;

public class Deputy {
  public static int LINE_COUNT = 0;
  
  public static final ArrayList<String> exclusionList;
  public static final ArrayList<String> inclusionList;
  
  public static boolean checkInclusionList = false;
  
  public static final String PROFILER_NAME = "org/spideruci/analysis/dynamic/Profiler";
  
  public static final String STRING_DESC = "Ljava/lang/String;";
  public static final String OBJECT_DESC = "Ljava/lang/Object;";
  public static final String EVENT_TYPE_DESC = "Lorg/spideruci/analysis/trace/EventType;";
  public static final String INT_TYPEDESC = "I";
  public static final String FLOAT_TYPEDESC = "F";
  public static final String CHAR_TYPEDESC = "C";
  public static final String BOOLEAN_TYPEDESC = "Z";
  public static final String BYTE_TYPEDESC = "B";
  public static final String DOUBLE_TYPEDESC = "D";
  public static final String LONG_TYPEDESC = "J";
  
  public static final String LDC_16 = "$ldc_16$";
  public static final String LDC_8 = "$ldc_8$";
  
  static {
    exclusionList = new ArrayList<String>();
    exclusionList.add("org/gjt/sp/util/Log$");
    exclusionList.add("org/gjt/sp/util/Log$LogPrintStream");
    exclusionList.add("org/gjt/sp/util/Log$LogInputStream");
    exclusionList.add("org/gjt/sp/util/Log$LogOutputStream");
    exclusionList.add("java/");
    exclusionList.add("org/objectweb/asm");
    exclusionList.add("test/");
    exclusionList.add("Core/");
    exclusionList.add("Data/");
    exclusionList.add("dacapo");
    exclusionList.add("Harness");
    exclusionList.add("com/google");
    exclusionList.add("sun");
    exclusionList.add("apple/");
    exclusionList.add("com/sun");
    exclusionList.add("javax/");
    exclusionList.add("org/ietf");
    exclusionList.add("org/jcp");
    exclusionList.add("org/omg");
    exclusionList.add("org/w3c");
    exclusionList.add("org/xml");
    exclusionList.add("org/objenesis");
    exclusionList.add("org/eclipse");
    exclusionList.add("org/mockito");
    exclusionList.add("org/hamcrest");
    exclusionList.add("sunw");
    exclusionList.add("com/thoughtworks/xstream");
    exclusionList.add("junit");
    exclusionList.add("org/junit");
    exclusionList.add("org/fest");
    exclusionList.add("junit/tests/framework/");
    exclusionList.add("junit/tests/runner/TextFeedbackTest");
    exclusionList.add("junit/tests/runner/TextRunnerTest");
    exclusionList.add("junit/tests/extensions");
    exclusionList.add("org/apache/commons/logging");
    exclusionList.add("org/apache/commons/logging");
    exclusionList.add("edu/uci/spiderlab/analysis");
    exclusionList.add("org/spideruci/analysis");
    
    inclusionList = new ArrayList<String>();
    inclusionList.add("org/gjt/sp/");
    inclusionList.add("DumpXML");
    inclusionList.add("org/spideruci/patternabstraction/benckmark");
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
  
  public static String primitiveCode2String(int primitiveCode) {
    switch(primitiveCode) {
    case Opcodes.T_BOOLEAN:
      return "Z";
    case Opcodes.T_BYTE:
      return "B";
    case Opcodes.T_CHAR:
      return "C";
    case Opcodes.T_DOUBLE:
      return "D";
    case Opcodes.T_FLOAT:
      return "F";
    case Opcodes.T_INT:
      return "I";
    case Opcodes.T_LONG:
      return "J";
    case Opcodes.T_SHORT:
      return "S";
    default:
      throw new RuntimeException("Unexpected primitive code: " + primitiveCode);
    }
  }
  
}
