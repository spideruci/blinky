package org.spideruci.analysis.statik;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import org.spideruci.analysis.statik.blocks.AnalysisBlock;

public class AnalysisConfig {
  
  public final static String JRE7_LIB = "jre7lib";
  public final static String ARG_CLASS = "argclass";
  public final static String ENTRY_CLASS = "entryclass";
  public final static String ENTRY_METHOD = "entrymethod";
  public final static String ENTRY_METHODS_DB = "entrymethodsdb";
  public final static String DEBUG = "debug";
  public final static String CALL_GRAPH_ALGO = "callgraph-algo";
  public final static String CLASSPATH = "classpath";
  private final static String SUT_JARPATH = "sutjarpath";
  
  private final static String EXEC_MARKER = "$";
  
  // Add new configuration file keys above here.
  
  private final File configFile;
  private final TreeMap<String, String> configs;
  private List<String> argsList;
  private final List<Class<? extends AnalysisBlock>> executableBlocks;
  
  
  public static AnalysisConfig init(final String configPath) {
    Path path = Paths.get(configPath);
    AnalysisConfig userConfig = new AnalysisConfig(path.toFile());
    userConfig.parse();
    return userConfig;
  }
  
  private AnalysisConfig(File configFile) {
    this.configFile = configFile;
    this.configs = new TreeMap<>();
    this.executableBlocks = new ArrayList<>();
  }
  
  public void setArgs(List<String> argsList) {
    this.argsList = argsList;
  }
  
  public String[] getArgs() {
    return this.argsList.toArray(new String[0]);
  }
  
  public boolean contains(final String key) {
    return configs.containsKey(key);
  }
  
  public String get(final String key) {
    return configs.get(key);
  }
  
  private void parse() {
    String sutjarpath = null;
    
    try {
      Scanner scanner = new Scanner(configFile);
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        
        if(line == null)
          continue;
        
        line = line.trim();
        
        if(line.isEmpty() 
            || isLineComment(line) 
            || lineIsExecutable(line)
            || !doesLineContainProperty(line)) {
          continue;
        }

        String[] split = line.split("=");
        if(split[0] == null || split[0].isEmpty()) {
          continue;
        }

        boolean valueInvalid = 
            split.length < 2 || split[1] == null || split[1].isEmpty();

        
        if(split[0].equals(SUT_JARPATH)){
          sutjarpath = split[1].replaceAll(",", ":");
          continue;
        }
        
        if(split[0].equals(CLASSPATH)){
          split[1] = split[1].replaceAll(",", ":");
          
          if(sutjarpath != null)
            split[1] = split[1] + ":" + sutjarpath;
        }
        
        final String key = split[0];
        final String value = valueInvalid ? null : split[1];
        this.configs.put(key, value);
      }
      
      scanner.close();
    } catch(FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  private boolean lineIsExecutable(String line) {
    if(line.startsWith(EXEC_MARKER)) {
      final String trimmedLine = line.substring(EXEC_MARKER.length()).trim();
      try {
        Class<?> klass = Class.forName(trimmedLine);
        
        if(klass != null && AnalysisBlock.class.isAssignableFrom(klass)) {
          this.executableBlocks.add((Class<? extends AnalysisBlock>)klass);
          DebugUtil.printfln("Registering %s as an executable analysis block.", trimmedLine);
          return true;
        }
        
        DebugUtil.printfln("%s is not an executable analysis block.", trimmedLine);
      } catch (ClassNotFoundException e) {
        DebugUtil.printfln("Unable to find class by the name: %s.", trimmedLine);
        e.printStackTrace();
      }
    }
    
    // Given that we cannot find an executable class on this line,
    // this line is not executable.
    return false;
  }

  private boolean isLineComment(final String line) {
    return line.startsWith("#") || line.startsWith("//");
  }
  
  private boolean doesLineContainProperty(final String line) {
    return line.contains("=");
  }

  public List<Class<? extends AnalysisBlock>> getBlocks() {
    return this.executableBlocks  ;
  }

}
