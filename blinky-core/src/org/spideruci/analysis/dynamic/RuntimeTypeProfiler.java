package org.spideruci.analysis.dynamic;

import org.spideruci.analysis.dynamic.util.Buffer;
import org.spideruci.analysis.statik.instrumentation.Config;

public class RuntimeTypeProfiler {
  
  public static Object[] tempObjectArray;
  
  public static final String BUFFER_TYPE_NAME_SYSID = "bufferTypeNameAndSysId";
  synchronized static public Buffer bufferTypeNameAndSysId(final Buffer buffer,
      final Object obj, final String staticTypeName) {
    if(!Profiler.$guard1$) {
      boolean guard = Profiler.guard();
      bufferTypeNameAndSysId(obj, staticTypeName, buffer, -1);
      Profiler.reguard(guard);
    }
    
    return buffer;
  }
  
  synchronized static public Buffer bufferTypeNameAndSysId(final Buffer buffer, 
      final String typeName, final Object obj) {
    if(!Profiler.$guard1$) {
      boolean guard = Profiler.guard();
      bufferTypeNameAndSysId(obj, typeName, buffer, -1);
      Profiler.reguard(guard);
    }
    
    return buffer;
  }
  
  public static final String BUFFER_TYPE_NAME = "bufferTypeName";
  synchronized static public Buffer bufferTypeName(Buffer buffer, String typeName) {
    if(!Profiler.$guard1$) {
      boolean guard = Profiler.guard();
      buffer.append(typeName).append("~");
      Profiler.reguard(guard);
    }
    
    return buffer;
  }
  
  public static final String GET_SIGN = "getEnterRuntimeSignature";
  public static String getEnterRuntimeSignature(Buffer buffer) {
    if(Profiler.logEnterRuntimeSign && buffer != null) {
      if(buffer.length() == 0) {
        return "~";
      }
      return buffer.toString();
    }
    
    return Config.NA;
  }
  
  public static final String SETUP_INVOKE = "setupForInvoke";
  synchronized static public void setupForInvoke(int argCount) {
    tempObjectArray = new Object[argCount];
    Profiler.REAL_ERR.println("****" + tempObjectArray.length);
  }
  
  synchronized static public void setupForInvoke(int argCount, String methodName) {
    tempObjectArray = new Object[argCount];
  }
  
  public static String getInvokeRuntimeSignature() {
    if(Profiler.logInvokeRuntimeSign) {
//      String sign = invokeBuffer.toString();
//      if(sign.trim().isEmpty()) {
//        sign = "~";
//      }
//      return sign;
      return "~";
    }
    
    return Config.NA;
  }
  
  private static void printLnArgLog(String typeInfo, String index) {
    Profiler.printLnInvokeArgLog(typeInfo, String.valueOf(index));
  }
  
  public static final String PUT = "putParameter";
  synchronized public static void putParameter(Object obj, String staticTypeName, int index) {
    try {
      tempObjectArray[index] = obj;
    } catch(ArrayIndexOutOfBoundsException e) {
      Profiler.REAL_ERR.println(">>" + staticTypeName);
      throw e;
    }
    
    final String type = obj == null ? staticTypeName : obj.getClass().getName();
    printLnArgLog(staticTypeName, String.valueOf(index));
  }

  synchronized public static void putParameter(boolean obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("Z", String.valueOf(index));
  }

  synchronized public static void putParameter(byte obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("B", String.valueOf(index));
  }

  synchronized public static void putParameter(short obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("S", String.valueOf(index));
  }

  synchronized public static void putParameter(char obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("C", String.valueOf(index));
  }

  synchronized public static void putParameter(int obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("I", String.valueOf(index));
  }

  synchronized public static void putParameter(float obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("F", String.valueOf(index));
  }

  synchronized public static void putParameter(double obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("D", String.valueOf(index));
  }

  synchronized public static void putParameter(long obj, int index) {
    tempObjectArray[index] = obj;
    printLnArgLog("J", String.valueOf(index));
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
    Profiler.REAL_OUT.println(tempObjectArray[index]);
    
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
      final String staticTypeName, Buffer buffer, final int pos) {
    String typeName;
    final int sysid;
    
    if(obj == null) {
      typeName = staticTypeName;
    } else {
      typeName = obj.getClass().getName();
    }
    
    if(obj != null) {
      sysid = System.identityHashCode(obj);
    } else {
      sysid = 0;
    }
    
    Buffer subBuffer = new Buffer();
    subBuffer.append(typeName).append("#").append(String.valueOf(sysid)).append("~");
    
    if(pos == -1) {
      buffer.append(subBuffer.toString());
    } else {
      buffer.insert(pos, subBuffer.toString());
    }
  }

}
