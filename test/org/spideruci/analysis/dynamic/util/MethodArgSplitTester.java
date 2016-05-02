package org.spideruci.analysis.dynamic.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodArgSplitTester {

  @Test
  public void hasSinglePrimitiveOnly() {
    // given
    final String desc = "(B";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B"}, argSplit);
  }
  
  @Test
  public void hasSinglePrimitiveMultiArrayOnly() {
    // given
    final String desc = "([[[B)";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[[[B"}, argSplit);
  }
  
  @Test
  public void testPrimitiveOnlyWithAllPrimitiveTypes() {
    // given
    final String desc = "BCDFIJSZ";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B", "C", "D", "F", "I", "J", "S", "Z"}, argSplit);
  }
  
  @Test
  public void testPrimitiveOnlyWithAllPrimitiveMultiArrayTypes() {
    // given
    final String desc = "[[B[[[C[[D[[[[F[[J[[S[[Z";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[[B", "[[[C", "[[D", "[[[[F", "[[J", "[[S", "[[Z"}, argSplit);
  }
  
  @Test
  public void testPrimitiveOnlyWithRepitions() {
    // given
    final String desc = "BBB";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B", "B", "B"}, argSplit);
  }
  
  @Test
  public void testSingleReference() {
    // given
    final String desc = "Ljava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSingleMultiArrayReference() {
    // given
    final String desc = "[[Ljava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[[Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSingleArrayReference() {
    // given
    final String desc = "[Ljava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSinglePrimitiveAndReference() {
    // given
    final String desc = "BLjava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B", "Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSinglePrimitiveAndReferenceMultiArray() {
    // given
    final String desc = "B[[Ljava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B", "[[Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSinglePrimitiveMultiArrayAndReference() {
    // given
    final String desc = "[[BLjava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[[B", "Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSingleReferenceAndPrimitive() {
    // given
    final String desc = "Ljava/lang/String;B";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Ljava/lang/String;", "B"}, argSplit);
  }

  @Test
  public void testSinglePrimitiveAndArrayReference() {
    // given
    final String desc = "B[Ljava/lang/String;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"B", "[Ljava/lang/String;"}, argSplit);
  }
  
  @Test
  public void testSingleArrayReferenceAndPrimitive() {
    // given
    final String desc = "[Ljava/lang/String;B";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[Ljava/lang/String;", "B"}, argSplit);
  }
  
  @Test
  public void testSingleArrayReferenceAndReference() {
    // given
    final String desc = "[Ljava/lang/String;Ljava/lang/Object;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[Ljava/lang/String;", "Ljava/lang/Object;"}, argSplit);
  }
  
  @Test
  public void testSingleReferenceAndArrayReference() {
    // given
    final String desc = "Ljava/lang/String;[Ljava/lang/Object;";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Ljava/lang/String;", "[Ljava/lang/Object;"}, argSplit);
  }
  
  @Test
  public void testSingleReferenceAndMultiplePrimitives() {
    // given
    final String desc = "Ljava/lang/String;BCDFJSZ";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Ljava/lang/String;", "B", "C", "D", "F", "J", "S", "Z"}, argSplit);
  }
  
  @Test
  public void testSingleArrayReferenceAndMultiplePrimitives() {
    // given
    final String desc = "[Ljava/lang/String;BCDFJSZ";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[Ljava/lang/String;", "B", "C", "D", "F", "J", "S", "Z"}, argSplit);
  }
  
  @Test
  public void testSingleReferenceAndRepeatingPrimitives() {
    // given
    final String desc = "Ljava/lang/String;BBB";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Ljava/lang/String;", "B", "B", "B"}, argSplit);
  }
  
  @Test
  public void testSingleArrayReferenceAndRepeatingPrimitives() {
    // given
    final String desc = "[Ljava/lang/String;BBB";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"[Ljava/lang/String;", "B", "B", "B"}, argSplit);
  }
  
  @Test
  public void testRepeatingPrimitivesAndSingleArrayReferenceAndRepeatingPrimitives() {
    // given
    final String desc = "ZZ[Ljava/lang/String;BBB";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Z", "Z", "[Ljava/lang/String;", "B", "B", "B"}, argSplit);
  }
  
  @Test
  public void testRepeatingPrimitivesAndSingleReferenceAndRepeatingPrimitives() {
    // given
    final String desc = "ZZLjava/lang/String;BBB";
    // when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    // then
    assertArrayEquals(new String[] {"Z", "Z", "Ljava/lang/String;", "B", "B", "B"}, argSplit);
  }
  
  @Test
  public void testZipFile$access$1400() {
    //given
    final String desc = "(JJJ[BII)I";
    //when
    final String[] argSplit = MethodDescSplitter.getArgTypeSplit(desc);
    //then
    assertArrayEquals(new String[] {"J", "J", "J", "[B", "I", "I"}, argSplit);
    
  }
}
