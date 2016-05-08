package org.spideruci.analysis.trace;

public interface MethodDecl {
  
  public int getId();
  public String getDeclOwner();
  public String getDeclName();
  public String getDeclAccess();
  public EventType getType();
  public String getLog();
  
}
