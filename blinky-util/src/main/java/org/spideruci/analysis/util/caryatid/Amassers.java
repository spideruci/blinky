package org.spideruci.analysis.util.caryatid;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.HashMultimap;

public class Amassers {
  
  public static class AmassingArrayList<T> extends ArrayList<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public AmassingArrayList<T> addAndDump(T e) {
      this.add(e);
      return this;
    }
    
    @SuppressWarnings("unchecked")
    public AmassingArrayList<T> addAndDump(T ... es) {
      for(T e : es)
        this.add(e);
      return this;
    }
    
  }
  
  public static class AmassingHashMap<K, V> extends HashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public AmassingHashMap<K, V> putAndDump(K key, V value) {
      this.put(key, value);
      return this;
    }
    
  }
  
  public static class AmassingMultimap<K, V> {

    private HashMultimap<K, V> accumulated;

    public static <K, V> AmassingMultimap<K, V> to(HashMultimap<K, V> x) {
      return new AmassingMultimap<K, V>(x);
    }
    
    public static <K, V> AmassingMultimap<K, V> toNewMap() {
      return new AmassingMultimap<K, V>();
    }

    private AmassingMultimap(HashMultimap<K, V> x) {
      accumulated = x;
    }
    
    private AmassingMultimap() {
      accumulated = HashMultimap.create();
    }

    public HashMultimap<K, V> putAndDump(K key, V value) {
      accumulated.put(key, value);
      return accumulated;
    }
    
    @SuppressWarnings("unchecked")
    public HashMultimap<K, V> putAndDump(K key, V ... values) {
      for(V v : values) {
        accumulated.put(key, v);
      }
      return accumulated;
    }
    
    public HashMultimap<K, V> andDump() {
      return accumulated;
    }

    public AmassingMultimap<K, V> put(K key, V value) {
      this.accumulated.put(key, value);
      return this;
    }
    
    @SuppressWarnings("unchecked")
    public AmassingMultimap<K, V> put(K key, V ... values) {
      for(V v : values) {
        this.accumulated.put(key, v);
      }
      return this;
    }
    
  }
  
}
