package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import org.spideruci.analysis.statik.instrumentation.Config;

/**
 * WARNING: DO NOT HAVE ANY NON_STATIC IMPORTS (i.e. DEPENDENCIES) 
 * IN PREMAIN. FOR SOME REASON IT BLOCKS THE INSTRUMENTAION.
 * @author vpalepu
 *
 */
public class Premain {
  
  public static boolean allowRetransform = false;
  public static boolean started = false;
  public static boolean ended = false;
  
  public static void premain(String agentArguments, 
      Instrumentation instrumentation) {
    boolean tempGuard = Profiler.$guard1$; 
    Profiler.$guard1$ = true;
    
    started = true;
    
    Profiler.initProfiler(agentArguments);
    
    System.out.println("EXCLUSION LIST");
    for(int i = 0; i < Config.exclusionList.length; i += 1) {
      REAL_ERR.println(Config.exclusionList[i]);
    }
    
    System.out.println("INCLUSION LIST");
    for(int i = 0; i < Config.inclusionList.length; i += 1) {
      REAL_ERR.println(Config.inclusionList[i]);
    }
    
    System.out.println("RETX INCLUSION LIST");
    final int retxCount = Config.retransformInclusionList.length;
    RuntimeClassRedefiner.RedefinitionTargets.wildCardTargets = new String[retxCount];
    for(int i = 0; i < retxCount; i += 1) {
      RuntimeClassRedefiner.RedefinitionTargets.wildCardTargets[i] =
          Config.retransformInclusionList[i];
      REAL_ERR.println(Config.retransformInclusionList[i]);
    }
    
    instrumentation.addTransformer(new Blinksformer());
    
    if(Premain.allowRetransform 
        && instrumentation.isRetransformClassesSupported()) {
      REAL_ERR.println("retransforming!");
      instrumentation.addTransformer(new RuntimeClassRedefiner(), true);
      
      Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
      for(Class<?> loadedClass : loadedClasses) {
        if(!instrumentation.isModifiableClass(loadedClass)) {
          continue;
        }
        try {
          instrumentation.retransformClasses(loadedClass);
        } catch (UnmodifiableClassException e) {
          REAL_ERR.println(loadedClass);
          e.printStackTrace(REAL_ERR);
        }
      }
    } else {
      REAL_ERR.println("FEEDBACK: Class Retransformation is disabled.");
    }
    
    ended = true;
    Profiler.$guard1$ = tempGuard;
    
    synchronized (Profiler.REAL_OUT) {
      Profiler.REAL_OUT.println("Premain:" + Profiler.$guard1$);
    }
    
  }
}
