package org.spideruci.analysis.util.caryatid;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class DiagnosisMessage {
  private final HashMap<String, String> attributeTable;
  
  public static DiagnosisMessage init() {
    return new DiagnosisMessage();
  }
  
  private DiagnosisMessage() {
    attributeTable = new LinkedHashMap<>();
  }
  
  public DiagnosisMessage append(String attribute, String value) {
    attributeTable.put(checkNotNull(attribute), checkNotNull(value));
    return this;
  }
  
  public DiagnosisMessage append(String attribute, Object value) {
    attributeTable.put(checkNotNull(attribute), checkNotNull(value).toString());
    return this;
  }
  
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for(String attribute : attributeTable.keySet()) {
      String value = attributeTable.get(attribute);
      buffer.append(attribute).append(" = ").append(value).append(";");
    }
    return buffer.append("\n").toString();
  }
  
}