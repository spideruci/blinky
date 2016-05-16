/*
@author Charles.Y.Feng
@date May 11, 2016 5:20:44 PM
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
 * <h2>Style Requirement: Kick Your Teammate Forward</h2> 
 * <p> In this style, functions take one additional parameter -- a function </br>
 * -- that is meant to be called at the very end, and passed what normally would be </br>
 * the return value of the current function. This makes it so that the functions  </br>
 * don't [need to] return to their callers, and instead continue to some  </br>
 * other function, their teammate function.
 * </p>
 * <p> 
 * This style is known in some circles as continuation-passing style, and it is </br>
 * often used with anonymous functions (aka lambdas) as continuations, rather </br>
 * than with named functions. The example here uses named functions for readability.
 * </p>
 */
public class KickYourTeammateForward {
	public void read_file(String path_to_file, Method func){
		
		String data;
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
			func.invoke(this, data, this.getClass().getMethod("normalize", String.class, Method.class));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void filter_chars(String str_data, Method func){
		try {
			func.invoke(this, str_data.replaceAll("[\\W_]+"," "), 
					this.getClass().getMethod("scan", String.class, Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void normalize(String str_data, Method func){
		try {
			func.invoke(this, str_data.toLowerCase(), 
					this.getClass().getMethod("remove_stop_words", List.class, Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void scan(String str_data, Method func){
		try {
			List<String> word_list = new ArrayList<String>();
			word_list.addAll(Arrays.asList(str_data.split(" ")));
			func.invoke(this,word_list, 
					this.getClass().getMethod("frequencies", List.class, Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void remove_stop_words(List<String> word_list, Method func){
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
			
			for (int i = 0; i < word_list.size(); i++) {
				if (stop_words.contains(word_list.get(i))) {
					word_list.remove(word_list.get(i));
					i--;
				}
			}
			br.close();
			func.invoke(this, word_list, 
					this.getClass().getMethod("sort", HashMap.class, Method.class));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void frequencies(List<String> word_list, Method func){
		HashMap<String,Integer> wf = new HashMap<String,Integer>();
		for(String w:word_list){
			if (wf.containsKey(w)) {
				wf.put(w, wf.get(w) + 1);
			} else {
				wf.put(w, 1);
			}
		}
		try {
			func.invoke(this, wf, this.getClass().getMethod("print_text", List.class, Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void sort(HashMap<String,Integer> wf, Method func){
		List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
		word_freqs_tmp.addAll(wf.entrySet());
		Collections.sort(word_freqs_tmp,new Comparator<Map.Entry<?, Integer>>() {
					public int compare(Map.Entry<?, Integer> o1,
							Map.Entry<?, Integer> o2) {
						if (o1.getValue() > o2.getValue())
							return -1;
						else if (o1.getValue() < o2.getValue())
							return 1;
						return 0;
					}
				});
		try {
			func.invoke(this, word_freqs_tmp, this.getClass().getMethod("no_op", Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void print_text(List<Map.Entry<String,Integer>> word_freqs, Method func){
		for(int i =0 ;i<25;i++){
			System.out.println(word_freqs.get(i).getKey()+"  -  "+word_freqs.get(i).getValue());
		}
		try {
			func.invoke(this,this.getClass().getMethod("no_op", Method.class));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void no_op(Method func){
		return;
	}
	
	public static void main(String[] args){
		KickYourTeammateForward kytf = new KickYourTeammateForward();
		try {
			kytf.read_file(Config.bookPath, kytf.getClass().getMethod("filter_chars", String.class, Method.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}