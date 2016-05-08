package org.spideruci.analysis.util.caryatid;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MethodArgSplitInitialsTest {
  
  // givens
  private String descOrMethodName;
  private char[] expStaticInitials;
  private char[] expInstanceInitials;

  public MethodArgSplitInitialsTest(String input, char[] output) {
    descOrMethodName = input;
    expStaticInitials = output;
    expInstanceInitials = new char[output.length + 1];
    expInstanceInitials[0] = 'L';
    for(int i = 0; i < output.length; i += 1) {
      expInstanceInitials[i + 1] = output[i];
    }
  }
  
  
  @Test
  public void test() {
    // when
    final char[] argSplit = Helper.getArgInitials2(descOrMethodName, true);
    final char[] instanceArgSplit = Helper.getArgInitials2(descOrMethodName, false);
    // then
    assertArrayEquals(expStaticInitials, argSplit);
    assertArrayEquals(expInstanceInitials, instanceArgSplit);
  }
  
  @Parameters
  public static Collection<Object[]> data() {
      return Arrays.asList(new Object[][] {     
               { "B", new char[] {'B'} }, 
               { "[[[B", new char[] {'['} },
               { "BCDFJSZ", new char[] {'B', 'C', 'D', 'F', 'J', 'S', 'Z'} },
               { "[[B[[[C[[D[[[[F[[J[[S[[Z", new char[] {'[', '[', '[', '[', '[', '[', '['} },
               { "BBB", new char[] {'B', 'B', 'B'} },
               { "BBB", new char[] {'B', 'B', 'B'} },
               
               { "Ljava/lang/String;BBB", new char[] {'L', 'B', 'B', 'B'} },
               { "BLjava/lang/String;BB", new char[] {'B', 'L', 'B', 'B'} },
               { "BBLjava/lang/String;B", new char[] {'B', 'B', 'L', 'B'} },
               { "BBBLjava/lang/String;", new char[] {'B', 'B', 'B', 'L'} },
               { "BLjava/lang/String;BLjava/lang/String;", new char[] {'B', 'L', 'B', 'L'} },
               
               
               { "Ljava/lang/String;", new char[] {'L'} },
               { "[Ljava/lang/String;", new char[] {'['} },
               { "[[Ljava/lang/String;", new char[] {'['} },
               
               { "BLjava/lang/String;", new char[] {'B', 'L'} },
               { "B[Ljava/lang/String;", new char[] {'B', '['} },
               { "B[[Ljava/lang/String;", new char[] {'B', '['} },
               { "B[[[Ljava/lang/String;", new char[] {'B', '['} },
               
               { "[BLjava/lang/String;", new char[] {'[', 'L'} },
               { "[[BLjava/lang/String;", new char[] {'[', 'L'} },
               { "[[[BLjava/lang/String;", new char[] {'[', 'L'} },
               
               { "[B[Ljava/lang/String;", new char[] {'[', '['} },
               { "[B[[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[B[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[B[[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[[B[[[[Ljava/lang/String;", new char[] {'[', '['} },
               
               { "Ljava/lang/String;B", new char[] {'L', 'B'} },
               { "[Ljava/lang/String;B", new char[] {'[', 'B'} },
               { "[[Ljava/lang/String;B", new char[] {'[', 'B'} },
               { "[[[[Ljava/lang/String;B", new char[] {'[', 'B'} },
               
               { "Ljava/lang/String;[B", new char[] {'L', '['} },
               { "Ljava/lang/String;[[B", new char[] {'L', '['} },
               { "Ljava/lang/String;[[[B", new char[] {'L', '['} },
               
               { "[Ljava/lang/String;[B", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[B", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[[B", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[[[[B", new char[] {'[', '['} },
               { "[Ljava/lang/String;[[[B", new char[] {'[', '['} },
               
               { "JJJ", new char[] {'J', 'J', 'J'} },
               { "JJJ", new char[] {'J', 'J', 'J'} },
               
               { "Ljava/lang/String;JJJ", new char[] {'L', 'J', 'J', 'J'} },
               { "JLjava/lang/String;JJ", new char[] {'J', 'L', 'J', 'J'} },
               { "JJLjava/lang/String;J", new char[] {'J', 'J', 'L', 'J'} },
               { "JJJLjava/lang/String;", new char[] {'J', 'J', 'J', 'L'} },
               { "JLjava/lang/String;JLjava/lang/String;", new char[] {'J', 'L', 'J', 'L'} },
               
               
               { "Ljava/lang/String;", new char[] {'L'} },
               { "[Ljava/lang/String;", new char[] {'['} },
               { "[[Ljava/lang/String;", new char[] {'['} },
               
               { "JLjava/lang/String;", new char[] {'J', 'L'} },
               { "J[Ljava/lang/String;", new char[] {'J', '['} },
               { "J[[Ljava/lang/String;", new char[] {'J', '['} },
               { "J[[[Ljava/lang/String;", new char[] {'J', '['} },
               
               { "[JLjava/lang/String;", new char[] {'[', 'L'} },
               { "[[JLjava/lang/String;", new char[] {'[', 'L'} },
               { "[[[JLjava/lang/String;", new char[] {'[', 'L'} },
               
               { "[J[Ljava/lang/String;", new char[] {'[', '['} },
               { "[J[[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[J[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[J[[Ljava/lang/String;", new char[] {'[', '['} },
               { "[[[J[[[[Ljava/lang/String;", new char[] {'[', '['} },
               
               { "Ljava/lang/String;J", new char[] {'L', 'J'} },
               { "[Ljava/lang/String;J", new char[] {'[', 'J'} },
               { "[[Ljava/lang/String;J", new char[] {'[', 'J'} },
               { "[[[[Ljava/lang/String;J", new char[] {'[', 'J'} },
               
               { "Ljava/lang/String;[J", new char[] {'L', '['} },
               { "Ljava/lang/String;[[J", new char[] {'L', '['} },
               { "Ljava/lang/String;[[[J", new char[] {'L', '['} },
               
               { "[Ljava/lang/String;[J", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[J", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[[J", new char[] {'[', '['} },
               { "[[Ljava/lang/String;[[[[J", new char[] {'[', '['} },
               { "[Ljava/lang/String;[[[J", new char[] {'[', '['} },
               
               { "[Ljava/lang/String;Ljava/lang/OJject;", new char[] {'[', 'L'} },
               { "[[Ljava/lang/String;Ljava/lang/OJject;", new char[] {'[', 'L'} },
               { "[[[Ljava/lang/String;Ljava/lang/OJject;", new char[] {'[', 'L'} },
               
               { "Ljava/lang/String;[Ljava/lang/OJject;", new char[] {'L', '['} },
               { "Ljava/lang/String;[[Ljava/lang/OJject;", new char[] {'L', '['} },
               { "Ljava/lang/String;[[[[Ljava/lang/OJject;", new char[] {'L', '['} },
               
               { "[Ljava/lang/String;[[[[Ljava/lang/Object;", new char[] {'[', '['} },
               
               { "Ljava/lang/String;BCDFJSZI", new char[] {'L', 'B', 'C', 'D', 'F', 'J', 'S', 'Z', 'I'} },
               { "[Ljava/lang/String;BCDFJSZI", new char[] {'[', 'B', 'C', 'D', 'F', 'J', 'S', 'Z', 'I'} },
               { "BCDF[Ljava/lang/String;BCDFJSZI", new char[] {'B', 'C', 'D', 'F', '[', 'B', 'C', 'D', 'F', 'J', 'S', 'Z', 'I'} },
               
               { "[Ljava/lang/String;BBB", new char[] {'[', 'B', 'B', 'B'} },
               { "ZZ[Ljava/lang/String;BBB", new char[] {'Z', 'Z', '[', 'B', 'B', 'B'} },
               { "ZZLjava/lang/String;BBB", new char[] {'Z', 'Z', 'L', 'B', 'B', 'B'} },
               
               { "(JJJ[BII)I", new char[] {'J', 'J', 'J', '[', 'I', 'I'} },
               {"(I"
                   + "Lorg/apache/fop/pdf/PDFResources;"
                   + "II"
                   + "Ljava/util/List;"
                   + "DD"
                   + "Ljava/util/List;"
                   + "Ljava/util/List;"
                   + "Ljava/lang/StringBuffer;)Lorg/apache/fop/pdf/PDFPattern;", 
                   new char[] {'I', 'L', 'I', 'I', 'L', 'D', 'D', 'L', 'L', 'L'}}
         });
  }

}