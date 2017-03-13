package org.spideruci.analysis.trace;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.TraceLogger;
import org.spideruci.analysis.trace.events.props.InvokeInsnExecPropNames;

public class InvokeExecTraceEventCreationTest extends ExecutionEventTestScafold {
  final private String invokeSignature;

  public InvokeExecTraceEventCreationTest() {
    // GIVEN
    super();
    // And
    this.insnType = EventType.$invoke$;
    // And
    this.invokeSignature = "(Ljava/util/randomSignature;)V";

    // WHEN
    actualEvent = EventBuilder.buildInvokeInsnExecEvent(id, dynamicHostId, insnId, 
        insnType, vitalState, invokeSignature);
    // And
    serializedEvent = TraceEvent.valueOf(actualEvent.toString());
  }

  // THEN
  @Test
  public void shouldCreateExecInvokeEventWithGivenRuntimeSignature() {
    assertEquals(invokeSignature, actualEvent.getProp(InvokeInsnExecPropNames.RUNTIME_SIGNATURE));
  }

  // And
  @Test
  public void createdExecInvokeEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(actualEvent);
  }

  // And
  @Test
  public void shouldCreateExecInvokeEventWithExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(actualEvent);
  }

  // And
  @Test
  public void createdInvokeEnterEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(actualEvent);
  }

  //And
  @Test
  public void serializedEventShouldHaveGivenRuntimeSignature() {
    assertEquals(invokeSignature, serializedEvent.getProp(InvokeInsnExecPropNames.RUNTIME_SIGNATURE));
  }

  //And
  @Test
  public void serializedExecInvokeEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(serializedEvent);
  }

  // And
  @Test
  public void serializedExecInvokeEventShoudHaveExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(serializedEvent);
  }

  // And
  @Test
  public void serializedExecInvokeEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(serializedEvent);
  }

  private void thenVerifyBasicEventPropertiesByPropertyNames(TraceEvent actualEvent) {
    
    assertEquals(vitalState[TraceLogger.THREAD_ID], Long.parseLong(actualEvent.getProp(InvokeInsnExecPropNames.THREAD_ID)) );
    assertEquals(vitalState[TraceLogger.TIMESTAMP], Long.parseLong(actualEvent.getProp(InvokeInsnExecPropNames.TIMESTAMP)) );
    assertEquals(dynamicHostId, actualEvent.getProp(InvokeInsnExecPropNames.DYN_HOST_ID) );
    assertEquals(insnId, actualEvent.getProp(InvokeInsnExecPropNames.INSN_EVENT_ID) );
    assertEquals(insnType, EventType.valueOf(actualEvent.getProp(InvokeInsnExecPropNames.INSN_EVENT_TYPE)) );
  }
}
