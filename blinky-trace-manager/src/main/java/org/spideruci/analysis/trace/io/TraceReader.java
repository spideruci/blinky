package org.spideruci.analysis.trace.io;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;

public class TraceReader {
	private final Iterator<TraceEvent> traceIterator;
	private TraceEvent currentEvent = null;

	public TraceReader(final File file) {
		TraceScanner scanner = new TraceScanner(file);
		traceIterator = scanner.iterator();
	}

	public TraceEvent readNextExecutedEvent() {
		TraceEvent event = null;
		while(traceIterator.hasNext()) {
			event = traceIterator.next();
			EventType type = event.getType();

			if(type == EventType.$$$) {
				currentEvent = event;
				return event;
			}

			if(type.isDecl()) {
				TraceData.methodDeclTable.put(event.getId(), event);
				continue;
			}

			if(type.isInsn()) {
				TraceData.insnTable.put(event.getId(), event);
				continue;
			}

			throw new RuntimeException("Unhandled Event Type: " + event.getType());
		}
		return null;
	}

	public boolean isMethodInvokeExec(TraceEvent event) {
		return (event.getType() == EventType.$$$ 
				&& event.getExecInsnType() == EventType.$invoke$);
	}

	public int getExecutedEventSourceLine(TraceEvent execEvent) {
		TraceEvent insnEvent = getInsnEvent(execEvent);
		int lineNumber = insnEvent.getInsnLine();
		return lineNumber;
	}

	public String getExecutedEventOwnerClass(TraceEvent execEvent) {
		TraceEvent declEvent = getDeclEvent(execEvent);
		return declEvent.getDeclOwner();
	}

	public String getExecutedEventOwnerMethod(TraceEvent execEvent) {
		TraceEvent declEvent = getDeclEvent(execEvent);
		return declEvent.getDeclName();
	}

	private TraceEvent getDeclEvent(TraceEvent execEvent) {
		TraceEvent insnEvent = getInsnEvent(execEvent);
		int declId = insnEvent.getInsnDeclHostId();
		TraceEvent declEvent = TraceData.methodDeclTable.get(declId);
		return declEvent;
	}

	private TraceEvent getInsnEvent(TraceEvent execEvent) {
		if(execEvent.getExecInsnType().isDecl()) {
			return null;
		}

		if(execEvent.getType().isInsn()) {
			return execEvent;
		}

		int execEventId = Integer.parseInt(execEvent.getExecInsnEventId());
		TraceEvent insnEvent = TraceData.insnTable.get(execEventId);
		return insnEvent;
	}

}
