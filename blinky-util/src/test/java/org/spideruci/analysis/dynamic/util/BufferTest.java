package org.spideruci.analysis.dynamic.util;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BufferTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void appendingNullStringShouldThrowRuntimeException() {
    //given
    String str = null;
    Buffer buffer = new Buffer();
    
    //then
    thrown.expect(RuntimeException.class);
    thrown.expectMessage(equalTo("String sent to the buffer was null."));
    
    //when
    buffer.append(str);
  }
  
  @Test
  public void appendingStringsWithSpaceShouldRetainWhiteSpace() {
    //given
    String str = " vijay";
    String str2 = "  krishna";
    Buffer buffer = new Buffer();
    
    //when
    String concat = buffer.append(str).append(str2).toString();
    
    //then
    assertEquals(" vijay  krishna", concat);
  }
  
  @Test
  public void appendingOneStringShouldMatchOriginalString() {
  //given
    String str = "vijay";
    Buffer buffer = new Buffer();
    
    //when
    String concat = buffer.append(str).toString();
    
    //then
    assertEquals("vijay", concat);
  }
  
  @Test
  public void appendingEmptyStringShouldResultInEmptyString() {
    //given
    String str = "";
    Buffer buffer = new Buffer();
    
    //when
    String concat = buffer.append(str).toString();
    
    //then
    assertEquals("", concat);
    assertEquals(0, buffer.length());
    assertEquals(0, concat.length());
  }
  
  @Test
  public void clearShouldResultInEmptyString() {
    //given
    String str = "vijay is a ";
    String str2 = "graduate researcher.";
    Buffer buffer = new Buffer();
    
    //when
    buffer.append(str).append(str2).clear();
    
    //then
    assertEquals(0, buffer.length());
    
    //and
    assertEquals(0, buffer.toString().length());
  }
  
  @Test
  public void shouldAllowAppendAfterClearing() {
    //given
    String str = "vijay is a ";
    String str2 = "graduate researcher";
    Buffer buffer = new Buffer();
    
    //when
    buffer.append(str).clear().append(str2);
    
    //then
    assertEquals(str2, buffer.toString());
    assertNotEquals(str, buffer.toString());
  }
  
  @Test
  public void shouldPrependWhenInsertingAtPos0() {
    //given
    String str = "vijay is";
    String str2 = "graduate researcher ";
    Buffer buffer = new Buffer();
    
    //when
    buffer.append(str).insert(0, str2);
    
    //then
    assertEquals("graduate researcher vijay is", buffer.toString());
  }
  
  @Test
  public void shouldMessStringsUpWhenInsertingAtPos1() {
    //given
    String str = "vijay is";
    String str2 = "graduate researcher ";
    Buffer buffer = new Buffer();
    
    //when
    buffer.append(str).insert(1, str2);
    
    //then
    assertEquals("vgraduate researcher ijay is", buffer.toString());
  }
  
  @Test
  public void shouldMessStringsUpWhenInsertingAtPos2() {
    //given
    String str = "vijay is";
    String str2 = "graduate researcher ";
    Buffer buffer = new Buffer();
    
    //when
    buffer.append(str).insert(2, str2);
    
    //then
    assertEquals("vigraduate researcher jay is", buffer.toString());
  }

}
