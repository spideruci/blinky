/*
@author Charles.Y.Feng
@date May 12, 2016 5:03:14 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.spideruci.analysis.diagnostics.subjects.styleEx.Config;


public class Words2 implements Words {
	@Override
	public ArrayList<String> extractWords(String pathToFile) throws IOException{
		List<String> word_list = new ArrayList<String>();
		String data = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pathToFile));
			StringBuilder sb = new StringBuilder();
			int v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) v);
			}
			data = sb.toString();
			data = data.replaceAll("([\\W_])+", " ").toLowerCase();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		word_list.addAll(Arrays.asList(data.split(" ")));
		
		HashSet<String> stopWords = new HashSet<String>();
		try {
			br = new BufferedReader(new FileReader(Config.stopWordsPath));
			StringBuilder sb = new StringBuilder();
			int v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) v);
			}
			stopWords.addAll(Arrays.asList(sb.toString().split(",")));
			for (char ch = 'a'; ch <= 'z'; ch++) {
				stopWords.add(ch + "");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> newWordsList = new ArrayList<String>();
		for(String word : word_list) {
			if(!stopWords.contains(word))
				newWordsList.add(word);
		}
		
		return newWordsList;
	}
}