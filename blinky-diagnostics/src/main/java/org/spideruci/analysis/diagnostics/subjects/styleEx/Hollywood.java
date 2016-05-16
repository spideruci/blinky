/*
@author Charles.Y.Feng
@date May 11, 2016 4:36:15 PM
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <h2>Style Requirement: Hollywood</h2> 
 * <p>
 * This style differs from the previous ones by its use of inversion of control: </br>
 * rather than an entity e1 calling another entity e2 with the purpose </br>
 * of getting some information, e1 registers with e2 for a callback; e2 then 
 * calls back e1 at a later time.
 * </p>
 * <p> 
 * The Hollywood style of programming seems rather contrived, but it has </br>
 * one interesting property: rather than hardwiring callers to callees at specific </br>
 * points of the program (i.e. function calls, where the binding is done by naming </br>
 * functions), it reverts that relation, allowing a callee to trigger actions in many </br>
 * callers at a time determined by the callee. This supports a different kind of </br>
 * module composition, as many modules can register handlers for the same event on a provider.
 * </p><p> 
 * This style is used in many object-oriented frameworks, as it is a powerful </br>
 * mechanism for the framework code to trigger actions in arbitrary application </br>
 * code. Inversion of control is precisely what makes frameworks different from </br>
 * regular libraries. The Hollywood style, however, should be used with care, </br>
 * as it may result in code that is extremely hard to understand. We will see </br>
 * variations of this style in subsequent chapters.
 * </p>
 */
public class Hollywood {
	public static void main(String[] args) {
		WordFrequencyFramework wfapp = new WordFrequencyFramework();
		StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
		DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
		WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(
				wfapp, data_storage);
		wfapp.run(Config.bookPath);
	}
}

class WordFrequencyFramework {
	List<Method> load_event_handlers;
	List<Method> dowork_event_handlers;
	List<Method> end_event_handlers;
	HashMap<Method, Object> objMap;
	
	public WordFrequencyFramework(){
		load_event_handlers = new ArrayList<Method>();
		dowork_event_handlers = new ArrayList<Method>();
		end_event_handlers = new ArrayList<Method>();
		objMap = new HashMap<Method, Object>();
	}

	public void register_for_load_event(Method handler, Object obj) {
		load_event_handlers.add(handler);
		objMap.put(handler, obj);
	}

	public void register_for_dowork_event(Method handler, Object obj) {
		dowork_event_handlers.add(handler);
		objMap.put(handler, obj);
	}

	public void register_for_end_event(Method handler, Object obj) {
		end_event_handlers.add(handler);
		objMap.put(handler, obj);
	}

	public void run(String path_to_file) {
		try {
			for (Method m : load_event_handlers) {
				m.invoke(objMap.get(m), path_to_file);
			}

			for (Method m : dowork_event_handlers) {
				m.invoke(objMap.get(m));
			}

			for (Method m : end_event_handlers) {
				m.invoke(objMap.get(m));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class DataStorage {
	StopWordFilter stop_word_filter;
	String data;
	List<Method> word_event_handlers;
	HashMap<Method, Object> objMap;

	public DataStorage(WordFrequencyFramework wfapp, StopWordFilter stop_word_filter) {
		this.stop_word_filter = stop_word_filter;
		word_event_handlers = new ArrayList<>();
		objMap = new HashMap<Method, Object>();
		try {
			wfapp.register_for_load_event(this.getClass().getMethod("load",String.class),this);
			wfapp.register_for_dowork_event(this.getClass().getMethod("produce_words"),this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void load(String path_to_file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path_to_file));
			StringBuilder sb = new StringBuilder();
			int v = -1;
			while ((v = br.read()) != -1) {
				sb.append((char) v);
			}
			data = sb.toString();
			data = data.replaceAll("([\\W_])+", " ").toLowerCase();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void produce_words() {
		String[] data_strs = data.split(" ");
		for (String s: data_strs){
			if(!stop_word_filter.is_stop_word(s)){
				for(Method m : word_event_handlers){
					try {
						m.invoke(objMap.get(m), s);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void register_for_word_event(Method m, Object obj) {
		word_event_handlers.add(m);
		objMap.put(m, obj);
	}
}

class StopWordFilter {

	Set<String> stop_words;

	public StopWordFilter(WordFrequencyFramework wfapp){
		try {
			wfapp.register_for_load_event(this.getClass().getMethod("load",String.class), this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		stop_words = new HashSet<String>();
	}
	
	public void load(String ignore) {
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean is_stop_word(String word) {
		return stop_words.contains(word);
	}
}

class WordFrequencyCounter {

	HashMap<String, Integer> word_freqs;
	
	public WordFrequencyCounter(WordFrequencyFramework wfapp,
			DataStorage data_storage) {
		word_freqs = new HashMap<String, Integer>();
		try {
			data_storage.register_for_word_event(
					this.getClass().getMethod("increment_count", String.class),
					this);
			wfapp.register_for_end_event(
					this.getClass().getMethod("print_freqs"), this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void print_freqs() {
		List<Map.Entry<String, Integer>> word_freqs_tmp = new ArrayList<Map.Entry<String, Integer>>();
		word_freqs_tmp.addAll(word_freqs.entrySet());
		Collections.sort(word_freqs_tmp,
				new Comparator<Map.Entry<?, Integer>>() {
					public int compare(Map.Entry<?, Integer> o1,
							Map.Entry<?, Integer> o2) {
						if (o1.getValue() > o2.getValue())
							return -1;
						else if (o1.getValue() < o2.getValue())
							return 1;
						return 0;
					}
				});

		for (int i = 0; i < 25; i++) {
			System.out.println(word_freqs_tmp.get(i).getKey() + "  -  "
					+ word_freqs_tmp.get(i).getValue() + "");
		}

	}

	public void increment_count(String word) {
		if (word_freqs.containsKey(word)) {
			word_freqs.put(word, word_freqs.get(word) + 1);
		} else {
			word_freqs.put(word, 1);
		}
	}
}
