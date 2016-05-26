/*
@author Charles.Y.Feng
@date May 11, 2016 3:36:02 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h2>Style Requirement: Candy Factory</h2> 
 * <p>	
 * The candy factory programming style tries to achieve this kind of mathematical purity by </br>
 * seeing everything as relations mapping one set of inputs to one set of outputs. 
 * <p>
 * This constraint is very strong: in the pure pipeline style, the world outside boxed </br> 
 * functions doesn't exist, other than in the beginning, as the source of input to a computation,</br>
 * and at the end, as the receiver of the output. The program needs to be expressed as boxed</br> 
 * functions and function composition. Unfortunately, our term frequency program needs to read data</br>
 * from files, so it isn't completely pure.
 * </p>
 */
public class CandyFactory {

  public static String read_file(String path_to_file) {
    try {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new FileReader(path_to_file));
      int v = -1;
      while ((v = br.read()) != -1) {
        sb.append((char) (v));
      }
      return sb.toString();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String filter_chars_and_normalize(String str_data) {
    return str_data.toLowerCase().replaceAll("([\\W_])+", " ");
  }

  public static List<String> scan(String str_data) {
    List<String> list = new ArrayList<String>();
    String[] words = str_data.split(" ");
    for (String w : words) {
      if (w.trim().length() > 1) {
        list.add(w.trim());
      }  
    }
    return list;
  }

  public static List<String> remove_stop_words(List params) {
    try {

      List<String> word_list = new ArrayList<String>();
      Object[] objArrays = (Object[])params.get(0);
      for(Object o : objArrays){
        word_list.add(o.toString());
      }

      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new FileReader(
          params.get(1).toString()));
      String line = "";
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      Set<String> stop_words = new HashSet<String>();
      stop_words.addAll(Arrays.asList(sb.toString().split(",")));

      for (char ch = 'a'; ch <= 'z'; ch++) {
        stop_words.add(ch + "");
      }

      for (int i = 0; i < word_list.size(); i++) {
        if (stop_words.contains(word_list.get(i))) {
          word_list.remove(word_list.get(i));
          i--;
        }
      }
      return word_list;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Hashtable<String, Integer> frequencies(List<String> word_list) {
    Hashtable<String, Integer> map = new Hashtable<String, Integer>();
    for (int i = 0; i < word_list.size(); i++) {
      if (map.keySet().contains(word_list.get(i))) {
        map.put(word_list.get(i), map.get(word_list.get(i)) + 1);
      } else {
        map.put(word_list.get(i), 1);
      }
    }

    return map;
  }

  public static void print_all(List<Map.Entry<String, Integer>> word_freqs) {
    if (word_freqs.size() > 0) {
      System.out.println(word_freqs.get(0).getKey() + "  -  "
          + word_freqs.get(0).getValue());
      print_all(word_freqs.subList(1, word_freqs.size()));
    }
  }

  public static List<Map.Entry<String, Integer>> sort(
      Hashtable<String, Integer> word_freq) {
    List<Map.Entry<String, Integer>> word_freq_list = new ArrayList<Map.Entry<String, Integer>>();
    word_freq_list.addAll(word_freq.entrySet());
    Collections.sort(word_freq_list,
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
    return word_freq_list;
  }

  public static void main(String[] args) {
    //		String bookPath = args[0];
    //		String stopWordsPath = args[1];
    String stopWordsPath = Config.stopWordsPath;
    String bookPath = Config.bookPath;

    print_all(sort(
        frequencies(remove_stop_words(
            Arrays.asList(scan(filter_chars_and_normalize(read_file(bookPath))).toArray(),stopWordsPath)
            )))
        .subList(0, 25));
  }
}
