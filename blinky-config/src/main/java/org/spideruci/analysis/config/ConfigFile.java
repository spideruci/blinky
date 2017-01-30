package org.spideruci.analysis.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

public class ConfigFile {
  
  private final File file;
  
  public static ConfigFile create(String fileName) {
    return create(fileName, new SystemStreamLog());
  }
  
  public static ConfigFile create(String fileName, final Log log) {
    File configfile = getFile(fileName, log);
    ConfigFile configFile = new ConfigFile(configfile);
    return configFile;
  }
  
  private static File getFile(String fileName, Log log) {

    log.debug(String.format("Using config file: %s", fileName));
    File file = new File(fileName);
    
    final boolean configNotExists = !file.exists();
    final boolean configNotFile = !file.isFile();
    final boolean configIsDirectory = file.isDirectory();
    
    if(configNotExists || configNotFile || configIsDirectory) {
      log.debug(String.format("Config doesn't exist? %B", configNotExists));
      log.debug(String.format("Config not a file? %B", configNotFile));
      log.debug(String.format("Config is a directory? %B", configIsDirectory));
      
      final String invalidFileMessage =
          String.format("No valid config file at %s", file.toString());
      log.error(invalidFileMessage);
      
      throw new RuntimeException(invalidFileMessage);
    }
    
    log.info(String.format("Valid config file available at %s", file.toString()));
    return file;
  }
  
  private ConfigFile(final File file) {
    this.file = file;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public Map<String, ?> readConfig() {
    
    try {
      Reader reader = new FileReader(this.file);
      YamlConfig readerConfig = new YamlConfig();
      readerConfig.setClassTag("tag:yaml.org,2002:bool", Boolean.class);
      
      YamlReader configReader = new YamlReader(reader, readerConfig);
      Object config = configReader.read();
      
      @SuppressWarnings("unchecked")
      Map<String, ?> map = (Map<String, ?>) config;
      
      configReader.close();
      
      return map;
      
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void logConfig(Map<String, ?> map, Log log) {
    for(String key : map.keySet()) {
      Object value = map.get(key);
      System.out.println(key + " " + ((value == null) ? "null" : value.getClass()));
      if(value instanceof Collection) {
        
        @SuppressWarnings("rawtypes")
        Collection collection = (Collection) value;
        for(Object item : collection) {
          log.debug("\t" + item.getClass());
          break;
        }
        
      }
    }
  }

}
