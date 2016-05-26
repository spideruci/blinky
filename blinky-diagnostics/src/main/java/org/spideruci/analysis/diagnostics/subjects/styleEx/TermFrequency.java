/*
@author Charles.Y.Feng
@date May 11, 2016 3:18:45 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * <h2>Style Requirement</h2> 
 * <p>	
 * Using your favorite programming language (other than Python), implement the term frequency program
 * that is the basis for the exercises in the book – see Prologue in the book. There is no need to follow any
 * specific style; just make it work as best as you can.
 * </p>
 */
public class TermFrequency {

  public static void main(String[] args) {


    HashMap<String, Term> termMap = new HashMap<String, Term>();
    List<String> stopSign = new ArrayList<String>();

    //		String input = args[0];
    String input = Config.bookPath;

    try {

      BufferedReader br = new BufferedReader(new FileReader(Config.stopWordsPath));
      String readline = "";
      while ((readline = br.readLine()) != null) {
        readline = readline.toLowerCase();
        stopSign.addAll(Arrays.asList(readline.split(",")));
      }
      br.close();

      br = new BufferedReader(new FileReader(input));
      readline = "";
      while ((readline = br.readLine()) != null) {
        String[] words = readline.toLowerCase().split("[^a-zA-Z]+");
        for (String word : words) {
          if (!stopSign.contains(word) && word.length()>1) {
            if (termMap.keySet().contains(word)) {
              termMap.get(word).frequency++;
            } else {
              Term t = new Term();
              t.term = word;
              t.frequency = 1;
              termMap.put(word, t);
            }

          }
        }
      }

      List<Term> frequentTermList = new ArrayList<Term>();
      frequentTermList.addAll(termMap.values());
      Term[] terms = frequentTermList.toArray(new Term[0]);
      Arrays.sort(terms);

      for (int i = 0; i < 25; i++) {
        System.out.println(terms[i].term + "  -  " + terms[i].frequency);
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

@SuppressWarnings("rawtypes")
class Term implements Comparable {
  protected int frequency;
  protected String term;

  @Override
  public int compareTo(Object o) {
    Term t = (Term) o;

    if (t.frequency > this.frequency)
      return 1;
    else if (t.frequency < this.frequency)
      return -1;
    else
      return 0;
  }

}
