package org.spideruci.analysis.trace.io;

import java.util.HashMap;
import java.util.Iterator;

import org.spideruci.analysis.trace.TraceEvent;

public class TraceData {
	private HashMap<Integer, TraceEvent> insnTable;
	private HashMap<Integer, TraceEvent> methodDeclTable;

	private static TraceData instance = null;
	
	private TraceData(){
		empty();
	}
	
	public static TraceData getInstance(){
		if (instance == null) {
			instance = new TraceData();
		}
		return instance;
	}
	
	public void empty(){
		insnTable = new HashMap<>();
		methodDeclTable = new HashMap<>();
	}
	
	public void putInsnEvent(int execEventId, TraceEvent event){
		insnTable.put(execEventId, event);
	}
	
	public void putMethodDecl(int declId, TraceEvent event){
		methodDeclTable.put(declId, event);
	}
	
	public TraceEvent getDeclEvent(int declId){
		return methodDeclTable.get(declId);
	}
	
	public TraceEvent getInsnEvent(int execEventId){
		return insnTable.get(execEventId);
	}
}
