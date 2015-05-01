package org.spideruci.analysis.trace;

import org.objectweb.asm.Opcodes;

public class Event {
  public static final String SOURCELINE_EVENT = "$sourcelinenumber$";
  
  private final String id;
  @SuppressWarnings("unused") private final String threadId;
  @SuppressWarnings("unused") private final String objectId;
  @SuppressWarnings("unused") private final String time;
  private final String sourceLine;
  private final String sourceClass;
  private final String sourceMethod;
  private final String eventType;
  private final int opcode;
  
    private Event(
        String threadId,
        String id,
        String time,
        String objectId,
        String sourceLine, 
        String sourceClass,
        String sourceMethod,
        String type,
        int opcode) {
      this.eventType = type;
      this.sourceLine = sourceLine;
      this.sourceClass = sourceClass;
      this.sourceMethod = sourceMethod;
      this.time = time;
      this.objectId = objectId;
      this.id = id;
      this.threadId = threadId;
      this.opcode = opcode;
    }
  
  public static Event fromString(String eventString) {
    if(!eventString.startsWith("*")) {
      return null;
    }
    eventString = eventString.substring(1);
    String[] split = eventString.split(",");
    String[] split_0 = split[0].split("\\*");
    String threadId;
    String id;
    String time;
    String objectId;
    String sourceLine;
    String sourceClass;
    String sourceMethod;
    String type;
    String opcode;
    try {
      threadId = split_0[0];
      id = split_0[1];
      time = split[1];
      objectId = split[2];
      sourceLine = split[3]; 
      sourceClass = split[4];
      sourceMethod = split[5];
      type = split[6];
      opcode = split[7];
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      System.err.println(eventString);
      return null;
    } 
    Event event = 
        new Event(
            threadId, 
            id, 
            time, 
            objectId,
            sourceLine, 
            sourceClass, 
            sourceMethod, 
            type,
            Integer.parseInt(opcode));
    return event;
  }
  
  public String toSourceString() {
    final String sep = ":";
    StringBuffer buffer = new StringBuffer();
    buffer.append(sourceClass).append(sep);
    buffer.append(sourceMethod).append(sep);
    buffer.append("L").append(sourceLine);
    return buffer.toString();
  }
  
  public boolean isSourceEvent() {
    if(SOURCELINE_EVENT.equals(this.eventType)) {
      return true;
    }
    
    return false;
  }
  
  public boolean isMethodInvokeEvent() {
    switch(opcode) {
    case Opcodes.INVOKEINTERFACE:
    case Opcodes.INVOKESPECIAL:
    case Opcodes.INVOKESTATIC:
    case Opcodes.INVOKEVIRTUAL:
      return true;
    default:
        return false;
    }
  }
  
  public String id() {
    return id;
  }

  public String ownerClass() {
    return sourceClass;
  }
  
  public String ownerMethod() {
    return sourceMethod;
  }
  
  public String sourceLine() {
    return sourceLine;
  }

  public String invokedMethod() {
    if(isMethodInvokeEvent()) {
      return eventType;
    }
    return null;
  }
  
}
