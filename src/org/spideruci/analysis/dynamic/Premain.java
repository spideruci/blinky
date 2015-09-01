package org.spideruci.analysis.dynamic;

import java.lang.instrument.Instrumentation;

public class Premain {
  public static void premain(String agentArguments, 
      Instrumentation instrumentation) {
    boolean tempGuard = Profiler.$guard1$; 
    Profiler.$guard1$ = true;
    Profiler.initProfiler(agentArguments);
    instrumentation.addTransformer(new Transformer());
    Profiler.$guard1$ = tempGuard;
  }
}
