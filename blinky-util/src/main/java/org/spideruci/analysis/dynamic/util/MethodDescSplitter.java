package org.spideruci.analysis.dynamic.util;

public class MethodDescSplitter {
  
  private static char[] argInitialsFromDesc(final String desc, 
      final boolean isMethodStatic) {
    Buffer buffer = Buffer.init();
    char[] descChars = desc.toCharArray();
    for(int i = 0; i < descChars.length; i += 1) {
      char descChar = descChars[i];
      switch(descChar) {
      case '[':
        continue;
      case 'B': case 'C': case 'D':
      case 'F': case 'I': case 'J':
      case 'S':  case 'Z':
        buffer.append(descChar);
        continue;
      case 'L':
        buffer.append(descChar);
        while(descChar != ';') {
          descChar = descChars[++i];
        }
      }
    }
    return buffer.value();
  }
  
  public static String[] getArgTypes(String methodName, String methodOwner, boolean isStatic) {
    final String[] temp = getArgTypeSplit(methodName);
    if(isStatic) {
      return temp;
    }
    else {
      final String[] argTypes = new String[temp.length + 1];
      argTypes[0] = "L" + methodOwner + ";";
      for(int i = 0; i < temp.length; i += 1) {
        argTypes[i+1] = temp[i];
      }
      return argTypes;
    }
  }
  
  public static String[] getArgTypeSplit(String desc) {
    desc = extractDescFromMethodIdent(desc);
    
    int argCount = argInitialsFromDesc(desc, true).length;
    String[] argTypeSplit = new String[argCount];

    int count = 0;
    StringBuilder arrayBuilder = new StringBuilder();
    char[] chs = desc.toCharArray();
    for(int i = 0; i < chs.length; i += 1) {
      char ch = chs[i];
      if(ch == '[') {
        arrayBuilder.append(ch);
        continue;
      }
      
      if(ch == 'L') {
        Buffer type = new Buffer();
        type.append(ch);
        while(ch != ';') {
          ch = chs[++i];
          type.append(ch);
        }
        argTypeSplit[count] = type.toString();
      } else {
        argTypeSplit[count] = String.valueOf(ch);
      }
      
      int arrayBuilderLength = arrayBuilder.length(); 
      if(arrayBuilderLength != 0) {
        argTypeSplit[count] = arrayBuilder.toString() + argTypeSplit[count];
        arrayBuilder.delete(0, arrayBuilderLength);
      }

      count += 1;
    }

    return argTypeSplit;
  }
  
  private static String extractDescFromMethodIdent(String methodName) {
    int beginIndex = methodName.indexOf('(') + 1;
    int endIndex = methodName.lastIndexOf(')');
    
    endIndex = (endIndex == -1) ? methodName.length() : endIndex;
    
    Buffer desc = Buffer.init();
    for(int i = beginIndex; i < endIndex; i += 1) {
      desc.append(methodName.charAt(i));
    }
    
    return desc.toString();
  }

}
