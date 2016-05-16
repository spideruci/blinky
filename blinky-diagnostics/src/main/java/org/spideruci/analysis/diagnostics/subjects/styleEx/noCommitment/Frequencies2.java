/*
@author Charles.Y.Feng
@date May 12, 2016 5:03:44 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

class Pair{
	String word;
	int count;
	public Pair(String w, int c){
		word = w;
		count = c;
	}
}

public class Frequencies2 implements Frequencies {
	@Override
	public ArrayList<Pair> top25(ArrayList<String> words){
		Set<String> uniqueWords = new HashSet<String>(words);
		ArrayList<Pair> freqs = new ArrayList<Pair>();
		for (String w : uniqueWords) {
			Pair cur = new Pair(w, Collections.frequency(words, w));
			freqs.add(cur);
		}
		
		Comparator<Pair> pairComparator = new Comparator<Pair>(){
			@Override
			public int compare(Pair p1, Pair p2){
				return p2.count - p1.count;
			}
		};
		
		Collections.sort(freqs, pairComparator);
		ArrayList<Pair> tops = new ArrayList<Pair>();
		for(int i = 0; i < 25; i++){
			tops.add(freqs.get(i));
		}
		return tops;
	}
}
