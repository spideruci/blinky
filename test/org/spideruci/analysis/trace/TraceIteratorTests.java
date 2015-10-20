package org.spideruci.analysis.trace;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.matchers.Any;
import org.spideruci.analysis.trace.io.TraceScanner;

public class TraceIteratorTests {

  @Test
  public void shouldHaveThreeNextTraceEvents() {
    //given
    TraceEvent[] events = { mock(TraceEvent.class), mock(TraceEvent.class), mock(TraceEvent.class) };
    TraceScanner scanner = new TraceScanner(events);
    TraceIterator iterator = scanner.traceIterator();
    
    //when and then
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
  }
  
  @Test
  public void shouldAllowIndefiniteHasNextChecksWithNoNexts() {
    //given
    TraceEvent[] events = { mock(TraceEvent.class) };
    TraceScanner scanner = new TraceScanner(events);
    TraceIterator iterator = scanner.traceIterator();
    
    //when and then
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
  }
  
  @Test
  public void shouldAllowThreeNexts() {
    //given
    TraceEvent[] events = { mock(TraceEvent.class), mock(TraceEvent.class), mock(TraceEvent.class) };
    when(events[0].getId()).thenReturn(1);
    when(events[1].getId()).thenReturn(2);
    when(events[2].getId()).thenReturn(3);
    TraceScanner scanner = new TraceScanner(events);
    TraceIterator iterator = scanner.traceIterator();
    
    //when and then
    assertEquals(iterator.next().getId(), 1);
    assertEquals(iterator.next().getId(), 2);
    assertEquals(iterator.next().getId(), 3);
  }
  
  @Test
  public void shouldAllowThreeNextsWithMultipleHasNextsInBetween() {
    //given
    TraceEvent[] events = { mock(TraceEvent.class), mock(TraceEvent.class), mock(TraceEvent.class) };
    when(events[0].getId()).thenReturn(1);
    when(events[1].getId()).thenReturn(2);
    when(events[2].getId()).thenReturn(3);
    TraceScanner scanner = new TraceScanner(events);
    TraceIterator iterator = scanner.traceIterator();
    
    //when and then
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().getId(), 1);
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().getId(), 2);
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().getId(), 3);
  }
  
  @Test
  public void shouldNotHaveAnyEventsAfterThreeNexts() {
    //given
    TraceEvent[] events = { mock(TraceEvent.class), mock(TraceEvent.class), mock(TraceEvent.class) };
    when(events[0].getId()).thenReturn(1);
    when(events[1].getId()).thenReturn(2);
    when(events[2].getId()).thenReturn(3);
    TraceScanner scanner = new TraceScanner(events);
    TraceIterator iterator = scanner.traceIterator();
    
    //when and then
    assertEquals(iterator.next().getId(), 1);
    assertEquals(iterator.next().getId(), 2);
    assertEquals(iterator.next().getId(), 3);
    assertFalse(iterator.hasNext());
  }
  
  @Test
  public void shouldNotHaveAnyEventsWhenLinesDontStartWith$() {
    //given
    Scanner scanner = new Scanner("t\nt\nt\n");
    
    //when
    TraceIterator iterator = new TraceIterator(scanner);
    
    //then
    assertFalse(iterator.hasNext());
  }
  
  @Test
  public void shouldHaveEventsWithLinesStartingWith$() {
    //given
    Scanner scanner = new Scanner("$$$,1,x,x,x,x,x\n$$$,2,x,x,x,x,x\n$$$,3,x,x,x,x,x\n");
    
    //when
    TraceIterator iterator = new TraceIterator(scanner);
    
    //then
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
  }
  
  @Test
  public void shouldAllowMultipleHasNextsWithNoNexts() {
    //given
    Scanner scanner = new Scanner("$$$,1,x,x,x,x,x");
    
    //when
    TraceIterator iterator = new TraceIterator(scanner);
    
    //then
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
  }
}
