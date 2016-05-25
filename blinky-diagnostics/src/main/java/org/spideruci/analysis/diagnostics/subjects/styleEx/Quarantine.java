/*
@author Charles.Y.Feng
@date May 12, 2016 3:18:48 PM
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
 * <h2>Style Requirement: Quarantine</h2> 
 * <p> This style makes use of another variation of function composition. Its </br>
 * contraints are very interesting, namely the first one: the core program </br>
 * cannot do IO. For our term-frequency task, that reads files and outputs a </br>
 * result on the screen, this constraint poses a puzzling problem: how can we do </br>
 * it if the functions can't read files such as the Pride and Prejudice text and </br>
 * can't print things on the screen? Indeed, how can one write programs these </br>
 * days that don't interact one way or another with the user, the file system, and </br>
 * the network while the program is executing?
 * </p>
 */
public class Quarantine {

  public static void main(String[] args) {

    args = new String[2];
    args[0] = Config.bookPath;
    args[1] = Config.stopWordsPath;

    TFQuarantine quarantine = (new TFQuarantine(new get_input(args)))
        .bind(new extract_words()).bind(new remove_stop_words())
        .bind(new frequencies()).bind(new sort())
        .bind(new top25());
    //.bind(new top25_freqs());
    quarantine.execute();
  }

  static class get_input implements MyFunction {
    String[] args;

    public get_input(String[] args) {
      this.args = args;
    }

    @Override
    public Object func(Object o) {
      return new get_input_core(args);
    }

    class get_input_core implements IOFunction {
      String[] args;

      public get_input_core(Object o) {
        args = (String[]) o;
      }

      @Override
      public Object ioFunc() {
        return args[0];
      }
    }
  }

  static class extract_words implements MyFunction {

    @Override
    public Object func(Object o) {
      return new extract_words_core(o);
    }

    class extract_words_core implements IOFunction {
      String path_to_file;

      public extract_words_core(Object o) {
        path_to_file = (String) o;
      }

      @Override
      public Object ioFunc() {
        String data = null;
        BufferedReader br;
        try {
          br = new BufferedReader(new FileReader(path_to_file));
          StringBuilder sb = new StringBuilder();
          int v = -1;
          while ((v = br.read()) != -1) {
            sb.append((char) v);
          }
          br.close();
          data = sb.toString();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        List<String> word_list = new ArrayList<String>();
        word_list.addAll(Arrays.asList(data.replaceAll("[\\W_]+", " ")
            .toLowerCase().split(" ")));
        return word_list;
      }
    }
  }

  static class remove_stop_words implements MyFunction {

    @Override
    public Object func(Object o) {
      return new remove_stop_words_core(o);
    }

    class remove_stop_words_core implements IOFunction {

      List<String> word_list;

      public remove_stop_words_core(Object o) {
        word_list = (List<String>) o;
      }

      @Override
      public Object ioFunc() {
        Set<String> stop_words = new HashSet<String>();
        BufferedReader br;
        try {
          br = new BufferedReader(
              new FileReader(Config.stopWordsPath));
          StringBuilder sb = new StringBuilder();
          int v = -1;
          while ((v = br.read()) != -1) {
            sb.append((char) v);
          }
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
          br.close();
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return word_list;
      }
    }
  }

  static class frequencies implements MyFunction {
    @Override
    public Object func(Object o) {
      ArrayList<String> word_list_tmp = (ArrayList<String>) o;
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
  }

  static class sort implements MyFunction {

    @Override
    public Object func(Object o) {
      List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
      word_freqs_tmp.addAll(((HashMap<String, Integer>) o).entrySet());
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
  }

  static class top25_freqs implements MyFunction {
    @Override
    public Object func(Object o) {
      // TODO Auto-generated method stub
      List<Map.Entry<String, Integer>> word_freqs_tmp = (ArrayList<Map.Entry<String, Integer>>) (o);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 25; i++) {
        sb.append(word_freqs_tmp.get(i).getKey()).append("  -  ")
        .append(word_freqs_tmp.get(i).getValue()).append("\n");
      }
      return sb.toString();
    }
  }

  static class top25 implements MyFunction {
    @Override
    public Object func(Object o) {
      return new top25_core(o);
    }

    class top25_core implements IOFunction {
      List<Map.Entry<String, Integer>> word_freqs;

      public top25_core(Object o) {
        word_freqs = (ArrayList<Map.Entry<String, Integer>>) (o);
      }
      @Override
      public Object ioFunc() {
        for(int i=0;i<25;i++)
          System.out.println(word_freqs.get(i).getKey()+"  -  "+word_freqs.get(i).getValue());
        return "";
      }
    }
  }

  interface MyFunction {
    Object func(Object o);
  }

  interface IOFunction {
    Object ioFunc();
  }

  static class TFQuarantine {
    private List<MyFunction> funcs;

    public TFQuarantine(MyFunction func) {
      funcs = new ArrayList<MyFunction>();
      funcs.add(func);
    }

    public TFQuarantine bind(MyFunction e) {
      funcs.add(e);
      return this;
    }

    public void execute() {
      Object value = null;
      try {
        for (MyFunction m : funcs) {
          value = m.func(guard_callable(value));
        }
        System.out.print(guard_callable(value));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static Object guard_callable(Object v) throws Exception {
      if (v instanceof IOFunction) {
        return ((IOFunction) v).ioFunc();
      }
      return v;
    }
  }
}

