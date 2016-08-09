package org.spideruci.analysis.statik;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class AnalysisConfig {
  
  
  public final static String JRE7_LIB = "jre7lib";
  public final static String ARG_CLASS = "argclass";
  public final static String ENTRY_CLASS = "entryclass";
  public final static String ENTRY_METHOD = "entrymethod";
  
  // Add new configuration file keys above here.
  
  private final File configFile;
  private final TreeMap<String, String> configs;
  private List<String> argsList;
  
  
  public static AnalysisConfig init(final String configPath) {
    Path path = Paths.get(configPath);
    AnalysisConfig userConfig = new AnalysisConfig(path.toFile());
    userConfig.parse();
    return userConfig;
  }
  
  private AnalysisConfig(File configFile) {
    this.configFile = configFile;
    this.configs = new TreeMap<>();
  }
  
  public void setArgs(List<String> argsList) {
    this.argsList = argsList;
  }
  
  public String[] getArgs() {
    return this.argsList.toArray(new String[0]);
  }
  
  public String get(final String key) {
    return configs.get(key);
  }
  
  private void parse() {
    try {
      Scanner scanner = new Scanner(configFile);
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if(line == null 
            || line.isEmpty() 
            || isLineComment(line) 
            || !doesLineContainProperty(line)) {
          continue;
        }

        String[] split = line.split("=");
        if(split[0] == null || split[0].isEmpty()) {
          continue;
        }

        boolean valueInvalid = 
            split.length < 2 || split[1] == null || split[1].isEmpty();

        final String key = split[0];
        final String value = valueInvalid ? null : split[1];
        this.configs.put(key, value);
      }
      
      scanner.close();
    } catch(FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  private boolean isLineComment(final String line) {
    return line.startsWith("#") && line.startsWith("//");
  }
  
  private boolean doesLineContainProperty(final String line) {
    return line.contains("=");
  }

}
