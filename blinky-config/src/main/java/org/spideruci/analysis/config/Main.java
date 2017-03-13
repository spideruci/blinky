package org.spideruci.analysis.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.spideruci.analysis.config.definer.ConfigFieldsDefiner;

public class Main {

  public final static String JAVA_PKG_SEP = "/";

  private static final String CONFIG_FILEPATH = "config.filepath";
  private static final String CONFIG_CLASSNAME = "config.classname";
  private static final String CONFIG_COMPILEDOUTPUT = "config.compiledoutput";

  public static final Log log = new SystemStreamLog();
  public static Log getLog() {
    return log;
  }

  public static void main(String[] args) {
    final String introMessage = 
        String.format("Setting up Compile-time configurations for BLINKY CORE!");
    log.info(introMessage);

    final String configFilePath = System.getProperty(CONFIG_FILEPATH);

    ConfigFile conf = ConfigFile.create(configFilePath);
    Map<String, ?> config = conf.readConfig();

    final String configClassName = System.getProperty(CONFIG_CLASSNAME);
    final String className = configClassName.replaceAll("\\.", JAVA_PKG_SEP);

    Path configClassPath = getConfigClassPath(className);
    File configClassFile = configClassPath.toFile();

    byte[] modBytecode = 
        ConfigFieldsDefiner.define(className, configClassFile, config);

    writeToClassFile(modBytecode, configClassPath.toString());
  }

  private static boolean isClassNameValid(String className) {
    return className != null && className.length() != 0; 
  }

  private static Path getConfigClassPath(String className) {
    if(!isClassNameValid(className)) {
      getLog().error(String.format("Invalid class name: %s", className));
      throw new RuntimeException();
    }

    final String targetClasses = System.getProperty(CONFIG_COMPILEDOUTPUT);
    Path path = Paths.get(targetClasses);

    if(Files.exists(path)) {
      getLog().info(path.toString());
    } else {
      getLog().error("Unable to find the classes directory :(");
      throw new RuntimeException();
    }

    String[] classNameSplit = className.split(JAVA_PKG_SEP);
    for(int i = 0; i <= classNameSplit.length - 2; i += 1) {
      final String splitItem = classNameSplit[i];
      Path temp = path.resolve(splitItem);
      if(!Files.exists(temp)) {
        final String errMsg = 
            String.format("Path is invalid: %s", String.valueOf(temp));

        getLog().error(errMsg);
        throw new RuntimeException(errMsg);
      }

      path = temp;
    }

    Path configClassPath = 
        path.resolve(classNameSplit[classNameSplit.length - 1] + ".class");
    final String confClsPathName = configClassPath.toString();
    if(Files.exists(configClassPath)) {
      getLog().info(String.format("Updating config class: %s", confClsPathName));
    } else {
      final String errMsg = String.format("Path invalid: %s", confClsPathName);
      getLog().error(errMsg);
      throw new RuntimeException(errMsg);
    }

    return configClassPath;
  }

  private static void writeToClassFile(byte[] bytecode, String filePath) {
    try {
      PrintStream byteStream = new PrintStream(filePath);
      byteStream.write(bytecode);
      byteStream.close();
    } catch(IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
