/*
@author Charles.Y.Feng
@date May 12, 2016 2:31:36 PM
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
import java.util.Set;
import java.util.Map.Entry;


/**
 * <h2>Style Requirement: Declared Intentions</h2> 
 * <p> 
 * There is a category of programming abnormalities that, from very </br>
 * early on in the history of computers, has been known to be problematic: </br>
 * type mismatches. That is, a function expects an argument of a certain </br>
 * type, but is given a value of another type; or a function returns a value of a </br>
 * certain type which is then used by the caller of the function as if it were a </br>
 * value of another type. This is problematic because values of different types </br>
 * usually have different memory sizes, which means that when type mismatches </br>
 * occur, memory can be overwritten and made inconsistent.
 * </p><p>
 * Luckily, these abnormalities are relatively easy to deal with -- at least in </br>
 * comparison to all other abnormalities that can happen during program execution </br>
 * -- and the issue has been largely solved for quite some time in mainstream </br>
 * programming languages by means of type systems. All modern programming </br>
 * languages have a type system1, and data types are checked in various points </br>
 * of program development and execution.
 * </p>
 * <p> 
 * What is the difference between the Declared Intentions style and the previous </br>
 * two sytles? The Declared Intentions style applies only to one category of  </br>
 * abnormalities: type mismatches. The previous two example programs check for  </br>
 * more than just types; for example, they check whether the given arguments are  </br>
 * empty or have certain sizes. Those kinds of conditions are cumbersome, although  </br>
 * not impossible, to express in terms of types.
 * </p>
 */
public class DeclaredIntentions {
	public static void main(String[] args) {

		try {
			Object wordListObj = extract_words((Object)Config.bookPath);
			if(!(wordListObj instanceof List)){
				throw new Exception(String.format("Expecting %s got %s", "List", wordListObj));
			}
			List<String> word_list = (ArrayList<String>)wordListObj;
			
			Object wordFreqObj = frequencies(wordListObj);
			if(!(wordFreqObj instanceof HashMap)){
				throw new Exception(String.format("Expecting %s got %s", "HashMap", wordFreqObj));
			}
			HashMap<String,Integer> word_freqs = (HashMap<String,Integer>)wordFreqObj;
			
			Object wordFreqResObj = sort(word_freqs);
			if(!(wordFreqResObj instanceof List)){
				throw new Exception(String.format("Expecting %s got %s", "List", wordFreqResObj));
			}
			List<Entry<String, Integer>> word_freq_res = (List<Entry<String, Integer>>)wordFreqResObj;
			for(int i=0;i<25;i++)
				System.out.println(word_freq_res.get(i).getKey()+"  -  "+word_freq_res.get(i).getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static Object extract_words(Object path_to_file_obj) throws Exception {
		if (!(path_to_file_obj instanceof String))
			throw new Exception(String.format("Expecting %s got %s", "String", path_to_file_obj));
		
		String path_to_file = (String)path_to_file_obj;
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
		
		Set<String> stop_words = new HashSet<String>();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return word_list;
	}

	public static Object frequencies(Object word_list_obj) throws Exception {
		
		if (!(word_list_obj instanceof List))
			throw new Exception(String.format("Expecting %s got %s", "List", word_list_obj));
		
		List<String> word_list = (List<String>)word_list_obj;
		
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

	public static Object sort(Object word_freq_obj) throws Exception {
		
		if (!(word_freq_obj instanceof HashMap))
			throw new Exception(String.format("Expecting %s got %s", "HashMap", word_freq_obj));
		
		HashMap<String,Integer> word_freq = (HashMap<String,Integer>)word_freq_obj;
		
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
			e.printStackTrace();
		}
		return word_freqs_tmp;
	}
}
