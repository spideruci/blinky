package org.spideruci.analysis.dynamic;

public class RuntimeTypeProfiler {
  
  public static final StringBuffer buffer = new StringBuffer();
  
  public static final String BUFFER_TYPE_NAME_SYSID = "bufferTypeNameAndSysId";
  synchronized static public void bufferTypeNameAndSysId(final Object obj, 
      final String staticTypeName) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    
    final String TypeName;
    final int sysid;
    
    if(obj != null) {
      TypeName = obj.getClass().getName();
      sysid = System.identityHashCode(obj);
      
    } else {
      TypeName = staticTypeName;
      sysid = 0;
    }
    
    buffer.append(TypeName).append("#").append(sysid).append("~");
    
    Profiler.reguard(guard);
  }
  
  synchronized static public void bufferTypeNameAndSysId(final String typeName, 
      final Object obj) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    
    final int sysid = System.identityHashCode(obj);
    buffer.append(typeName).append("#").append(sysid).append("~");
    
    Profiler.reguard(guard);
  }
  
  public static final String BUFFER_TYPE_NAME = "bufferTypeName";
  synchronized static public void bufferTypeName(String typeName) {
    if(Profiler.$guard1$) {
      return;
    }
    
    boolean guard = Profiler.guard();
    buffer.append(typeName).append("~");
    Profiler.reguard(guard);
  }
  
  public static final String CLEAR_BUFFER = "clearBuffer";
  synchronized static public void clearBuffer() {
    final int length = buffer.length();
    if(length == 0) {
      return;
    }
    
    buffer.delete(0, length);
  }

}
