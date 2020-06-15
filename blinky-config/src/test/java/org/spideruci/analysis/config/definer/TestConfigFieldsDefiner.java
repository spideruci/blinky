package org.spideruci.analysis.config.definer;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;

public class TestConfigFieldsDefiner {

  private final static String className = "org/spideruci/analysis/statik/instrumentation/Deputy";
  private static File classFile;
  
  @BeforeClass
  public static void beforeTest() {
    ClassLoader classLoader = TestConfigFieldsDefiner.class.getClassLoader();
    final String classFileName = "Deputy.class";
    URL classfileURL = classLoader.getResource(classFileName);
    classFile = new File(classfileURL.getFile());
  }
  
  @Test
  public void smokeTestRedefinition() {
    Map<String, String> config = new HashMap<>();
    
    final String field = "UNDEFINED";
    final String value = "blah";
    config.put(field, value);
    
    ConfigFieldsDefiner.define(className, classFile, config);
  }
  
  @Test
  public void testConfigValuesForArray() {
    // given
    ConfigClassScanner configClassScanner = runClassScanner(className, classFile, null);
    
    // when
    Object value = configClassScanner.getPublicStaticFieldValue("exclusionList");
    
    // then
    assertNull(value);
  }

  @Test
  public void testConfigFieldsDefiner() {
    final String field = "UNDEFINED";
    final String value = "blah";
    
    Map<String, String> config = new HashMap<>();
    config.put(field, value);
    
    byte[] bytecode = ConfigFieldsDefiner.define(className, classFile, config);
    
    ConfigClassScanner configClassScanner = runClassScanner(className, null, bytecode);
    
    Object definedValue = configClassScanner.getPublicStaticFieldValue(field);
    assertEquals(value, definedValue);
  }
  
  
  @Test
  public void testConfigClassScanner() {
    // given
    ConfigClassScanner configClassScanner = 
        new ConfigClassScanner(ClassWriter.COMPUTE_MAXS, className);
    
    //and
    ClassAdapterRunner configScanRunner = 
        ClassAdapterRunner.create(configClassScanner, classFile);
    
    //and
    String[][] publicStaticFieldNames = {
        { "exclusionList", "def" },
        { "inclusionList", "def" },
        { "checkInclusionList", "def" },
        { "STATIC_IDENT", "def" },
        { "NA", "def" },
        { "PROFILER_NAME", "def" },
        { "RUNTIME_TYPE_PROFILER_NAME", "def" },
        { "STRING_DESC", "def" },
        { "OBJECT_DESC", "def" },
        { "EVENT_TYPE_DESC", "def" },
        { "INT_TYPEDESC", "def" },
        { "FLOAT_TYPEDESC", "def" },
        { "CHAR_TYPEDESC", "def" },
        { "BOOLEAN_TYPEDESC", "def" },
        { "BYTE_TYPEDESC", "def" },
        { "DOUBLE_TYPEDESC", "def" },
        { "LONG_TYPEDESC", "def" },
        { "LDC_16", "def" },
        { "LDC_8", "def" },
        { "NULL", "def" },
        { "UNDEFINED", "undef" },
        { "UNDEFINED_ARRAY", "undef" },
    };
    
    // when
    configScanRunner.run();
    
    // then
    assertTrue(configClassScanner.containsClinit());
    
    // and
    System.out.println("FIELD_NAME, Presence Check, (un)Defined Check");
    for(String[] publicStaticField : publicStaticFieldNames) {
      final String publicStaticFieldName = publicStaticField[0];
      
      System.out.print(publicStaticFieldName);
      
      String errMsg = 
          String.format(
              "Expected class to have a public static field w/ the name: %s.", 
              publicStaticFieldName);
      
      assertTrue(errMsg,
          configClassScanner.containsPublicStaticField(publicStaticFieldName));
      
      System.out.print(" Present ");
      
      final boolean isFieldExpectedToBeDefined = !"undef".equals(publicStaticField[1]);
      
      boolean isFieldActuallyDefined = 
          configClassScanner.publicStaticFieldIsDefined(publicStaticFieldName);
      
      assertEquals(isFieldExpectedToBeDefined, isFieldActuallyDefined);
      
      if(isFieldExpectedToBeDefined ^ isFieldActuallyDefined) { // different
        System.out.print(isFieldExpectedToBeDefined ? "expecting defined; " : "expecting not defined; ");
        System.out.println(isFieldActuallyDefined ? "actually defined" : "actually not defined");
      } else {
        System.out.println();
      }
    }
  }
  
  public static ConfigClassScanner runClassScanner(
      String className, File classFile, byte[] bytecode) {
    ConfigClassScanner configClassScanner = 
        new ConfigClassScanner(ClassWriter.COMPUTE_MAXS, className);
    
    ClassAdapterRunner configScanRunner = null;
    
    if(classFile != null) {
      configScanRunner = 
          ClassAdapterRunner.create(configClassScanner, classFile);
    } else if(bytecode != null) {
      configScanRunner = 
          ClassAdapterRunner.create(configClassScanner, bytecode);
    } else {
      throw new RuntimeException("No class file or bytecode array!");
    }
    
    configScanRunner.run();
    return configClassScanner;
  }

}
