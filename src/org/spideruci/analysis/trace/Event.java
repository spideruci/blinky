package org.spideruci.analysis.trace;

public class Event {
  public static final String SOURCELINE_EVENT = "$sourcelinenumber$";
  
  private final String id;
  private final String threadId;
  private final String objectId;
  private final String time;
  private final String sourceLine;
  private final String sourceClass;
  private final String sourceMethod;
  private final String eventType;
  
    private Event(
        String threadId,
        String id,
        String time,
        String objectId,
        String sourceLine, 
        String sourceClass,
        String sourceMethod,
        String type) {
      this.eventType = type;
      this.sourceLine = sourceLine;
      this.sourceClass = sourceClass;
      this.sourceMethod = sourceMethod;
      this.time = time;
      this.objectId = objectId;
      this.id = id;
      this.threadId = threadId;
    }
  
  public static Event fromString(String eventString) {
    if(!eventString.startsWith("*")) {
      return null;
    }
    eventString = eventString.substring(1);
    String[] split = eventString.split(",");
    String[] split_0 = split[0].split("\\*");
    String threadId = split_0[0];
    String id = split_0[1];
    String time = split[1];
    String objectId = split[2];
    String sourceLine = split[3]; 
    String sourceClass = split[4];
    String sourceMethod = split[5];
    String type = split[6]; 
    Event event = 
        new Event(
            threadId, 
            id, 
            time, 
            objectId,
            sourceLine, 
            sourceClass, 
            sourceMethod, 
            type);
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
  
}
