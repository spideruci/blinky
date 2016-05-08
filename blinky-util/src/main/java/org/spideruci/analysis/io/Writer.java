package org.spideruci.analysis.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
  
  private static Writer w;
  
  public static Writer singleton() {
    if(isSingletonDisabled()) {
      throw new RuntimeException("Cannot use Writer without intializing it."
          + "Use method, `Writer.initSingleton(File file)` to initialize.");
    }
    return w;
  }
  
  public static boolean isSingletonDisabled() {
    return w == null;
  }

  public static Writer initSingleton(File file) {
    w = new Writer();
    w.setWriter(file);
    return w;
  }
  
  public static Writer create(File file) {
    Writer w = new Writer();
    w.setWriter(file);
    return w;
  }
  
  private BufferedWriter writer;
  
  public static Writer init(File file) {
    Writer w = new Writer();
    w.setWriter(file);
    return w;
  }
  
  private Writer() { }

	private void setWriter(File file) {
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			writer = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if(writer == null) return;
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeln(String snippet) {
		try {
			writer.write(snippet);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeln(StringBuffer snippet) {
	  if(snippet == null) return;
	  writeln(snippet.toString());
	}
}
