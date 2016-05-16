/*
@author Charles.Y.Feng
@date May 12, 2016 3:31:03 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <h2>Style Requirement: Tantrum</h2> 
 * <p> 
 * This style is as defensive as the previous one: the same possible errors </br>
 * are being checked. But the way it reacts when abnormalities are detected </br>
 * is quite different: the functions simply refuse to continue.
 * </p>
 */
public class Tantrum {
	
	public Object extract_words(Object path_to_file_obj){
		assert path_to_file_obj instanceof String : "I need a string!";
		String path_to_file = (String)path_to_file_obj;
		assert !path_to_file.isEmpty() : "I need a non-empty string!";
		
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
			System.out.println(String.format("I/O error({0}) when opening {1}: {2}! I quit!",
					e.getMessage(), path_to_file, e.getCause()));
			e.printStackTrace();
		} 
		List<String> word_list = new ArrayList<String>();
		word_list.addAll(Arrays.asList(data.replaceAll("[\\W_]+"," ").toLowerCase().split(" ")));
		return word_list;
	}
	
	public Object remove_stop_words(Object word_list_obj){
		assert word_list_obj instanceof ArrayList : "I need a list!";
		ArrayList<String> word_list = (ArrayList<String>)word_list_obj;

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
		} catch (Exception e) {
			System.out.println(String.format("I/O error({0}) when opening ../stops_words.txt:{1}! I quit!",
					e.getMessage(), e.getCause()));
			e.printStackTrace();
		}
		return word_list;
	}
	
	public Object frequencies(Object word_list_obj){
		assert word_list_obj instanceof ArrayList : "I need a list!";
		List<String> word_list = (ArrayList<String>)word_list_obj;
		assert !word_list.isEmpty() : "I need a non-empty list!";
		
		if(word_list.isEmpty())
			return new HashMap<String,Integer>();
		
		HashMap<String,Integer> word_freqs = new HashMap<String,Integer>();
		for(String w:word_list){
			if (word_freqs.containsKey(w)) {
				word_freqs.put(w, word_freqs.get(w) + 1);
			} else {
				word_freqs.put(w, 1);
			}
		}
		return word_freqs;
	}
	
	public Object sort(Object word_freq_obj){
		assert word_freq_obj instanceof HashMap : "I need a HashMap!";
		HashMap<String,Integer> word_freq = (HashMap<String,Integer>)word_freq_obj;
		assert !word_freq.isEmpty() : "I need a non-empty HashMap!";
		List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
		try{
		word_freqs_tmp.addAll(word_freq.entrySet());
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
		} catch (Exception e) {
			System.out.println(String.format("Sorted threw {0}: {1}",
					e.getMessage(), e.getCause()));
			e.printStackTrace();
		}
		return word_freqs_tmp;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
	    try{
		assert args.length > 0: "I need an input file!";
		Tantrum t = new Tantrum();
		Object word_freqs_obj = t.sort(t.frequencies
				(t.remove_stop_words(t.extract_words((Object)Config.bookPath))));
		

		assert word_freqs_obj instanceof ArrayList :  "OMG! This is not a list!";
		List<Entry<String, Integer>> word_freqs = (List<Entry<String, Integer>>)word_freqs_obj;
		assert word_freqs.size() > 25 : "SRSLY? Less than 25 words!";
		for(int i=0;i<25;i++)
			System.out.println(word_freqs.get(i).getKey()+"  -  "+word_freqs.get(i).getValue());
	    }catch (Exception e) {
    		System.out.println(String.format("Something wrong: {0}",e));
		    e.printStackTrace();
	    }
	}
}

