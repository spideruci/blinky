package org.spideruci.analysis.trace;

import org.spideruci.util.MyAssert;

/**
 * TODO: rename to Event.
 * @author vpalepu
 *
 */
@SuppressWarnings("rawtypes")
public final class TraceEvent {
  
  private static final String SEP = ",";
  
  private final int id;
  private final EventType type;
  private final String[] propValues;
  private final Enum[] propNames;
  
  /**
   * Creates a trace event that can store properties for the 
   * execution of a static instruction, and its properties are specified
   * with {@link InsnExecPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, InsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a static instruction 
   * in a method definition, as specified by {@link InsnPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnEvent(int id, EventType type) {
    return new TraceEvent(id, type, InsnPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a method or class
   * declaration, as specified by {@link DeclPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createDeclEvent(int id, EventType type) {
    return new TraceEvent(id, type, DeclPropNames.values);
  }
  
  public static TraceEvent valueOf(String eventString) {
    String[] split = eventString.split(SEP);
    String typeString = split[0];
    int id = Integer.parseInt(split[1]);
    
    TraceEvent event;
    EventType type = EventType.valueOf(typeString);
    if(type == EventType.$$$) {
      event = TraceEvent.createInsnExecEvent(id);
    } else if(type.isDecl()) {
      event = TraceEvent.createInsnEvent(id, type);
    } else {
      event = TraceEvent.createDeclEvent(id, type);
    }
    
    final int offset = 2;
    MyAssert.assertThat(event.getPropCount() == 
        (event.propNames.length  - offset));
    
    for(int i = offset; i < split.length; i += 1) {
      event.setProp(i - offset, split[i]);
    }
    
    return event;
  }
  
  private TraceEvent(int id, EventType type, Enum[] propNames) {
    if(propNames == null || propNames.length <= 0) {
      throw new UnsupportedOperationException();
    }
    this.id = id;
    this.type = type;
    this.propNames = propNames;
    this.propValues = new String[propNames.length];
  }

  public int getId() {
    return this.id;
  }

  public EventType getType() {
    return this.type;
  }

  public String getLog() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(type == null ? "" : type).append(SEP);
    buffer.append(id).append(SEP);
    int lastIndex = propValues.length - 1;
    for(int i = 0; i < lastIndex; i += 1) {
      String value = propValues[i];
      buffer.append(value == null ? "" : value).append(SEP);
    }
    buffer.append(propValues[lastIndex]);
    return buffer.toString();
  }
  
  public int getPropCount() {
    return this.propValues.length;
  }

  public String getProp(int index) {
    return propValues[index];
  }

  public String getProp(final Enum propName) {
    if(!enumChecksOut(propName)) 
      return null;
    int index = propName.ordinal();
    return propValues[index];
  }

  public String getPropName(int index) {
    return propNames[index].toString();
  }

  public void setProp(int index, String propValue) {
    propValues[index] = propValue;
  }

  public void setProp(final Enum propName, final String propValue) {
    if(enumChecksOut(propName))
      setProp(propName.ordinal(), propValue);
  }
  
    private boolean enumChecksOut(final Enum propName) {
      if(propName == null
          || propName.getDeclaringClass() != propNames[0].getDeclaringClass())
        return false;
      return true;
    }
    
  public String getDeclName() {
    return getProp(DeclPropNames.NAME);
  }
  
  public String getDeclAccess() {
    return getProp(DeclPropNames.ACCESS);
  }
  
  public String getDeclOwner() {
    return getProp(DeclPropNames.OWNER);
  }
  
  @Override
  public String toString() {
    return getLog();
  }

}
