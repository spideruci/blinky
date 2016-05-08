package org.spideruci.analysis.trace;

import static org.junit.Assert.assertEquals;

import org.spideruci.analysis.dynamic.Profiler;

public class ExecutionEventTestScafold {
  
  // GIVEN
  protected final int id = 1;
  protected final String dynamicHostId = "333";
  protected final String insnId = "1";
  protected final long[] vitalState = new long[] {1, 1, 1};
  protected EventType insnType;
  protected TraceEvent actualEvent;
  protected TraceEvent serializedEvent;
  
  // THEN
  protected void thenVerifyEventIdAndType(TraceEvent actualEvent) {
    assertEquals(EventType.$$$, actualEvent.getType());
    assertEquals(id, actualEvent.getId());
  }
  
  // And
  protected void thenVerifyBasicEventProperties(TraceEvent actualEvent) {
    assertEquals(vitalState[Profiler.THREAD_ID], Long.parseLong(actualEvent.getExecThreadId()) );
    assertEquals(vitalState[Profiler.TIMESTAMP], Long.parseLong(actualEvent.getExecTimestamp()) );
    assertEquals(vitalState[Profiler.CALLDEPTH], Long.parseLong(actualEvent.getExecCalldepth()) );
    assertEquals(dynamicHostId, actualEvent.getExecInsnDynHost());
    assertEquals(insnId, actualEvent.getExecInsnEventId());
    assertEquals(insnType, actualEvent.getExecInsnType());
    
  }

}
