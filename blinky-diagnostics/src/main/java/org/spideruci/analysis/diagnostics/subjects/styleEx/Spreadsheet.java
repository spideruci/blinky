/*
@author Charles.Y.Feng
@date May 12, 2016 3:44:41 PM
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>Style Requirement: Spreadsheet</h2> 
 * <p> Like the persistent table style, this style uses tabular data, but with </br> 
 * a different goal in mind. Rather than storing the data and querying it later, </br> 
 * the goal here is to emulate the good old spreadsheets that have been used in </br> 
 * accounting for hundreds of years. In accounting, the data is layed out in tables;</br> 
 * while some columns have primitive data, other columns have compound data that results </br> 
 * from some combination of other columns (totals, averages, etc.). Spreadsheets are used </br> 
 * by everyone these days; however, not many realize that their underlying programming model </br> 
 * is quite powerful, and a great example of the dataflow family of programming styles.
 * </p>
 */
public class Spreadsheet {
	Object[] all_words = new Object[2];
	Object[] stop_words = new Object[2];
	Object[] unique_words = new Object[2];
	Object[] non_stop_words = new Object[2];
	Object[] counts = new Object[2];
	Object[] sorted_data = new Object[2];

	static List<Object[]> all_columns = new ArrayList<Object[]>();

	public static void main(String[] args) {
		Spreadsheet ss = new Spreadsheet();
		
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
			ss.stop_words[0] = stop_words;
			
			String data;
			br = new BufferedReader(new FileReader(Config.bookPath));
			sb = new StringBuilder();
			v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) v);
			}
			data = sb.toString();
			List<String> words = new ArrayList<String>();
			Matcher matcher = Pattern.compile("(?i)([a-z]{2,200})").matcher(sb.toString().toLowerCase());
			while (matcher.find())
				words.add(matcher.group(1).trim());
			
			ss.all_words[0] = words;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ss.initializeDatas();
		ss.update();
		
		List<Map.Entry<String, Integer>> sorted_data = (List<Map.Entry<String, Integer>>)ss.sorted_data[0];
		for(int i=0;i<25;i++){
			System.out.println(sorted_data.get(i).getKey() + "  -  "
					+ sorted_data.get(i).getValue() + "");
		}
	}

	public void initializeDatas() {
		all_words[1] = null;
		stop_words[1] = null;

		non_stop_words[1] = new MyFunction() {
			@SuppressWarnings("unchecked")
			@Override
			public Object func() {
				List<String> non_stop_words_data = new ArrayList<String>();
				List<String> all_words_data = ((List<String>) all_words[0]);
				Set<String> stop_words_data = (Set<String>) stop_words[0];
				for (String word : all_words_data) {
					if (stop_words_data.contains(word)) {
						non_stop_words_data.add("");
					} else {
						non_stop_words_data.add(word);
					}
				}
				return non_stop_words_data;
			}
		};

		unique_words[1] = new MyFunction() {
			@SuppressWarnings("unchecked")
			@Override
			public Object func() {
				List<String> uniques_word_data = new ArrayList<String>();
				List<String> datas = (ArrayList<String>)non_stop_words[0];
				for(String s : datas)
					if(!s.isEmpty() && !uniques_word_data.contains(s))
						uniques_word_data.add(s);
				return uniques_word_data;
			}
		};
		
		counts[1] = new MyFunction() {
			@SuppressWarnings("unchecked")
			@Override
			public Object func() {
				List<Integer> counts = new ArrayList<Integer>();
				List<String> datas = (ArrayList<String>)non_stop_words[0];
				List<String> uniques_word_data = (ArrayList<String>)unique_words[0];
				for(String word:uniques_word_data){
					counts.add(Collections.frequency(datas, word));
				}
				return counts;
			}
		};
		
		sorted_data[1] = new MyFunction(){

			@Override
			public Object func() {
				List<Map.Entry<String, Integer>> freq = new ArrayList<Map.Entry<String, Integer>>();
				
				List<Integer> counts_data = (ArrayList<Integer>)counts[0];
				List<String> uniques_word_data = (ArrayList<String>)unique_words[0];
				HashMap<String,Integer> map = new HashMap<String,Integer>(); 
				for(int i=0;i<counts_data.size();i++){
					map.put(uniques_word_data.get(i), counts_data.get(i));
				}
				freq.addAll(map.entrySet());
				Collections.sort(freq, new Comparator<Map.Entry<?, Integer>>() {
					public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
						if (o1.getValue() > o2.getValue())
							return -1;
						else if (o1.getValue() < o2.getValue())
							return 1;
						return 0;
					}
				});
				
				return freq;
			}
			
		};

		all_columns.add(all_words);
		all_columns.add(stop_words);
		all_columns.add(non_stop_words);
		all_columns.add(unique_words);
		all_columns.add(counts);
		all_columns.add(sorted_data);
	}

	public void update() {
		for(Object[] data: all_columns){
			if(data[1] != null && data[1] instanceof MyFunction){
				data[0] = ((MyFunction)data[1]).func();
			}
		}
	}
}

interface MyFunction {
	Object func();
}
