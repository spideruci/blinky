package org.spideruci.analysis.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.spideruci.analysis.config.definer.ConfigFieldsDefiner;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;


@Mojo( name = "configure", defaultPhase = LifecyclePhase.COMPILE )
public class ConfigureMojo extends AbstractMojo {
  
  public final static String JAVA_PKG_SEP = "/";

  @Parameter( property = "configure.clientName", defaultValue = "your awesome project" )
  private String clientName;
  
  @Parameter( property = "configure.configFile", defaultValue = "na" )
  private String configFile;

  @Parameter( property = "configure.configClassName")
  private String configClassName;
  
  @Parameter( property = "configure.targetClasses" )
  private String targetClasses;
  
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final String yo = 
        String.format("Setting up Compile-time configurations for %s!", 
            clientName.toUpperCase());
    getLog().info(yo);
    getLog().info(String.format("Using config file: %s", configFile));
    
    File file = new File(configFile);
    
    if(!file.exists() || !file.isFile() || file.isDirectory()) {
      getLog().info(String.format("No valid file available at %s", file.toString()));
      return;
    }
    
    getLog().info(String.format("Valid file available at %s", file.toString()));
    
    Map<String, ?> config = readConfigFile(file);
    readTarget(configClassName.replaceAll("\\.", JAVA_PKG_SEP), config);
  }
  
  private boolean isClassNameValid(String className) {
    return className != null && className.length() != 0; 
  }
  
  private void readTarget(String className, Map<String, ?> config) {
    if(!isClassNameValid(className)) {
      getLog().error(String.format("Class name (%s) is invalid.", className));
      return;
    }

    Path path = Paths.get(targetClasses);
    
    if(Files.exists(path)) {
      getLog().info(path.toString());
    } else {
      getLog().info("Unable to find the classes directory :(");
      return;
    }
    
    String[] classNameSplit = className.split(JAVA_PKG_SEP);
    for(int i = 0; i <= classNameSplit.length - 2; i += 1) {
      final String splitItem = classNameSplit[i];
      Path temp = path.resolve(splitItem);
      if(!Files.exists(temp)) {
        final String errMsg = String.format("Path is invalid: %s", temp.toString()); 
        getLog().error(errMsg);
        throw new RuntimeException(errMsg);
      }
      
      path = temp;
    }
    
    Path configClassPath = path.resolve(classNameSplit[classNameSplit.length - 1] + ".class");
    final String confClsPathName = configClassPath.toString();
    if(Files.exists(configClassPath)) {
      getLog().info(String.format("Updating config class: %s", confClsPathName));
    } else {
      final String errMsg = String.format("Path invalid: %s", confClsPathName);
      getLog().error(errMsg);
      throw new RuntimeException(errMsg);
    }
    
    byte[] modBytecode = 
        ConfigFieldsDefiner.rewrite(
            className, 
            configClassPath.toFile(), 
            config);
    
//    writeToClassFile(modBytecode, confClsPathName);
  }
  
  public static void writeToClassFile(byte[] bytecode, String filePath) {
    try {
      PrintStream byteStream = new PrintStream(filePath);
      byteStream.write(bytecode);
      byteStream.close();
    } catch(IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private static Map<String, ?> readConfigFile(File file) {
    try {
      Reader reader = new FileReader(file);
      YamlConfig readerConfig = new YamlConfig();
      readerConfig.setClassTag("tag:yaml.org,2002:bool", Boolean.class);
      
      YamlReader configReader = new YamlReader(reader, readerConfig);
      Object config = configReader.read();
      
      @SuppressWarnings("unchecked")
      Map<String, ?> map = (Map<String, ?>) config;
      
      System.out.println(map.toString());
      
      for(String key : map.keySet()) {
        Object value = map.get(key);
        System.out.println(key + " " + ((value == null) ? "null" : value.getClass()));
        if(value instanceof Collection) {
          Collection collection = (Collection) value;
          for(Object item : collection) {
            System.out.println("\t" + item.getClass());
            break;
          }
          
        }
      }

      configReader.close();
      
      return map;
      
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
