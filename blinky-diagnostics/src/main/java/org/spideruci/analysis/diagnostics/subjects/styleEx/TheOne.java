/*
@author Charles.Y.Feng
@date May 11, 2016 5:27:39 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h2>Style Requirement: The One</h2>
 * <p>
 * This style is another variation in sequencing functions beyond the </br>
 * traditional function composition provided by most programming
 * languages. </br>
 * In this style of composing functions, we establish an abstraction ("the one"
 * ) </br>
 * that serves as the glue between values and functions. This abstraction </br>
 * provides two main operations: a wrap operation that takes a simple value
 * and </br>
 * returns an instance of the glue abstraction, and a bind operation that feeds
 * a wrapped value to a function.
 * </p>
 */
public class TheOne {
  public static Object read_file(Object path_to_file) {
    String data = null;
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(((String) path_to_file)));
      StringBuilder sb = new StringBuilder();
      int v = -1;
      while ((v = br.read()) != -1) {
        sb.append((char) v);
      }
      data = sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  public static Object filter_chars(Object str_data) {
    return ((String) str_data).replaceAll("[\\W_]+", " ");
  }

  public static Object normalize(Object str_data) {
    return ((String) str_data).toLowerCase();
  }

  public static Object scan(Object str_data) {
    List<String> word_list = new ArrayList<String>();
    word_list.addAll(Arrays.asList(((String) str_data).split(" ")));
    return word_list;
  }

  public static Object remove_stop_words(Object word_list) {
    ArrayList<String> word_list_tmp = (ArrayList<String>) word_list;
    Set<String> stop_words = new HashSet<String>();
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(Config.stopWordsPath));
      StringBuilder sb = new StringBuilder();
      int v = -1;
      while ((v = br.read()) != -1) {
        sb.append((char) v);
      }
      stop_words.addAll(Arrays.asList(sb.toString().split(",")));
      for (char ch = 'a'; ch <= 'z'; ch++) {
        stop_words.add(ch + "");
      }

      for (int i = 0; i < word_list_tmp.size(); i++) {
        if (stop_words.contains(word_list_tmp.get(i))) {
          word_list_tmp.remove(word_list_tmp.get(i));
          i--;
        }
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return word_list_tmp;
  }

  public static Object frequencies(Object word_list) {
    ArrayList<String> word_list_tmp = (ArrayList<String>) word_list;
    HashMap<String, Integer> wf = new HashMap<String, Integer>();
    for (String w : word_list_tmp) {
      if (wf.containsKey(w)) {
        wf.put(w, wf.get(w) + 1);
      } else {
        wf.put(w, 1);
      }
    }
    return wf;
  }

  public static Object sort(Object wf) {
    List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
    word_freqs_tmp.addAll(((HashMap<String, Integer>) wf).entrySet());
    Collections.sort(word_freqs_tmp, new Comparator<Map.Entry<?, Integer>>() {
      public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
        if (o1.getValue() > o2.getValue())
          return -1;
        else if (o1.getValue() < o2.getValue())
          return 1;
        return 0;
      }
    });
    return word_freqs_tmp;
  }

  public static Object top25_freqs(Object wf) {
    List<Map.Entry<String, Integer>> word_freqs_tmp = (ArrayList<Map.Entry<String, Integer>>) (wf);

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 25; i++) {
      sb.append(word_freqs_tmp.get(i).getKey()).append("  -  ").append(word_freqs_tmp.get(i).getValue())
      .append("\n");
    }
    return sb.toString();
  }

  public static void main(String[] args) {

    try {
      (new TFTheOne(Config.bookPath)).bind(TheOne.class.getMethod("read_file", Object.class))
      .bind(TheOne.class.getMethod("filter_chars", Object.class))
      .bind(TheOne.class.getMethod("normalize", Object.class))
      .bind(TheOne.class.getMethod("scan", Object.class))
      .bind(TheOne.class.getMethod("remove_stop_words", Object.class))
      .bind(TheOne.class.getMethod("frequencies", Object.class))
      .bind(TheOne.class.getMethod("sort", Object.class))
      .bind(TheOne.class.getMethod("top25_freqs", Object.class)).printme();
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
  }
}

class TFTheOne {
  Object value;

  public TFTheOne(Object value) {
    this.value = value;
  }

  public TFTheOne bind(Method func) {
    try {
      this.value = func.invoke(null, this.value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return this;
  }

  public void printme() {
    System.out.print(((String) this.value));
  }
}