package org.spideruci.analysis.trace.events.props;

/**
 * Property Names for Method or Class declarations.
 * @author vpalepu
 *
 */
public enum DeclPropNames {
  NAME,
  OWNER, 
  ACCESS;
  
  public static final DeclPropNames[] values = DeclPropNames.values();
}
