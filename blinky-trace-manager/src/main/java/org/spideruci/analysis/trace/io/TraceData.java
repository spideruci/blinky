package org.spideruci.analysis.trace.io;

import java.util.HashMap;
import java.util.Iterator;

import org.spideruci.analysis.trace.TraceEvent;

public class TraceData {
	  public static HashMap<Integer, TraceEvent> insnTable;
	  public static HashMap<Integer, TraceEvent> methodDeclTable;

	  public TraceData(){
		  insnTable = new HashMap<>();
		  methodDeclTable = new HashMap<>();
	  }
}
