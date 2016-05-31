/*
@author Charles.Y.Feng
@date May 12, 2016 4:40:41 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ini4j.Ini;
import org.spideruci.analysis.diagnostics.subjects.styleEx.Config;


/**
 * <h2>Style Requirement: No Commitment</h2> 
 * <p> This style is at the center of software evolution and customization. </br> 
 * Developing software that is meant to be extended by others, or even by </br> 
 * the same developers but at a later point in time, carries a set of challenges </br> 
 * that don't exist in close-ended software.
 * </p>
 */
public class NoCommitment {

  public void load_plugins(String fileName) throws Exception {


	String configFilePath = this.getClass().getClassLoader().getResource("config.ini").getFile();
    Ini config = new Ini(new File(configFilePath));
    URLClassLoader cl = (URLClassLoader) this.getClass().getClassLoader();
    Words words = (Words) Class.forName(config.get("Plugins", "words"), true, cl).newInstance();
    Frequencies freqs = (Frequencies) Class.forName(config.get("Plugins", "frequencies"), true, cl).newInstance();
    Print print = (Print) Class.forName(config.get("Plugins", "print"), true, cl).newInstance();
    print.print(freqs.top25(words.extractWords(fileName)));
  }

  public static void main(String[] args) throws Exception {
    NoCommitment entry = new NoCommitment();
    entry.load_plugins(Config.bookPath);
  }
}
