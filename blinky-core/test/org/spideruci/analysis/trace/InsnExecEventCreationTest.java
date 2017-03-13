package org.spideruci.analysis.trace;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.TraceLogger;
import org.spideruci.analysis.trace.events.props.InsnExecPropNames;
import org.spideruci.analysis.trace.events.props.VarInsnExecPropNames;

public class InsnExecEventCreationTest extends ExecutionEventTestScafold {
  
  public InsnExecEventCreationTest() {
    // GIVEN
    super();
    // And
    this.insnType = EventType.$var$;

    // WHEN
    actualEvent = EventBuilder.buildInsnExecEvent(id, dynamicHostId, 
        insnId, insnType, vitalState);
    
    // And
    serializedEvent = TraceEvent.valueOf(actualEvent.toString());
  }

  // THEN
  @Test
  public void createdExecEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(actualEvent);
  }

  // And
  @Test
  public void shouldCreateExecEventWithExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(actualEvent);
  }

  // And
  @Test
  public void createdExecEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(actualEvent);
  }

  // And
  @Test
  public void serializedExecEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(serializedEvent);
  }

  // And
  @Test
  public void serializedExecEventShoudHaveExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(serializedEvent);
  }

  // And
  @Test
  public void serializedExecEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(serializedEvent);
  }

  private void thenVerifyBasicEventPropertiesByPropertyNames(TraceEvent actualEvent) {
    
    assertEquals(vitalState[TraceLogger.THREAD_ID], Long.parseLong(actualEvent.getProp(InsnExecPropNames.THREAD_ID)) );
    assertEquals(vitalState[TraceLogger.TIMESTAMP], Long.parseLong(actualEvent.getProp(InsnExecPropNames.TIMESTAMP)) );
    assertEquals(dynamicHostId, actualEvent.getProp(InsnExecPropNames.DYN_HOST_ID) );
    assertEquals(insnId, actualEvent.getProp(InsnExecPropNames.INSN_EVENT_ID) );
    assertEquals(insnType, EventType.valueOf(actualEvent.getProp(InsnExecPropNames.INSN_EVENT_TYPE)) );
  }
}
