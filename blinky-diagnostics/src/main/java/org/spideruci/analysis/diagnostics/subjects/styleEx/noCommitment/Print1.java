/*
@author Charles.Y.Feng
@date May 12, 2016 5:01:44 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;


import java.util.Iterator;
import java.util.Map;


public class Print1 implements Print {
  public void print(Object counts){
    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, Integer>> it = ((Map<String, Integer>)counts).entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Integer> pairs = it.next();
      String line = pairs.getKey() + "  -  " + pairs.getValue();
      System.out.println(line);
    }
  }
}
