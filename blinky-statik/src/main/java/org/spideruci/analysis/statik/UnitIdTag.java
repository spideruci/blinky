package org.spideruci.analysis.statik;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class UnitIdTag implements Tag {
  
  public static final String UNIT_ID_TAG_NAME = "UNIT_ID_TAG";
  
  private final String value;
  
  public UnitIdTag(final String id) {
    value = id;
  }

  @Override
  public String getName() {
    return UNIT_ID_TAG_NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return value.getBytes();
  }
  
  public String value() {
    return value;
  }

}
