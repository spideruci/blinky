package org.spideruci.analysis.trace;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.TraceLogger;
import org.spideruci.analysis.trace.events.props.FieldInsnExecPropNames;

public class FieldInsnExecEventCreationTest extends ExecutionEventTestScafold {
  final private String fieldId;
  final private String fieldOwnerId;
  
  public FieldInsnExecEventCreationTest() {
    // GIVEN
    super();
    // And
    this.insnType = EventType.$field$;
    // And
    this.fieldId = "12345678";
    this.fieldOwnerId = "87654321";

    // WHEN
    actualEvent = EventBuilder.buildFieldInsnExecEvent(id, dynamicHostId, 
        insnId, insnType, vitalState, fieldId, fieldOwnerId);
    
    // And
    serializedEvent = TraceEvent.valueOf(actualEvent.toString());
  }

  // THEN
  @Test
  public void shouldCreateExecFieldEventWithGivenFieldId() {
    assertEquals(fieldId, actualEvent.getProp(FieldInsnExecPropNames.FIELD_ID));
  }
  
  // And
  @Test
  public void shouldCreateExecFieldEventWithGivenFieldOwnerId() {
    assertEquals(fieldOwnerId, actualEvent.getProp(FieldInsnExecPropNames.FIELD_OWNER_ID) );
  }
  
  // And
  @Test
  public void createdExecFieldEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(actualEvent);
  }

  // And
  @Test
  public void shouldCreateExecFieldEventWithExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(actualEvent);
  }

  // And
  @Test
  public void createdExecFieldEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(actualEvent);
  }

  // And
  @Test
  public void serializedExecFieldEventShouldHaveGivenFieldId() {
    assertEquals(fieldId, serializedEvent.getProp(FieldInsnExecPropNames.FIELD_ID));
  }
  
  // And
  @Test
  public void serializedExecFieldEventShouldHaveGivenFieldOwnerId() {
    assertEquals(fieldOwnerId, serializedEvent.getProp(FieldInsnExecPropNames.FIELD_OWNER_ID) );
  }

  //And
  @Test
  public void serializedExecFieldEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(serializedEvent);
  }

  // And
  @Test
  public void serializedExecFieldEventShoudHaveExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(serializedEvent);
  }

  // And
  @Test
  public void serializedExecFieldEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(serializedEvent);
  }

  private void thenVerifyBasicEventPropertiesByPropertyNames(TraceEvent actualEvent) {
    
    assertEquals(vitalState[TraceLogger.THREAD_ID], Long.parseLong(actualEvent.getProp(FieldInsnExecPropNames.THREAD_ID)) );
    assertEquals(vitalState[TraceLogger.TIMESTAMP], Long.parseLong(actualEvent.getProp(FieldInsnExecPropNames.TIMESTAMP)) );
    assertEquals(dynamicHostId, actualEvent.getProp(FieldInsnExecPropNames.DYN_HOST_ID) );
    assertEquals(insnId, actualEvent.getProp(FieldInsnExecPropNames.INSN_EVENT_ID) );
    assertEquals(insnType, EventType.valueOf(actualEvent.getProp(FieldInsnExecPropNames.INSN_EVENT_TYPE)) );
  }
}
