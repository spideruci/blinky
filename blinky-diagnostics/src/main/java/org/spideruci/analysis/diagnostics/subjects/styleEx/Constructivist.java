/*
@author Charles.Y.Feng
@date May 11, 2016 5:37:45 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * <h2>Style Requirement: Constructivist</h2> 
 * <p> In this style, programs are mindful of possible abnormalities, they </br>
 * don't ignore them, but they take a constructivist approach to the problem: </br>
 * they incorporate practical heuristics in order x the problems in the service of </br>
 * getting the job done. They defend the code against possible errors of callers </br>
 * and providers by using reasonable fallback values whenever possible so that</br>
 * the program can continue.
 * </p>
 */
public class Constructivist {
	
	public Object extract_words(Object path_to_file_obj){
		if(path_to_file_obj == null || !(path_to_file_obj instanceof String))
			return new ArrayList<String>();
		String path_to_file = (String)path_to_file_obj;
		
		if(path_to_file.isEmpty())
			return new ArrayList<String>();
		
		String str_data;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path_to_file));
			StringBuilder sb = new StringBuilder();
			int v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) v);
			}
			br.close();
			str_data = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} 
		
		List<String> word_list = new ArrayList<String>();
		word_list.addAll(Arrays.asList(str_data.replaceAll("[\\W_]+"," ").toLowerCase().split(" ")));
		return word_list;
	}
	
	public Object remove_stop_words(Object word_list_obj){
		if(word_list_obj==null || !(word_list_obj instanceof List)){
			return new ArrayList<String>();
		}
		
		List<String> word_list = (ArrayList<String>)word_list_obj;
		if(word_list.isEmpty())
			return new ArrayList<String>();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			return word_list;
		}
	}
	
	public Object frequencies(Object word_list_obj){
		if(word_list_obj==null || !(word_list_obj instanceof ArrayList)){
			return new HashMap<String,Integer>();
		}
		List<String> word_list = (ArrayList<String>)word_list_obj;
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
		if(!(word_freq_obj instanceof HashMap)){
			return new ArrayList<Map.Entry<String, Integer>>();
		}
		
		HashMap<String,Integer> word_freq = (HashMap<String,Integer>)word_freq_obj;
		
		if(word_freq == null || word_freq.isEmpty())
			return new ArrayList<Map.Entry<String, Integer>>();
		
		List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
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
		return word_freqs_tmp;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		String filename = args.length>0?args[0]:Config.bookPath;
		Constructivist con = new Constructivist();
		
		List<Map.Entry<String, Integer>> word_freqs = 
				(List<Map.Entry<String, Integer>>)con.
				sort(con.frequencies(con.
						remove_stop_words(con.extract_words((Object)filename))));
		
		for(int i=0;i<25;i++)
			System.out.println(word_freqs.get(i).getKey()+"  -  "+word_freqs.get(i).getValue());
	}
}
