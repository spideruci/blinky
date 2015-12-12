package org.spideruci.analysis.dynamic;

import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import org.spideruci.analysis.statik.instrumentation.Deputy;

public class Premain {
  public static void premain(String agentArguments, 
      Instrumentation instrumentation) {
    boolean tempGuard = Profiler.$guard1$; 
    Profiler.$guard1$ = true;
    Profiler.initProfiler(agentArguments);
    
    instrumentation.addTransformer(new Blinksformer());
    
    if(Deputy.allowRetransform 
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
    
    Profiler.$guard1$ = tempGuard;
  }
}
