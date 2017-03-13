package org.spideruci.analysis.trace;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.TraceLogger;
import org.spideruci.analysis.trace.events.props.ArrayInsnExecPropNames;
import org.spideruci.analysis.trace.events.props.InvokeInsnExecPropNames;

@RunWith(Parameterized.class)
public class ArrayInsnExecEventCreationTest extends ExecutionEventTestScafold {
  final private String arrayElement;
  final private int arrayRefId;
  final private int elementIndex;
  final private int arrayLength;
  
  @Parameters
  public static Collection<Object[]> data() {
      return Arrays.asList(new Object[][] {     
               { EventType.$arrayload$ }, { EventType.$arraystore$ }  
         });
  }

  public ArrayInsnExecEventCreationTest(EventType execInsnType) {
    // GIVEN
    super();
    // And
    this.insnType = execInsnType;
    // And
    this.arrayElement = "12345678";
    this.arrayRefId = 87654321;
    this.elementIndex = 1;
    this.arrayLength = 10;

    // WHEN
    actualEvent = EventBuilder.buildArrayInsnExecEvent(id, dynamicHostId, 
        insnId, insnType, vitalState, arrayRefId, elementIndex, 
        arrayElement, arrayLength);
    
    // And
    serializedEvent = TraceEvent.valueOf(actualEvent.toString());
  }

  // THEN
  @Test
  public void shouldCreateExecArrayEventWithGivenArrayElement() {
    assertEquals(arrayElement, actualEvent.getProp(ArrayInsnExecPropNames.ARRAY_ELEMENT));
  }
  
  // And
  @Test
  public void shouldCreateExecArrayEventWithGivenArrayRefId() {
    assertEquals(arrayRefId, Integer.parseInt(actualEvent.getProp(ArrayInsnExecPropNames.ARRAYREF_ID)) );
  }
  
  //And
  @Test
  public void shouldCreateExecArrayEventWithGivenElementIndex() {
    assertEquals(elementIndex, Integer.parseInt(actualEvent.getProp(ArrayInsnExecPropNames.ELEMENT_INDEX)) );
  }
  
  //And
  @Test
  public void shouldCreateExecArrayEventWithGivenArrayLength() {
    assertEquals(arrayLength, Integer.parseInt(actualEvent.getProp(ArrayInsnExecPropNames.ARRAY_LENGTH)) );
  }

  // And
  @Test
  public void createdExecArrayEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(actualEvent);
  }

  // And
  @Test
  public void shouldCreateExecArrayEventWithExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(actualEvent);
  }

  // And
  @Test
  public void createdExecArrayEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(actualEvent);
  }

  // And
  @Test
  public void serializedEventShouldHaveGivenArrayElement() {
    assertEquals(arrayElement, serializedEvent.getProp(ArrayInsnExecPropNames.ARRAY_ELEMENT));
  }
  
  // And
  @Test
  public void serializedEventShouldHaveGivenArrayRefId() {
    assertEquals(arrayRefId, Integer.parseInt(serializedEvent.getProp(ArrayInsnExecPropNames.ARRAYREF_ID)) );
  }
  
  //And
  @Test
  public void serializedEventShouldHaveGivenElementIndex() {
    assertEquals(elementIndex, Integer.parseInt(serializedEvent.getProp(ArrayInsnExecPropNames.ELEMENT_INDEX)) );
  }
  
  //And
  @Test
  public void serializedEventShouldHaveGivenArrayLength() {
    assertEquals(arrayLength, Integer.parseInt(serializedEvent.getProp(ArrayInsnExecPropNames.ARRAY_LENGTH)) );
  }

  //And
  @Test
  public void serializedExecArrayEventShouldGetBasicPropertiesByName() {
    thenVerifyBasicEventPropertiesByPropertyNames(serializedEvent);
  }

  // And
  @Test
  public void serializedExecArrayEventShoudHaveExecEvenTypeAndGivenEventId() {
    thenVerifyEventIdAndType(serializedEvent);
  }

  // And
  @Test
  public void serializedExecArrayEventShouldGetBasicProperties() {
    thenVerifyBasicEventProperties(serializedEvent);
  }

  private void thenVerifyBasicEventPropertiesByPropertyNames(TraceEvent actualEvent) {
    
    assertEquals(vitalState[TraceLogger.THREAD_ID], Long.parseLong(actualEvent.getProp(ArrayInsnExecPropNames.THREAD_ID)) );
    assertEquals(vitalState[TraceLogger.TIMESTAMP], Long.parseLong(actualEvent.getProp(ArrayInsnExecPropNames.TIMESTAMP)) );
    assertEquals(dynamicHostId, actualEvent.getProp(ArrayInsnExecPropNames.DYN_HOST_ID) );
    assertEquals(insnId, actualEvent.getProp(ArrayInsnExecPropNames.INSN_EVENT_ID) );
    assertEquals(insnType, EventType.valueOf(actualEvent.getProp(ArrayInsnExecPropNames.INSN_EVENT_TYPE)) );
  }
}
