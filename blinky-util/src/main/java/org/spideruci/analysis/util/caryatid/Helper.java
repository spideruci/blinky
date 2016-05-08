package org.spideruci.analysis.util.caryatid;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;

import java.util.Random;
import java.util.Stack;


public class Helper {

  public static int s2i(final String str) {
    try {
      return Integer.parseInt(str);
    } catch(NumberFormatException ex) {
      System.out.println("Invalid number:" + str);
      throw ex;
    }
    
  }

  public static String i2s(final int i) {
    return String.valueOf(i);
  }

  public static String joinStrings(String seperator, Object ... strings) {
    int length = strings.length;
    StringBuffer buffer = new StringBuffer();
    if(length == 0) {
      return "";
    }

    buffer.append(strings[0] == null? null : strings[0].toString());
    for(int i = 1; i<length; i++) {
      buffer.append(seperator);
      buffer.append(strings[i] == null? null : strings[i].toString());
    }
    return buffer.toString();
  }

  public static <T> T checkEquals(T object, T against) {
    return checkEquals(object, against);
  }

  public static <T> T checkEquals(T object, T against, String errorMessage) {
    if(!object.equals(against)) {
      throw new RuntimeException(errorMessage);
    }
    return object;
  }

  public static int getArgCount(String methodName, boolean isStatic) {
    final String desc = extractDescFromMethodIdent(methodName);
    return argInitialsFromDescInternal(desc, isStatic).length;
  }

  public static char[] getArgInitials(String methodName, boolean isStatic) {
    final String desc = extractDescFromMethodIdent(methodName);
    return argInitialsFromDescInternal(desc, isStatic);
  }
  
  public static char[] getArgInitials2(String methodName, boolean isStatic) {
    String[] types = getArgTypes(methodName, "spider", isStatic);
    char[]  arginitials = new char[types.length];
    
    for(int i = 0; i < arginitials.length; i += 1) {
      arginitials[i] = types[i].charAt(0);
    }
    
    return arginitials;
  }
  
  private static char[] argInitialsFromDescInternal(String desc, boolean isStatic) {
    String x1 = desc.replace("[", "");
    String x2 = x1.replace(";", "; ");
    String x3 = x2.replaceAll("L\\S+;", "L");
    String x4 = x3.replace(" ", "");
    if(!isStatic) {
      return ("L" + x4).toCharArray();
    }
    return x4.toCharArray();
  }
  
  public static String[] getArgTypes(String methodName, String methodOwner, boolean isStatic) {
    final String[] temp = Helper.getArgTypeSplit(methodName);
    if(isStatic) {
      return temp;
    } else {
      final String[] argTypes = new String[temp.length + 1];
      argTypes[0] = "L" + methodOwner + ";";
      for(int i = 0; i < temp.length; i += 1) {
        argTypes[i+1] = temp[i];
      }
      return argTypes;
    }
  }
  
  private static String extractDescFromMethodIdent(String methodName) {
    int beginIndex = methodName.indexOf('(') + 1;
    int endIndex = methodName.lastIndexOf(')');
    String desc = methodName.substring(beginIndex, endIndex);
    return desc;
  }

  public static String[] getArgTypeSplit(String desc) {
    if(desc.contains("(") && desc.contains(")")) {
      desc = extractDescFromMethodIdent(desc);
    }
    
    int argCount = argInitialsFromDescInternal(desc, true).length;
    String[] argTypeSplit = new String[argCount];

    int count = 0;
    StringBuilder arrayBuilder = new StringBuilder();
    char[] chs = desc.toCharArray();
    for(int i = 0; i < chs.length; i += 1) {
      char ch = chs[i];
      if(ch == '[') {
        arrayBuilder.append(ch);
        continue;
      }
      
      if(ch == 'L') {
        StringBuffer type = new StringBuffer();
        type.append(ch);
        while(ch != ';') {
          ch = chs[++i];
          type.append(ch);
        }
        argTypeSplit[count] = type.toString();
      } else {
        argTypeSplit[count] = String.valueOf(ch);
      }
      
      int arrayBuilderLength = arrayBuilder.length(); 
      if(arrayBuilderLength != 0) {
        argTypeSplit[count] = arrayBuilder.toString() + argTypeSplit[count];
        arrayBuilder.delete(0, arrayBuilderLength);
      }

      count += 1;
    }

    return argTypeSplit;
  }

  public static boolean isArgPrimitive(char argInitial) {
    switch(argInitial) {
    case 'B': case 'b':
    case 'C': case 'c':
    case 'D': case 'd':
    case 'F': case 'f':
    case 'I': case 'i':
    case 'J': case 'j':
    case 'S': case 's':
    case 'Z': case 'z':
      return true;
    default:
      return false;
    }
  }

  public static void grep(String exact, File file, File output) {

    String grep_cmd = 
        "grep -m 1" + exact.replaceAll("\\$", "\\$") 
        + " " + file.getAbsolutePath() + " > " + output.getAbsolutePath();
    try {
      Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", grep_cmd});
      System.out.println(p.waitFor());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static class Collections2 {
    @SuppressWarnings("rawtypes")
    static private ArrayList emptyArrayList = new ArrayList();
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> emptyArrayList() {
      return emptyArrayList;
    }

    public static <T> LinkedHashSet<T> emptyLinkedHashSet() {
      return (LinkedHashSet<T>) Collections.<T>emptySet();
    }

    public static <K, V> HashMap<K, V> emptyHashMap() {
      return (HashMap<K, V>) Collections.<K, V>emptyMap();
    }

    public static <K> ArrayList<K> cloneArrayList(ArrayList<K> list) {
      ArrayList<K> nList = new ArrayList<>();
      for(K item : list) {
        nList.add(item);
      }
      return nList;
    }

    public static <U extends Number> 
    String stringify(Iterable<U> set, String seperator) {
      StringBuffer buffer = new StringBuffer();
      for(U element: set) {
        buffer.append(element).append(seperator);
      }
      return buffer.toString();
    }

    public static String stringify2(Iterable<String> set, String seperator) {
      StringBuffer buffer = new StringBuffer();
      for(String element: set) {
        buffer.append(element).append(seperator);
      }
      return buffer.toString();
    }

    public static <T> String stringify3(Iterable<T> set, String seperator) {
      StringBuffer buffer = new StringBuffer();
      for(T element: set) {
        buffer.append(element.toString()).append(seperator);
      }
      return buffer.toString();
    }

    public static <T> ArrayList<T> pickRandomFraction(ArrayList<T> all, int frac) {
      ArrayList<T> summaries = new ArrayList<>();
      int allcount = all.size();
      float fractionCount = (frac/100.0f) * allcount;
      if(fractionCount < 0.0f) {
        return summaries;
      }

      int randomSampleSize = (int) Math.floor(fractionCount);

      long seed = System.nanoTime();
      Collections.shuffle(all, new Random(seed));
      Collections.shuffle(all, new Random(seed));

      for(int i = 0; i < randomSampleSize; i += 1) {
        summaries.add(all.get(i));
      }

      return summaries;
    }

    public static <T extends Number> double sum(Collection<T> collection) {
      double sum = 0;

      for(T number : collection) {
        if(number == null) continue;
        sum += number.doubleValue();
      }

      return sum;
    }

    public static <T extends Number> double mean(Collection<T> collection) {
      double sum = 0;
      int count = 0;

      for(T number : collection) {
        if(number == null) continue;
        sum += number.doubleValue();
        count += 1;
      }
      double average = sum / count; 
      return average;
    }

    public static <K, V> String toString(Entry<K, V> entry) {
      K key = checkNotNull(entry).getKey();
      V value = entry.getValue();

      String toString = checkNotNull(key).toString() + " " + checkNotNull(value).toString();

      return toString;
    }

  }

  public static class Logger {
    private static boolean isLogging = false;
    public static void log(String logMessage, PrintStream out) {
      if(!isLogging) return;
      out.append(logMessage);
    }
  }

  public static int checkIfStringIsInteger(String s) {
    return Integer.parseInt(s);
  }
}