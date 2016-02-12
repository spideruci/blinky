package org.spideruci.analysis.statik.instrumentation;

import java.util.HashMap;

public class ClassSourceMap {

	
	  private static HashMap<String, String> classSourceMap = new HashMap<String, String>();
	  
	  public static HashMap<String, String> getclassSourceMap(){
		  return classSourceMap;
	  }
	  
	  public static void putsourceClassMap(String className, String sourceName){
		  classSourceMap.put(className, sourceName);
	  }
	  
	  public static String getSource(String className){
		  return classSourceMap.get(className);
	  }
}
