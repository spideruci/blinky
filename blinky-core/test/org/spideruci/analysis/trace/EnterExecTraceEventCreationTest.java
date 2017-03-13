package org.spideruci.analysis.trace;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.TraceLogger;
import org.spideruci.analysis.trace.events.props.EnterExecPropNames;

public class EnterExecTraceEventCreationTest extends ExecutionEventTestScafold {

  final private String runtimeSignature;
  
  public EnterExecTraceEventCreationTest() {
    // GIVEN
    super();
    // And
    this.insnType = EventType.$enter$;
    // And
    this.runtimeSignature = "(Ljava/util/randomSignature;)V";
    
    // WHEN
    actualEvent = EventBuilder.buildEnterExecEvent(id, dynamicHostId, insnId, 
        insnType, vitalState, runtimeSignature);
    // And
    serializedEvent = TraceEvent.valueOf(actualEvent.toString());
  }

  // THEN
  @Test
  public void shouldCreateExecEnterEventWithGivenRuntimeSignature() {
    assertEquals(runtimeSignature, actualEvent.getProp(EnterExecPropNames.RUNTIME_SIGNATURE));
  }

  // And
  @Test
  public void createdExecEnterEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(actualEvent);
  }

  // And
  @Test
  public void shouldCreateExecEnterEventWithExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(actualEvent);
  }

  // And
  @Test
  public void createdExecEnterEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(actualEvent);
  }

  //And
  @Test
  public void serializedEventShouldHaveGivenRuntimeSignature() {
    assertEquals(runtimeSignature, serializedEvent.getProp(EnterExecPropNames.RUNTIME_SIGNATURE));
  }

  //And
  @Test
  public void serializedExecEnterEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(serializedEvent);
  }

  // And
  @Test
  public void serializedExecEnterEventShoudHaveExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(serializedEvent);
  }

  // And
  @Test
  public void serializedExecEnterEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(serializedEvent);
  }

  private void thenVerifyBasicEventPropertiesByPropertyNames(TraceEvent actualEvent) {

    assertEquals(vitalState[TraceLogger.THREAD_ID], Long.parseLong(actualEvent.getProp(EnterExecPropNames.THREAD_ID)) );
    assertEquals(vitalState[TraceLogger.TIMESTAMP], Long.parseLong(actualEvent.getProp(EnterExecPropNames.TIMESTAMP)) );
    assertEquals(dynamicHostId, actualEvent.getProp(EnterExecPropNames.DYN_HOST_ID) );
    assertEquals(insnId, actualEvent.getProp(EnterExecPropNames.INSN_EVENT_ID) );
    assertEquals(insnType, EventType.valueOf(actualEvent.getProp(EnterExecPropNames.INSN_EVENT_TYPE)) );
  }

}
