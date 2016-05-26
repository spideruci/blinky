/*
@author Charles.Y.Feng
@date May 12, 2016 5:01:58 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Frequencies1 implements Frequencies {
  public static List<Map.Entry<String, Integer>> sort(HashMap<String, Integer> wordsFreq){
    List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>(wordsFreq.entrySet());
    Collections.sort(word_freqs_tmp,
        new Comparator<Map.Entry<?, Integer>>() {
      public int compare(Map.Entry<?, Integer> o1,
          Map.Entry<?, Integer> o2) {
        if (o1.getValue() > o2.getValue())
          return -1;
        else if (o1.getValue() < o2.getValue())
          return 1;
        return 0;
      }
    });
    return word_freqs_tmp;
  }

  @Override
  public Map<String, Integer> top25(ArrayList<String> wordsList){
    HashMap<String,Integer> wordsFrequency = new HashMap<String,Integer>();
    for(int i = 0; i < wordsList.size(); i++) {
      String s = wordsList.get(i);
      if(wordsFrequency.containsKey(s)) {
        int count = wordsFrequency.get(s);
        wordsFrequency.put(s, count + 1);
      } else
        wordsFrequency.put(s, 1);
    }

    List<Map.Entry<String, Integer>> wordsFreq = sort(wordsFrequency);
    HashMap<String, Integer> top25 = new LinkedHashMap<String, Integer>();
    for(int i = 0; i < 25; i++) {
      top25.put(wordsFreq.get(i).getKey(), wordsFreq.get(i).getValue());
    }

    return top25;
  }
}
