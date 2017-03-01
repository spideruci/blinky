package org.spideruci.analysis.config;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestConfigFile {
  
  public static File configYaml;
  
  private Map<String, ?> config;
  
  @BeforeClass
  public static void setup() {
    ClassLoader classLoader = TestConfigFile.class.getClassLoader();
    URL classfileURL = classLoader.getResource("config.yaml");
    configYaml = new File(classfileURL.getFile()); 
  }
  
  @Before
  public void beforeTest() {
    ConfigFile configFile = ConfigFile.create(configYaml.toString());
    config = configFile.readConfig();
  }

  @Test
  public void testForIntegerConfig() {
    // given
    int expectedValue = 1;
    String field = "number";
    
    // when
    
    int actualValue = (int) ((Integer) config.get(field));
    
    // then
    assertEquals(expectedValue, actualValue); 
  }
  
  @Test
  public void testForFloatConfig() {
    // given
    float expectedValue = 1.0f;
    String field = "float";
    
    // when
    float actualValue = (float) ((Float) config.get(field));
    
    // then
    assertEquals(expectedValue, actualValue, 0.0f);
  }
  
  @Test
  public void testForFloatConfig2() {
    // given
    float expectedValue = 0.0f;
    String field = "LINE_COUNT";
    
    // when
    float actualValue = (float) ((Float) config.get(field));
    
    // then
    assertEquals(expectedValue, actualValue, 0.0f);
  }
  
  @Test
  public void testForBooleanConfig() {
    // given
    boolean expectedValue = true;
    String field = "boolen";
    
    // when
    boolean actualValue = (boolean) ((Boolean) config.get(field));
    
    // then
    assertEquals(expectedValue, actualValue);
  }
  
  @Test
  public void testForStringConfig() {
    // given
    String expectedValue = "vijay";
    String field = "name";
    
    // when
    String actualValue = (String) config.get(field);
    
    // then
    assertEquals(expectedValue, actualValue);
  }
  
  @Test
  public void testForArrayListConfig() {
    // given
    ArrayList<String> expectedValue = new ArrayList<>();
    expectedValue.add("1");
    String field = "array";
    
    // when
    @SuppressWarnings("unchecked")
    ArrayList<String> actualValue = (ArrayList<String>) config.get(field);
    
    // then
    assertEquals(expectedValue, actualValue);
    
    // and
    assertEquals(expectedValue.size(), actualValue.size());
    
    // and
    assertEquals(expectedValue.get(0), actualValue.get(0));
  }

  @Test
  public void testForArrayListConfig2() {
    // given
    int expectedArrayListSize = 43;
    String field = "exclusionList";
    
    // when
    @SuppressWarnings("unchecked")
    ArrayList<String> actualValue = (ArrayList<String>) config.get(field);
    
    // then
    assertEquals(expectedArrayListSize, actualValue.size());
  }
  
  @Test
  public void testForArrayListConfig3() {
    // given
    int expectedArrayListSize = 19;
    String field = "inclusionList";
    
    // when
    @SuppressWarnings("unchecked")
    ArrayList<String> actualValue = (ArrayList<String>) config.get(field);
    
    // then
    assertEquals(expectedArrayListSize, actualValue.size());
  }
}
