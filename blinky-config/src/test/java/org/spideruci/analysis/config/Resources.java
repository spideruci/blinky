package org.spideruci.analysis.config;

import java.io.File;
import java.net.URL;

import org.junit.BeforeClass;
import org.spideruci.analysis.config.definer.TestClassScanner;

public class Resources {
  
  public final static String CLASSNAME = "org/spideruci/analysis/statik/instrumentation/Deputy";
  public final static String CLASS_FILE_NAME = "Deputy.class";
  public final static File CLASSFILE;
  
  static {
    ClassLoader classLoader = Resources.class.getClassLoader();
    URL classfileURL = classLoader.getResource(CLASS_FILE_NAME);
    CLASSFILE = new File(classfileURL.getFile());
  }

}
