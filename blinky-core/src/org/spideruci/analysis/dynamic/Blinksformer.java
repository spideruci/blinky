package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;
import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;
import static org.spideruci.analysis.logging.ErrorLogManager.FAILD;
import static org.spideruci.analysis.logging.ErrorLogManager.SKIPD;
import static org.spideruci.analysis.logging.ErrorLogManager.SUXES;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.spideruci.analysis.statik.instrumentation.Config;
import org.spideruci.analysis.statik.instrumentation.OfflineInstrumenter;
import org.spideruci.analysis.dynamic.RuntimeClassRedefiner.RedefinitionTargets;
import org.spideruci.analysis.logging.ErrorLogManager;
import org.spideruci.analysis.statik.instrumentation.ClassInstrumenter;
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
      OfflineInstrumenter.isActive = isRuntime;
      ClassInstrumenter ins = new ClassInstrumenter();
      instrumentedBytes = ins.instrument(className, classBytes, null);
      OfflineInstrumenter.isActive = false;
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
    
    if(className.startsWith("java/lang")
        || className.startsWith("java/io")
        || className.startsWith("java/nio")
        || className.equals("java/io/Writer")
        || className.equals("java/io/BufferedWriter")
        || className.equals("java/io/OutputStreamWriter")
        || className.equals("java/io/PrintStream")
        || className.equals("java/io/FilterOutputStream")
        || className.equals("java/io/OutputStream")
        || className.equals("java/io/PrintWriter")) {
      return !shouldInstrument;
    }
    
    final boolean classUnderSpideruciNamespace = 
        className.startsWith(Constants.SPIDER_NAMESPACE)
        || className.startsWith(Constants.SPIDER_NAMESPACE2);
    
    if(classUnderSpideruciNamespace
        && (className.contains("test") || className.contains("subject")) ) {
      return shouldInstrument; // shouldInstrument
    }
    
    if(className.contains("Test")
        || className.contains("Mockito") 
        || className.contains("Mock")) {
      return !shouldInstrument; // shouldNotInstrument;
    }

    if(RedefinitionTargets.isException(className)) { // TODO abstract this away?
      return !shouldInstrument;
    }
    
    if(Config.checkInclusionList) {
      for(String item : Config.inclusionList) {
        if(className.startsWith(item)) {
          return shouldInstrument;
        }
      }
    }
    
    for(String item : Config.exclusionList) {
      if(className.startsWith(item)) {
        return !shouldInstrument;
      }
    }
    
    return shouldInstrument;
  }

}
