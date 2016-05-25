/*
@author Charles.Y.Feng
@date May 11, 2016 5:17:15 PM
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>Style Requirement: Infinite Mirror</h2> 
 * <p> 
 * This style encourages problem solving by induction. An inductive </br>
 * solution is one where a general goal is achieved in two steps: (1) solving </br>
 * one or more base cases, and (2) providing a solution that, if it works for the</br>
 * Nth case, it also works for the Nth+1 case. In computing, inductive solutions </br>
 * are usually expressed via recursion.
 * </p>
 */
public class InfiniteMirror {
	public void count(List<String> word_list, HashSet<String> stopwords, HashMap<String,Integer>wordfreqs){
		if(word_list==null || word_list.isEmpty())
			return;
		else if(word_list.size()==1){
			String word = word_list.get(0);
			if(!stopwords.contains(word)){
				if (wordfreqs.containsKey(word)) {
					wordfreqs.put(word, wordfreqs.get(word) + 1);
				} else {
					wordfreqs.put(word, 1);
				}
			}
		}else{
			count(word_list.subList(0, 1),stopwords,wordfreqs);
			count(word_list.subList(1, word_list.size()),stopwords,wordfreqs);
		}
	}
	
	public void wf_print(HashMap<String,Integer> wordfreqs){
		if(wordfreqs==null||wordfreqs.isEmpty())
			return;
		else if(wordfreqs.size()==1){
			List<String> keys = new ArrayList<String>(wordfreqs.keySet());
			System.out.println(keys.get(0)+"  -  "+wordfreqs.get(keys.get(0)));
		}else{
			List<String> keys = new ArrayList<String>(wordfreqs.keySet());
			
			HashMap<String,Integer> wordFreqsTmp = new HashMap<String,Integer>();
			wordFreqsTmp.put(keys.get(0), wordfreqs.get(keys.get(0)));
			wordfreqs.remove(keys.get(0));
			wf_print(wordFreqsTmp);
			wf_print(wordfreqs);
		}
		
	}
	
	public static void main(String[] args){
		int RECURSION_LIMIT = 1000;
		InfiniteMirror im = new InfiniteMirror();
		HashSet<String> stop_words = new HashSet<String>();
		List<String> words = new ArrayList<String>();
		HashMap<String,Integer> word_freqs = new HashMap<String,Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					Config.stopWordsPath));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String[] stopWords = sb.toString().split(",");
			stop_words.addAll(Arrays.asList(stopWords));
			
			br = new BufferedReader(new FileReader(Config.bookPath));
			sb = new StringBuilder();
			int v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) (v));
			}
			Matcher matcher = Pattern.compile("(?i)([a-z]{2,200})").matcher(sb.toString().toLowerCase());
			while (matcher.find())
				words.add(matcher.group(1).trim());
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<words.size();i+=RECURSION_LIMIT){
			im.count(words.subList(i, i+RECURSION_LIMIT>words.size()?words.size():i+RECURSION_LIMIT),stop_words, word_freqs);
		}
		
		List<Map.Entry<String, Integer>> counts = new ArrayList<Map.Entry<String, Integer>>();
		counts.addAll(word_freqs.entrySet());
		Collections.sort(counts, new Comparator<Map.Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
				if (o1.getValue() > o2.getValue())
					return -1;
				else if (o1.getValue() < o2.getValue())
					return 1;
				return 0;
			}
		});

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for(int i=0;i<25;i++)
			sortedMap.put(counts.get(i).getKey(), counts.get(i).getValue());
		im.wf_print(sortedMap);
	}
}
