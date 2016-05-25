/*
@author Charles.Y.Feng
@date May 11, 2016 4:23:48 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * <h2>Style Requirement: Bulletin Board</h2> 
 * <p> 
 * The Bulletin Board style is often used with asynchronous components, but, </br>
 * as seen, here, that is not required. The infrastructure for handling events may </br>
 * be as simple as the one shown here or much more sophisticated, with several </br>
 * components interacting for the distribution of events. The infrastructure may </br>
 * also include more sophisticated event structures that support more detailed</br>
 * event filtering { for example, rather than simple subscriptions to event types, </br>
 * like shown here, components may subscribe to a combination of event types </br>
 * and contents.
 * </p><p>
 * Like the previous style, the Bulletin Board style supports inversion of control, </br>
 * but taken to its most extreme and minimal form { events generated by </br>
 * some components in the system may cause actions in other components of the</br>
 * system. The subscription is anonymous, so a component generating an event,</br>
 * in principle, doesn't know all the components that are going to handle that </br>
 * event. This style supports a very flexible entity composition mechanism (via events),</br>
 * but, like the previous style, in certain cases it may lead to systems whose erroneous</br>
 * behaviors are difficult to trace.
 * </p>
 */
public class BulletinBoard {
	public static void main(String[] args){
		EventManager em = new EventManager();
		new BulletinBoard_DataStorage(em); 
		new BulletinBoard_StopWordFilter(em); 
		new BulletinBoard_WordFrequencyCounter(em);
		new WordFrequencyApplication(em);
		String[] event = {"run",Config.bookPath};
		em.publish(event);
	}
}


class EventManager{
	HashMap<String,List<Method>> subscriptions;
	HashMap<Method,Object> objMap;
	
	public EventManager(){
		subscriptions = new HashMap<String,List<Method>>();
		objMap = new HashMap<Method,Object>();
	}
	
	public void subscribe(String event_type, Method handler, Object obj){
		if(subscriptions.containsKey(event_type)){
			subscriptions.get(event_type).add(handler);
		}else{
			List<Method> methods = new ArrayList<Method>();
			methods.add(handler);
			subscriptions.put(event_type, methods);
		}
		objMap.put(handler, obj);
	}
	
	public void publish(String[] event){
		String event_type = event[0];
		if(subscriptions.containsKey(event_type)){
			for(Method m : subscriptions.get(event_type)){
				try {
					m.invoke(objMap.get(m), (Object)(event));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class BulletinBoard_DataStorage{
	EventManager em;
	String data;
	
	public BulletinBoard_DataStorage(EventManager em){
		this.em = em;
		try {
			em.subscribe("load", this.getClass().getMethod("load", String[].class), this);
			em.subscribe("start", this.getClass().getMethod("produce_words", String[].class), this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
	}
	
	public void load(String[] event){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(event[1]));
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
	} 
	
	public void produce_words(String[] event){
		String[] data_strs = data.split(" ");
		for(String s : data_strs){
			String[] eventTmp = {"word",s};
			em.publish(eventTmp);
		}
		String[] eventTmp = {"eof",null};
		em.publish(eventTmp);
	}
}

class BulletinBoard_StopWordFilter{
	EventManager em;
	Set<String> stop_words;
	public BulletinBoard_StopWordFilter(EventManager em){
		stop_words = new HashSet<String>();
		this.em = em;
		try {
			this.em.subscribe("load", this.getClass().getMethod("load", String[].class), this);
			this.em.subscribe("word", this.getClass().getMethod("is_stop_word", String[].class),this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void load(String[] event){
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
	
	public void is_stop_word(String[] event){
		String word = event[1];
		if (!stop_words.contains(word)){
			String[] eventTmp = {"valid_word", word}; 
			em.publish(eventTmp);
		}
	}
}

class BulletinBoard_WordFrequencyCounter{
	EventManager em;
	
	HashMap<String,Integer> word_freqs;
	public BulletinBoard_WordFrequencyCounter(EventManager em){
		word_freqs = new HashMap<String,Integer>();
		this.em = em;
		try {
			em.subscribe("valid_word", this.getClass().getMethod("increment_count", String[].class),this);
			em.subscribe("print", this.getClass().getMethod("print_freqs", String[].class),this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void increment_count(String[] event){
		String word = event[1];
		if (word_freqs.containsKey(word)) {
			word_freqs.put(word, word_freqs.get(word) + 1);
		} else {
			word_freqs.put(word, 1);
		}
				
	}
	
	public void print_freqs(String[] event){
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
}

class WordFrequencyApplication{
	EventManager em;
	
	public WordFrequencyApplication(EventManager event_manager){
		this.em = event_manager;
		try {
			em.subscribe("run", this.getClass().getMethod("run", String[].class), this);
			em.subscribe("eof", this.getClass().getMethod("stop", String[].class), this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void run(String[] event){
		String path_to_file = event[1];
		String[] eventTmp = {"load",path_to_file};
		em.publish(eventTmp);
		String[] eventTmp2 = {"start",null};
		em.publish(eventTmp2);
	}
	
	public void stop(String[] event){
		String[] eventTmp = {"print",null};
		em.publish(eventTmp);
	}
}
