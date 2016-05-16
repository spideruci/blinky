/*
@author Charles.Y.Feng
@date May 11, 2016 3:58:41 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>Style Requirement: Code Golf</h2> 
 * <p>	
 * The goal is to implement the program's functionality in as few lines of code as possible.</br> 
 * This is usually achieved by using advanced features of the programming language and </br>
 * its libraries. When brevity is the only goal, it is not unusual for this style to result </br>
 * in lines of code that are very long, with instruction sequences that are hard to understand. 
 * </p>
 * 
 * <p>
 * Often, too, textual brevity may result in programs that perform poorly or that have bugs, some </br>
 * of which only manifesting themselves when the code is used in larger or dierent contexts. </br>
 * Brevity, however, when used appropriately, may result in programs that are quite elegant and easy to </br>
 * read because they are small.
 * </p>
 */
public class CodeGolf {
	public static void main(String[] args) throws Exception {
		String stopWordsPath = Config.stopWordsPath;
		String bookPath = Config.bookPath;
		
		BufferedReader br = new BufferedReader(new FileReader(stopWordsPath));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		Set<String> stops = new HashSet<String>();
		stops.addAll(Arrays.asList(sb.toString().split(",")));

		br = new BufferedReader(new FileReader(bookPath));
		sb = new StringBuilder();
		int v = -1;
		while ((v = br.read()) != -1) {
			sb.append((char) (v));
		}

		List<String> words = new ArrayList<String>();
		Matcher matcher = Pattern.compile("(?i)([a-z]{2,200})").matcher(sb.toString().toLowerCase());
		while (matcher.find())
			words.add(matcher.group(1).trim());

		Hashtable<String, Integer> map = new Hashtable<String, Integer>();
		for (String word : words) {
			if (!stops.contains(word) && !map.containsKey(word)) {
				int freq = Collections.frequency(words, word);
				map.put(word, freq);
			}
		}

		List<Map.Entry<String, Integer>> counts = new ArrayList<Map.Entry<String, Integer>>();
		counts.addAll(map.entrySet());
		Collections.sort(counts, new Comparator<Map.Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
				if (o1.getValue() > o2.getValue())
					return -1;
				else if (o1.getValue() < o2.getValue())
					return 1;
				return 0;
			}
		});

		for (int i = 0; i < 25; i++)
			System.out.println(counts.get(i).getKey() + "  -  " + counts.get(i).getValue());

	}
}
