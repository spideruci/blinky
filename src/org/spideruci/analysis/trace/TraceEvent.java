package org.spideruci.analysis.trace;

/**
 * TODO: rename to Event.
 * @author vpalepu
 *
 */
@SuppressWarnings("rawtypes")
public final class TraceEvent {
  
  private final int id;
  private final EventType type;
  private final String[] props;
  private final Enum[] propNames;
  
  /**
   * Creates a trace event that can store properties for the 
   * execution of a static instruction, and its properties are specified
   * with {@link org.spideruci.analysis.trace.InsnExecPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnExecEvent(int id, EventType type) {
    return new TraceEvent(id, type, InsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a 
   * static instruction in a method definition, as specified by 
   * {@link org.spideruci.analysis.trace.InsnPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnEvent(int id, EventType type) {
    return new TraceEvent(id, type, InsnPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a method or class
   * declaration, as specified by 
   * {@link org.spideruci.analysis.trace.DeclPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createDeclEvent(int id, EventType type) {
    return new TraceEvent(id, type, DeclPropNames.values);
  }
  
  private TraceEvent(int id, EventType type, Enum[] propNames) {
    if(propNames == null || propNames.length <= 0) {
      throw new UnsupportedOperationException();
    }
    this.id = id;
    this.type = type;
    this.propNames = propNames;
    this.props = new String[propNames.length];
  }

  public int getId() {
    return this.id;
  }

  public EventType getType() {
    return this.type;
  }

  public String getLog() {
    StringBuffer buffer = new StringBuffer();
    if(type != null) {
      buffer.append(type).append(',');
    }
    buffer.append(id).append(',');
    int lastIndex = props.length - 1;
    for(int i = 0; i < lastIndex; i += 1) {
      String value = props[i];
      buffer.append(value).append(',');
    }
    buffer.append(props[lastIndex]);
    return buffer.toString();
  }
  
  public int getPropCount() {
    return this.props.length;
  }

  public String getProp(int index) {
    return props[index];
  }

  public String getProp(final Enum propName) {
    if(!enumChecksOut(propName)) 
      return null;
    int index = propName.ordinal();
    return props[index];
  }

  public String getPropName(int index) {
    return propNames[index].toString();
  }

  public void setProp(int index, String propValue) {
    props[index] = propValue;
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
