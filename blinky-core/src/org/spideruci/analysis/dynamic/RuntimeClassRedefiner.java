package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.logging.ErrorLogManager.SKIPD;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.spideruci.analysis.logging.ErrorLogManager;

public class RuntimeClassRedefiner implements ClassFileTransformer {

  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classfileBuffer) throws IllegalClassFormatException {
    
    if(!Premain.started && Premain.ended) {
      throw new RuntimeException("Premain signals end, with not-started signal.");
    }
    
    if(!Premain.started || (Premain.started && Premain.ended)) {
      return null;
    }
    
    boolean tempGuard = Profiler.$guard1$; 
    Profiler.$guard1$ = true;
    
    byte[] instrumentedBytes = null;

    if(className.equals("java/util/zip/ZipFile")) {
      ErrorLogManager.logClassTxStatus(className, true, SKIPD);
    } else if(RedefinitionTargets.isExactTarget(className)
        && !RedefinitionTargets.isException(className)) {
      instrumentedBytes = Blinksformer.instrumentClass(className, 
          classfileBuffer, true /*isRuntime*/);
    } else {
      ErrorLogManager.logClassTxStatus(className, true, SKIPD);
    }
    
    
    Profiler.$guard1$ = tempGuard;
    Profiler.REAL_OUT.println("RuntimeClassRedefiner:" + Profiler.$guard1$);
    return instrumentedBytes;
  }



  public static class RedefinitionTargets {

    public static boolean isTarget(String className) {
      for(String wildCard : wildCardTargets) {
        if(className.startsWith(wildCard)) {
          return true;
        }
      }
      return false;
    }

    public static boolean isExactTarget(String className) {
      
      for(String exactTarget : exactTargets) {
        if(className.equals(exactTarget)) {
          return true;
        }
      }
      return false;
    }
    
    public static boolean isException(String className) {
      for(String wildCard : wildCardExceptions) {
        if(className.startsWith(wildCard)) {
          return true;
        }
      }
      
      return false;
    }
    
    public static boolean isExactException(String className) {
      for(String exactTarget : exactExceptions) {
        if(className.equals(exactTarget)) {
          return true;
        }
      }
      return false;
    }
    
    
    private static final String[] wildCardTargets = new String[] {
//        "java/util",
//        "java/net/URLClassLoader", 
//        "java/security/SecureClassLoader",
//        "java/lang/ClassLoader"
//        "java/util/concurrent"
//        "java/security",
    };
    
    private static final String[] exactTargets = new String[] {
//    "java/util/Hashtable", "java/util/regex/Pattern",  
        "java/util/ArrayList",
//        "java/net/URLClassLoader", 
//        "java/security/SecureClassLoader",
//        "java/util/Arrays",
    };
    
    private static final String[] wildCardExceptions = new String[] {
        "java/security/AccessControl",
        // the class-loaders do not play well with the dependence analyzer.
        "java/net/URLClassLoader", 
        "java/security/SecureClassLoader", 
        "java/util/Arrays"
//        "java/util/concurrent/ThreadLocalRandom"
    };
    
    private static final String[] exactExceptions = new String[] {
//        "java/util/Hashtable", "java/util/regex/Pattern", "java/util/regex/Matcher"
        "java/net/URLClassLoader", 
        "java/security/SecureClassLoader",
        "java/util/zip/ZipFile",
        "java/security/AccessControl",
        "java/util/Arrays"
    };
  }

}

