/*
@author Charles.Y.Feng
@date May 11, 2016 5:14:21 PM
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
import java.util.regex.Pattern;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <h2>Style Requirement: Hollywood (Words with Z)</h2>
 * <p>
 * This class implements an additional task: after printing out the list of 25
 * top words, </br>
 * it should print out the number of non-stop words with the letter z.
 * Additional constraints:</br>
 * (i) no changes should be made to the existing classes; adding new classes and
 * more lines of </br>
 * code to the main function is allowed; (ii) files should be read only once for
 * both term-frequency </br>
 * and "words with z" tasks.
 * 
 */
public class HollywoodWordsWithZ {
	public static void main(String[] args) {
		WordFrequencyFramework wfapp = new WordFrequencyFramework();
		StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
		DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);

		WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(wfapp, data_storage);
		ZWordFilter zword_fileter = new ZWordFilter(wfapp, data_storage);
		wfapp.run(Config.bookPath);

	}
}

class ZWordFilter {

	int zstopwords_counter;

	public ZWordFilter(WordFrequencyFramework wfapp, DataStorage data_storage) {
		zstopwords_counter = 0;
		try {
			data_storage.register_for_word_event(this.getClass().getMethod("zword_increment", String.class), this);
			wfapp.register_for_end_event(this.getClass().getMethod("print_zstopword_freqs"), this);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

	}

	public void print_zstopword_freqs() {
		System.out.println(zstopwords_counter);
	}

	public void zword_increment(String word) {
		if (word.contains("z")) {
			zstopwords_counter++;
		}
	}
}
