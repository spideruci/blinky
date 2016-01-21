package org.spideruci.analysis.dynamic;

import org.spideruci.analysis.statik.instrumentation.Deputy;

public class RuntimeTypeProfiler {
  
  public static final StringBuffer enterBuffer = new StringBuffer();
  public static final StringBuffer invokeBuffer = new StringBuffer();
  public static Object[] tempObjectArray;
  
  public static final String BUFFER_TYPE_NAME_SYSID = "bufferTypeNameAndSysId";
  synchronized static public void bufferTypeNameAndSysId(final Object obj, 
      final String staticTypeName) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    
    bufferTypeNameAndSysId(obj, staticTypeName, enterBuffer, -1);
    
    Profiler.reguard(guard);
  }
  
  synchronized static public void bufferTypeNameAndSysId(final String typeName, 
      final Object obj) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    
    bufferTypeNameAndSysId(obj, typeName, enterBuffer, -1);
    
    Profiler.reguard(guard);
  }
  
  public static final String BUFFER_TYPE_NAME = "bufferTypeName";
  synchronized static public void bufferTypeName(String typeName) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    enterBuffer.append(typeName).append("~");
    Profiler.reguard(guard);
  }
  
  public static final String CLEAR_BUFFER = "clearBuffer";
  synchronized static public void clearBuffer() {
    final int length = enterBuffer.length();
    if(length == 0) {
      return;
    }
    
    enterBuffer.delete(0, length);
  }
  
  public static final String SETUP_INVOKE = "setupForInvoke";
  synchronized static public void setupForInvoke(int argCount) {
    tempObjectArray = new Object[argCount];
    final int length = invokeBuffer.length();
    if(length == 0) {
      return;
    }
    
    invokeBuffer.delete(0, length);
  }
  
  public static String getEnterRuntimeSignature() {
    if(Profiler.logEnterRuntimeSign) {
      return enterBuffer.toString();
    }
    
    return Deputy.NA;
  }
  
  public static String getInvokeRuntimeSignature() {
    if(Profiler.logInvokeRuntimeSign) {
      return invokeBuffer.toString();
    }
    
    return Deputy.NA;
  }
  
  public static final String PUT = "putParameter";
  synchronized public static void putParameter(Object obj, String staticTypeName, int index) {
    tempObjectArray[index] = obj;
    bufferTypeNameAndSysId(obj, staticTypeName, invokeBuffer, 0);
  }

  synchronized public static void putParameter(boolean obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "Z~");
  }

  synchronized public static void putParameter(byte obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "B~");
  }

  synchronized public static void putParameter(short obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "S~");
  }

  synchronized public static void putParameter(char obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "C~");
  }

  synchronized public static void putParameter(int obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "I~");
  }

  synchronized public static void putParameter(float obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "F~");
  }

  synchronized public static void putParameter(double obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "D~");
  }

  synchronized public static void putParameter(long obj, int index) {
    tempObjectArray[index] = obj;
    invokeBuffer.insert(0, "J~");
  }

  public static final String GET = "getParameter";
  synchronized public static Object getParameter(int index) {
    return tempObjectArray[index];
  }

  synchronized public static boolean getParameterZ(int index) {
    boolean x = (Boolean) tempObjectArray[index]; 
    return x;
  }

  synchronized public static byte getParameterB(int index) {
    byte x = (Byte) tempObjectArray[index]; 
    return x;
  }

  synchronized public static short getParameterS(int index) {
    short x = (Short) tempObjectArray[index]; 
    return x;
  }

  synchronized public static int getParameterI(int index) {
    int x = (Integer) tempObjectArray[index]; 
    return x;
  }

  synchronized public static char getParameterC(int index) {
    char x = (Character) tempObjectArray[index]; 
    return x;
  }

  synchronized public static float getParameterF(int index) {
    float x = (Float) tempObjectArray[index]; 
    return x;
  }

  synchronized public static long getParameterJ(int index) {
    long x = (Long) tempObjectArray[index]; 
    return x;
  }

  synchronized public static double getParameterD(int index) {
    Double x = (Double) tempObjectArray[index]; 
    return x;
  }
  

  /**
   * @param obj
   * @param staticTypeName
   */
  public static void bufferTypeNameAndSysId(final Object obj, 
      final String staticTypeName, StringBuffer buffer, final int pos) {
    final String typeName;
    final int sysid;
    
    if(staticTypeName.startsWith("[L") || obj == null) {
      typeName = staticTypeName;
    } else {
      typeName = obj.getClass().getName();
    }
    
    if(obj != null) {
      sysid = System.identityHashCode(obj);
    } else {
      sysid = 0;
    }
    
    StringBuffer subBuffer = new StringBuffer();
    subBuffer.append(typeName).append("#").append(sysid).append("~");
    
    if(pos == -1) {
      buffer.append(subBuffer);
    } else {
      buffer.insert(pos, subBuffer);
    }
  }

}
