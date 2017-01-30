package org.spideruci.analysis.statik.instrumentation;

public class Config {
  public static int LINE_COUNT = 0;
  
  public static final String[] exclusionList = null;
  public static final String[] inclusionList = null;
  
  public static boolean checkInclusionList = false;
  
  public static final String STATIC_IDENT = "C";
  
  public static final String NA = "NA";
  
  public static final String PROFILER_NAME = "org/spideruci/analysis/dynamic/Profiler";
  public static final String RUNTIME_TYPE_PROFILER_NAME = "org/spideruci/analysis/dynamic/RuntimeTypeProfiler";
  
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
  
//  public static final String NULL = null;
//  public static String UNDEFINED;
//  public static String[] UNDEFINED_ARRAY;
  
//  static {
//    exclusionList = new String[] {
//    
//        "java/",
//        "java/lang",
//        "org/objectweb/asm",
//        "edu/uci/spiderlab/analysis",
//        "org/spideruci/analysis",
//        "test/",
//        "Core/",
//        "Data/",
//        "dacapo",
//        "Harness",
//        "com/google",
//        "sun",
//        "apple/",
//        "com/apple",
//        "com/sun",
//        "javax/",
//        "org/ietf",
//        "org/jcp",
//        "org/omg",
//        "org/w3c",
//        "org/xml",
//        "org/objenesis",
//        "org/eclipse",
//        "org/mockito",
//        "org/hamcrest",
//        "sunw",
//        "com/thoughtworks/xstream",
//        "junit",
//        "org/junit",
//        "org/fest",
//        "junit/tests/framework/",
//        "junit/tests/runner/TextFeedbackTest",
//        "junit/tests/runner/TextRunnerTest",
//        "junit/tests/extensions",
//        "org/apache/commons/logging",
//        "org/apache/commons/logging",
//        "org/codehaus/plexus",
//        "org/spideruci/tacoco",
//        "org/apache/maven",
//
//        "org/gjt/sp/util/Log$",
//        "org/gjt/sp/util/Log$LogPrintStream",
//        "org/gjt/sp/util/Log$LogInputStream",
//        "org/gjt/sp/util/Log$LogOutputStream"
//    };
//    
//    inclusionList = new String[] {
//        "java/io",
//        "java/awt",
//        "java/beans",
//        "java/io",
//        "java/math",
//        "java/net",
//        "java/nio",
//        "java/rmi",
//        "java/sql",
//        "java/text",
//        "javax",
//        "org/ietf",
//        "org/jcp",
//        "org/omg",
//        "org/w3c",
//        "org/xml",
//        "org/gjt/sp/",
//        "DumpXML",
//        "org/spideruci/patternabstraction/benckmark"
//    };
//  }

}
