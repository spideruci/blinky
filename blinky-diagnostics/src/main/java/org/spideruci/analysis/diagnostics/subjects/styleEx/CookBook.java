/*
@author Charles.Y.Feng
@date May 11, 2016 4:09:41 PM
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <h2>Style Requirement: Cook Book</h2> 
 * <p>	
 * In this style, the larger problem is subdivided into subunits, aka procedures, </br>
 * each doing one thing. It is common in this style for the procedures to share data </br>
 * among themselves, as a means to achieve the final goal. Furthermore, the state changes </br>
 * may depend on previous values of the variables. The procedures are said to have side effects</br>
 * on this data. The computation proceeds with one procedure processing some data in</br>
 * the pool and preparing data for the next procedure.
 * </p>
 */
public class CookBook {

	List<String> words;
	List<Character> data;
	ArrayList<Map.Entry<String, Integer>> word_freqs;

	public void read_file(String path_to_file) {
		if (!data.isEmpty()) {
			data.clear();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(path_to_file));
			int v = -1;
			while ((v = br.read()) != -1) {
				data.add((char) (v));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void filter_chars_and_normalize() {
		for (int i = 0; i < data.size(); i++) {
			if (!Character.isDigit(data.get(i))
					&& !Character.isLetter(data.get(i))) {
				data.set(i, ' ');
			} else {
				data.set(i, Character.toLowerCase(data.get(i)));
			}
		}
	}

	public void scan() {
		if (!words.isEmpty()) {
			words.clear();
		}
		StringBuilder sb = new StringBuilder();

		for (Character c : data) {
			sb.append(c);
		}
		
		for(String s : Arrays.asList(sb.toString().split(" ")))
			if(s.trim().length() > 0)
				words.add(s.trim());
	}

	public void remove_stop_words() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(Config.stopWordsPath));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String[] stopWords = sb.toString().split(",");
			HashSet<String> stopWordSet = new HashSet<String>();
			stopWordSet.addAll(Arrays.asList(stopWords));
			for (char ch = 'a'; ch <= 'z'; ch++) {
				stopWordSet.add(ch + "");
			}

			for (int i = 0; i < words.size(); i++) {
				if (stopWordSet.contains(words.get(i))) {
					words.remove(words.get(i));
					i--;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void frequencies() {
		if (!word_freqs.isEmpty()) {
			word_freqs.clear();
		}

		Hashtable<String, Integer> map = new Hashtable<String,Integer>();
		for (int i = 0; i < words.size(); i++) {
			if (map.keySet().contains(words.get(i))) {
				map.put(words.get(i), map.get(words.get(i)) + 1);
			} else {
				map.put(words.get(i), 1);
			}
		}
		word_freqs.addAll(map.entrySet());
	}

	public void sort() {

		Collections.sort(word_freqs, new Comparator<Map.Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> o1,
					Map.Entry<?, Integer> o2) {
				if (o1.getValue() > o2.getValue())
					return -1;
				else if (o1.getValue() < o2.getValue())
					return 1;
				return 0;
			}
		});
	}

	public CookBook() {
		word_freqs = new ArrayList<Map.Entry<String, Integer>>();
		words = new ArrayList<String>();
		data = new ArrayList<Character>();
	}

	public static void main(String[] args) {
		CookBook cb = new CookBook();
//		String bookPath = args[0];
		String bookPath = Config.bookPath;
		
		cb.read_file(bookPath);
		cb.filter_chars_and_normalize();
		cb.scan();
		cb.remove_stop_words();
		cb.frequencies();
		cb.sort();
		
		for (int i=0;i<25;i++)
			System.out.println(cb.word_freqs.get(i).getKey()+"  -  "+cb.word_freqs.get(i).getValue());
	}
}

