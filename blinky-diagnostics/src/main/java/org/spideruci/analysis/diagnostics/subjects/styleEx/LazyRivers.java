/*
@author Charles.Y.Feng
@date May 12, 2016 3:35:40 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


/**
 * <h2>Style Requirement: LazyRivers</h2> 
 * <p> This style focuses on the problem of processing data that comes into </br> 
 * the application continuously and may not even have an end. The same issues </br> 
 * are seen in processing data whose size is known, but larger than the available </br> 
 * memory. The style establishes a ow of data from upstream (data sources) to downstream (data sinks), </br> 
 * with processing units along the way. The data is let to ow through the stream only when </br> 
 * the sinks need it. At any point in time, the only data present in the stream is </br> 
 * the one needed to produce whatever piece of data the sinks need, therefore </br>  
 * avoiding the problems raised by too much data at a time.
 * </p><p>
 * The Lazy River style is nicely expressed when programming languages support generators.</br> 
 * Some programming languages, e.g. Java, don't support generators; the Lazy Rivers style </br> 
 * can be implemented using iterators in Java, but the code will look ugly. When the programming</br> 
 * language doesn't support generators or iterators, it is still possible to support the goals </br> 
 * of this style, but the expression of that intent is considerably more complicated. In the absence </br> 
 * of generators and iterators, the next best mechanism for implementing the constraints underlying this </br>  
 * style is with threads. The style presented in the next chapter, Free Agents, would be a good t for this data-centric style.
 * </p>
 */
public class LazyRivers {
  public static void main(String[] args) {
    @SuppressWarnings("rawtypes")
    Stream word_freq_stream = null;
    try {
      word_freq_stream = count_and_sort(Config.bookPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Iterator<List<Map.Entry<String, Integer>>> word_freqs = word_freq_stream.iterator();
    List<Map.Entry<String, Integer>> freq = word_freqs.next();
    while(freq!=null){
      System.out.println("--------------------------------");
      for (int i = 0; i < 25; i++) {
        Map.Entry<String, Integer> entry = freq.get(i);
        System.out.println(entry.getKey() + "  -  "
            + entry.getValue());
      }
      freq = word_freqs.next();
    }
  }

  public static Stream<String> lines(String filename)
      throws NoSuchAlgorithmException, Exception {

    return Stream.generate(new Supplier<String>(){
      BufferedReader br = new BufferedReader(new FileReader(filename));
      @Override
      public String get() {
        try {
          return br.readLine();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }


  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Stream all_words(String filename) throws Exception {
    return Stream.generate(new Supplier(){
      Stream<String> lineStream = lines(filename);
      Iterator<String> iter = lineStream.sequential().iterator();
      String word = "";
      String line = iter.next();
      List<String> words_storage = new ArrayList<String>();
      @Override
      public Object get() {
        if(words_storage.isEmpty()){
          line = iter.next();
          if(line!=null){
            Matcher matcher = Pattern.compile("(?i)([a-z]{2,200})").matcher(line.toLowerCase());
            while (matcher.find())
              words_storage.add(matcher.group(1).trim());
            if(words_storage.isEmpty())
              words_storage.add("");
          }else{
            return null;
          }
        }
        word = words_storage.get(0);
        words_storage.remove(word);
        return word;
      }
    });
  }


  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Stream non_stop_words(String filename) throws Exception {
    Set<String> stop_words = new HashSet<String>();
    BufferedReader br;
    br = new BufferedReader(new FileReader(
        Config.stopWordsPath));
    StringBuilder sb = new StringBuilder();
    int v = -1;
    while ((v = br.read()) != -1) {
      sb.append((char) v);
    }
    stop_words.addAll(Arrays.asList(sb.toString().split(",")));
    for (char ch = 'a'; ch <= 'z'; ch++) {
      stop_words.add(ch + "");
    }
    br.close();
    Iterator<String> all_words_iter = all_words(filename).iterator();
    return Stream.generate(new Supplier(){
      @Override
      public Object get() {
        String word = all_words_iter.next();
        if(word != null){
          if (!stop_words.contains(word))
            return word;
          return "";
        }
        else 
          return null;
      }
    });
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Stream count_and_sort(String filename) throws Exception {
    HashMap<String, Integer> freqs = new HashMap<String, Integer>();
    Stream non_stop_words_stream = non_stop_words(filename);
    Iterator<String> non_stop_words = non_stop_words_stream.iterator();
    return Stream.generate(new Supplier(){
      boolean stop_flag=false;
      int i=1;
      String word=non_stop_words.next();
      @Override
      public Object get() {
        while((i++%5000!=0) && (word!=null)){

          if (freqs.containsKey(word)) {
            freqs.put(word, freqs.get(word) + 1);
          } else {
            if(!word.isEmpty()){
              freqs.put(word, 1);
            }
          }
          word = non_stop_words.next();
        }
        if(stop_flag)
          return null;

        List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
        word_freqs_tmp.addAll(freqs.entrySet());
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
        if(word==null){
          stop_flag=true;
        }
        return word_freqs_tmp;
      }
    });
  }
}

