package org.spideruci.analysis.dynamic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.statik.instrumentation.SourceLineInstrumenter;
import org.spideruci.util.Constants;

public class Transformer implements ClassFileTransformer {

  public Transformer() {
    super();
  }
  
  public static void premain(String agentArguments, 
      Instrumentation instrumentation) {
    instrumentation.addTransformer(new Transformer());
  }
  
  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classBytes) throws IllegalClassFormatException {
    byte[] instrumentedBytes = null;
    boolean shouldNotInstrument = false;
    
    shouldNotInstrument = shouldNotInstrument(className);
    
    if(shouldNotInstrument) {
      return classBytes;
    }
    
    try {
      SourceLineInstrumenter ins = new SourceLineInstrumenter();
      instrumentedBytes = ins.instrument(className, classBytes, null);
    } catch(Exception ex) {
      instrumentedBytes = classBytes;
    }
    
    return instrumentedBytes;
  }
  
  private boolean shouldNotInstrument(String className) {
    boolean shouldNotInstrument = false;
    if(className.startsWith(Constants.SPIDER_NAMESPACE)
        || className.startsWith(Constants.SPIDER_NAMESPACE2)) {
      if(className.contains("test")) {
        return false; // shouldInstrument
      } else {
        return true; // shouldNotInstrument
      }
    }
    
    for(String item : Deputy.exclusionList) {
      if(className.startsWith(item)) {
        return true;
      }
    }
    
    return shouldNotInstrument;
  }
  
}
