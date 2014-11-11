package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.Opcodes;

public class MethodProperties {
  
  public static final String desc = "Ledu/uci/spiderlab/analysis/dynamic/instrumentation/control/MethodProperties;"; 

	public String MethodOwnerName;
	public String MethodName;
	public int MethodAccess;
	public String MethodDescription;

	public MethodProperties(
			String className, 
			String methodName, 
			int methodAccess, 
			String methodDescription)  {
		this.MethodOwnerName = className;
		this.MethodName = methodName;
		this.MethodAccess = methodAccess;
		this.MethodDescription = methodDescription;
	}
	
	public String toString() {
		return this.MethodName + this.MethodDescription 
				+ " : " 
				+ this.MethodAccess + " " + this.MethodOwnerName;
	}
	
	public boolean isStatic() {
	  return (this.MethodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC; 
	}
	
	public String callSite() {
	  return MethodOwnerName + "/" + MethodName + MethodDescription;
	}
	
	public static class LocalVariable {
		public String name;
		public String desc;
		public int index;
		public int start;
		public int end;
		
		public LocalVariable(
				String name, 
				String desc, 
				int index, 
				int start, 
				int end) {
			this.name = name;
			this.desc = desc;
			this.index = index;
			this.start = start;
			this.end = end;
		}
		
		public String toString() {
			return "name: " + name
					+ " desc: " + desc
					+ " index:" + index
					+ " start:" + start
					+ " end:" + end;
		}
	}
}