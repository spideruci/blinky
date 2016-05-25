/*
@author Charles.Y.Feng
@date May 11, 2016 4:30:21 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;


/**
 * <h2>Style Requirement: Bulletin Board (Words with Z)</h2> 
 * <p>
 * This class implements an additional task: after printing out the list of 25 top words, </br>
 * it should print out the number of non-stop words with the letter z. Additional constraints:</br>
 * (i) no changes should be made to the existing classes; adding new classes and more lines of </br>
 * code to the main function is allowed; (ii) files should be read only once for both term-frequency </br>
 * and "words with z" tasks.
 * 
 */
public class BulletinBoardWordsWithZ {
  public static void main(String[] args) {

    EventManager em = new EventManager();
    new BulletinBoard_DataStorage(em);
    new BulletinBoard_StopWordFilter(em);
    new BulletinBoard_WordFrequencyCounter(em);
    new BulletinBoard_ZWordFilter(em);
    new WordFrequencyApplication(em);

    String[] event = { "run", Config.bookPath };
    em.publish(event);
  }
}

class BulletinBoard_ZWordFilter {
  EventManager em;
  int zstopwords_counter;

  public BulletinBoard_ZWordFilter(EventManager em) {
    zstopwords_counter = 0;
    this.em = em;
    try {
      em.subscribe("valid_word", this.getClass().getMethod("zword_increment", String[].class), this);
      em.subscribe("print", this.getClass().getMethod("print_zstopword_freqs", String[].class), this);
    } catch (NoSuchMethodException | SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void print_zstopword_freqs(String[] event) {
    System.out.println(zstopwords_counter);
  }

  public void zword_increment(String[] event) {
    String word = event[1];
    if (word.contains("z")) {
      zstopwords_counter++;
    }
  }
}