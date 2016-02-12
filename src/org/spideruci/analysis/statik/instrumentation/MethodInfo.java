package org.spideruci.analysis.statik.instrumentation;

public class MethodInfo {

	private int startLine = -1;
	private int endLine = -1;
	private String className = null;
	
	public int getStartLine(){
		return startLine;
	}
	
	public int getEndLine(){
		return endLine;
	}
	
	public String getClassName(){
		return className;
	}
	
	public void putStartLine(int line){
		this.startLine = line;
	}
	
	public void putEndLine(int line){
		this.endLine = line;
	}
	
	public void putClassName(String name){
		this.className = name;
	}
	
}
