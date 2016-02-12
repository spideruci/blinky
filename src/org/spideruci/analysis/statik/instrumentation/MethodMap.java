package org.spideruci.analysis.statik.instrumentation;

import java.util.HashMap;

public class MethodMap {
	
	  private static HashMap<String, MethodInfo> methodMap = new HashMap<String, MethodInfo>();
	  
	  public static HashMap<String, MethodInfo> getMethodMap(){
		  return methodMap;
	  }
	  
	  public static void clearMethodMap(){
		  methodMap = new HashMap<String, MethodInfo>();
	  }
	  
	  /* 
	   * Put method information into @methodMap
	   * @name : method name
	   * @line : source line number
	   * @className: class name that the method belongs to
	   */
	  public static void putMethod(String name, int line, String className){
		  
		  //if method doesn't exist in @methodMap, 
		  //put the method into the hash and its class information
		  if(methodMap.get(name) == null){
			  methodMap.put(name, new MethodInfo());
			  methodMap.get(name).putClassName(className);
		  }

		  //if method start line is set to -1
		  //put the current source line number as the method start line and end line
		  if(methodMap.get(name).getStartLine() == -1){
			  methodMap.get(name).putStartLine(line);
			  methodMap.get(name).putEndLine(line);
		  }
			  
		  //if method start line is not -1 and smaller than current source line number,
		  //put the current source line number as the method start line
		  else if(methodMap.get(name).getStartLine() > line)
			  methodMap.get(name).putStartLine(line);
		  
		  //if method start line is not -1 and the end line is smaller than current source line number,
		  //put the current source line number as the method end line
		  else if(methodMap.get(name).getEndLine() < line)
				  methodMap.get(name).putEndLine(line);
		 
	  }
	  
	  public static void putMethodClass(String methodName, String className){
		  methodMap.get(methodName).putClassName(className);
	  }
	  
	  
	  public static void putMethodMap(String name, MethodInfo info){
		  methodMap.put(name, info);
	  }
	  

}
