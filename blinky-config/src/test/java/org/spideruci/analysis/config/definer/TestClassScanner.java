package org.spideruci.analysis.config.definer;

import static org.junit.Assert.*;
import static org.spideruci.analysis.config.Resources.CLASSNAME;
import static org.spideruci.analysis.config.Resources.CLASSFILE;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

@RunWith(Parameterized.class)
public class TestClassScanner {
  
  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "exclusionList", "[Ljava/lang/String;", null, true },
      { "inclusionList", "[Ljava/lang/String;", null, true },
      { "checkInclusionList", "Z", null, true }, // bug
      { "STATIC_IDENT", "Ljava/lang/String;", "C", true },
      { "NA", "Ljava/lang/String;", "NA", true },
      { "PROFILER_NAME", "Ljava/lang/String;", "org/spideruci/analysis/dynamic/Profiler", true },
      { "RUNTIME_TYPE_PROFILER_NAME", "Ljava/lang/String;", "org/spideruci/analysis/dynamic/RuntimeTypeProfiler", true },
      { "STRING_DESC", "Ljava/lang/String;", "Ljava/lang/String;", true },
      { "OBJECT_DESC", "Ljava/lang/String;", "Ljava/lang/Object;", true },
      { "EVENT_TYPE_DESC", "Ljava/lang/String;", "Lorg/spideruci/analysis/trace/EventType;", true },
      { "INT_TYPEDESC", "Ljava/lang/String;", "I", true },
      { "FLOAT_TYPEDESC", "Ljava/lang/String;", "F", true },
      { "CHAR_TYPEDESC", "Ljava/lang/String;", "C", true },
      { "BOOLEAN_TYPEDESC", "Ljava/lang/String;", "Z", true },
      { "BYTE_TYPEDESC", "Ljava/lang/String;", "B", true },
      { "DOUBLE_TYPEDESC", "Ljava/lang/String;", "D", true },
      { "LONG_TYPEDESC", "Ljava/lang/String;", "J", true },
      { "LDC_16", "Ljava/lang/String;", "$ldc_16$", true },
      { "LDC_8", "Ljava/lang/String;", "$ldc_8$", true },
      { "NULL", "Ljava/lang/String;", null, true },
      { "UNDEFINED", "Ljava/lang/String;", null, false },
      { "UNDEFINED_ARRAY", "[Ljava/lang/String;", null, false },
    });
  }
  
  private String field;
  private String fieldDesc;
  private Object value;
  private boolean defined;
  
  public TestClassScanner(final String f, final String fd, final Object v, final boolean d) {
    field = f;
    fieldDesc = fd;
    value = v;
    defined = d;
  }
  
  @Test
  public void fieldDeclarationPresenceCheck() {
    // given
    final String fieldName = this.field;
    
    // when
    ConfigClassScanner configClassScanner = runClassScanner(CLASSNAME, CLASSFILE, null);

    // then
    assertTrue(configClassScanner.containsPublicStaticField(fieldName));
  }
  
  @Test
  public void fieldTypeCheck() {
    // given
    Type expectedFieldType = Type.getType(this.fieldDesc);
    
    // when
    ConfigClassScanner configClassScanner = runClassScanner(CLASSNAME, CLASSFILE, null);
    // and
    Type fieldType = configClassScanner.getPublicStaticField(field);
    
    // then
    assertEquals(expectedFieldType, fieldType);
  }
  
  @Test
  public void fieldValueCheck() {
    // given
    Object expectedFieldValue = this.value;
    String fieldName = this.field;
    
    // when
    ConfigClassScanner configClassScanner = runClassScanner(CLASSNAME, CLASSFILE, null);
    // and
    Object fieldValue = configClassScanner.getPublicStaticFieldValue(fieldName);
    
    // then
    assertEquals(expectedFieldValue, fieldValue);
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
