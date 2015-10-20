package org.spideruci.analysis.dynamic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.statik.instrumentation.SourceLineInstrumenter;
import org.spideruci.analysis.util.ByteCodePrinter;
import org.spideruci.analysis.util.Constants;

public class Blinksformer implements ClassFileTransformer {

  public Blinksformer() {
    super();
  }

  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classBytes) throws IllegalClassFormatException {
    byte[] instrumentedBytes = null;

    if(!shouldInstrument(className)) {
      System.err.println("instrumentation skipped for " + className);
      return classBytes;
    }

    try {
      SourceLineInstrumenter ins = new SourceLineInstrumenter();
      instrumentedBytes = ins.instrument(className, classBytes, null);
      System.err.println("instrumentation successful for " + className);
    } catch(Exception ex) {
      ByteCodePrinter.printToFile(className, classBytes, instrumentedBytes);
      ex.printStackTrace();
      System.err.println("instrumentation failed for " + className);
      instrumentedBytes = classBytes;
    }

    return instrumentedBytes;
  }

  private boolean shouldInstrument(String className) {
    final boolean shouldInstrument = true;
    if(className.startsWith(Constants.SPIDER_NAMESPACE)
        || className.startsWith(Constants.SPIDER_NAMESPACE2)) {
      if(className.contains("test")) {
        return shouldInstrument; // shouldInstrument
      }
    }

    if(className.contains("Test")) {
      return !shouldInstrument; // shouldNotInstrument;
    }

    if(className.contains("Mockito") || className.contains("Mock")) {
      return !shouldInstrument; // shouldNotInstrument;
    }
    
    if(Deputy.checkInclusionList) {
      for(String item : Deputy.inclusionList) {
        if(className.startsWith(item)) {
          return shouldInstrument;
        }
      }
    }

    for(String item : Deputy.exclusionList) {
      if(className.startsWith(item)) {
        return !shouldInstrument;
      }
    }

    return shouldInstrument;
  }

}
