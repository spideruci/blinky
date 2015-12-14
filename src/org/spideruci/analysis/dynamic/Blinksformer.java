package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;
import static org.spideruci.analysis.util.ErrorLogManager.SUXES;
import static org.spideruci.analysis.util.ErrorLogManager.SKIPD;
import static org.spideruci.analysis.util.ErrorLogManager.FAILD;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.dynamic.RuntimeClassRedefiner.RedefinitionTargets;
import org.spideruci.analysis.statik.instrumentation.ClassInstrumenter;
import org.spideruci.analysis.util.ByteCodePrinter;
import org.spideruci.analysis.util.Constants;
import org.spideruci.analysis.util.ErrorLogManager;

public class Blinksformer implements ClassFileTransformer {

  public Blinksformer() {
    super();
  }

  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classBytes) throws IllegalClassFormatException {
    
    File blinkyErrorLogPath = new File(ByteCodePrinter.bytecodePrintPath);
    
    if(!blinkyErrorLogPath.exists() || !blinkyErrorLogPath.isDirectory()) {
      blinkyErrorLogPath.mkdirs();
    }
    
    boolean isRetransformTarget = 
        (Premain.allowRetransform && RedefinitionTargets.isTarget(className));

    if(!shouldInstrument(className) && !isRetransformTarget) {
      ErrorLogManager.logClassTxStatus(className, false, SKIPD);
      return classBytes;
    }
    
    byte[] instrumentedBytes = instrumentClass(className, classBytes, 
        false /*isRuntime*/);

    return instrumentedBytes;
  }

  /**
   * @param className
   * @param classBytes
   * @return
   */
  static byte[] instrumentClass(String className, byte[] classBytes, 
      boolean isRuntime) {
    byte[] instrumentedBytes = null;
    try {
      ClassInstrumenter ins = new ClassInstrumenter();
      instrumentedBytes = ins.instrument(className, classBytes, null);
//    ByteCodePrinter.printToFile(className, classBytes, instrumentedBytes);
      ErrorLogManager.logClassTxStatus(className, isRuntime, SUXES);
    } catch(Exception ex) {
//    ByteCodePrinter.printToFile(className, classBytes, instrumentedBytes);
      ex.printStackTrace(REAL_ERR);
      ErrorLogManager.logClassTxStatus(className, isRuntime, FAILD);
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
