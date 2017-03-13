package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.Opcodes;

public class Deputy {

  public static String desc2type(String desc) {
    if(desc.charAt(0) != 'L') return desc;
    String type = desc.substring(1, desc.length() - 1);
    return type;
  }

  public static String joinStrings(String[] strings, String sep) {
    int length = strings.length;
    if(length == 0) return "";
    
    StringBuffer buffer = new StringBuffer();
    buffer.append(strings[0]);
    for(int i = 1; i<length; i++) {
      buffer.append(sep);
      buffer.append(strings[i]);
    }
    return buffer.toString();
  }

  public static String primitiveCode2String(int primitiveCode) {
    switch(primitiveCode) {
    case Opcodes.T_BOOLEAN:
      return "Z";
    case Opcodes.T_BYTE:
      return "B";
    case Opcodes.T_CHAR:
      return "C";
    case Opcodes.T_DOUBLE:
      return "D";
    case Opcodes.T_FLOAT:
      return "F";
    case Opcodes.T_INT:
      return "I";
    case Opcodes.T_LONG:
      return "J";
    case Opcodes.T_SHORT:
      return "S";
    default:
      throw new RuntimeException("Unexpected primitive code: " + primitiveCode);
    }
  }

}
