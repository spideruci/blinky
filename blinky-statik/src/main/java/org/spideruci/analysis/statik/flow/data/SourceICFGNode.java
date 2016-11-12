package org.spideruci.analysis.statik.flow.data;

public class SourceICFGNode {

	private int lineNum;
	private String className;
	private String methodName;
	
	public SourceICFGNode(int lineNum, String className, String methodName){
		this.lineNum = lineNum;
		this.className = className;
		this.methodName = methodName;
	}
	
	public void setLineNum(int lineNum){
		this.lineNum = lineNum;
	}
	
	public void setClassName(String name){
		className = name;
	}
	
	public void setMethodName(String name){
		methodName = name;
	}
	
	public int getLineNum(){
		return lineNum;
	}

	public String getClassName(){
		return className;
	}
	
	public String getMethodName(){
		return methodName;
	}
	

	@Override
	public String toString(){
		return "LineNum: " + lineNum + " className: " + className + " methodName: " + methodName;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof SourceICFGNode){
			SourceICFGNode node = (SourceICFGNode) o;
			return (this.lineNum == node.getLineNum()) && this.className.equals(node.getClassName()) && this.methodName.equals(node.getMethodName());
		}
		return false;
	}

}
