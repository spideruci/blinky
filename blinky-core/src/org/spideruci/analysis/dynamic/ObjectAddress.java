package org.spideruci.analysis.dynamic;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ObjectAddress {

  /**
   * Code copied from 
   * <a href="http://jtechbits.blogspot.com/2012/03/getting-address-of-object.html">
   *  http://jtechbits.blogspot.com
   * </a>.
   * @param object
   * @return
   */
  public static long getObjectAddress(Object object) {
    Unsafe unsafe = getUnsafe();
    long address = -1;

    if(null != unsafe) {
      try {
        

        address = getAddressFromObjectSetter(object, unsafe);
        
      } catch(Exception e) {
        e.printStackTrace();
      }
    } else
      System.out.println("Getting unsafe failed!");
    
    System.out.println("---------");
    
    return address;
  }

  private static long getAddressFromObjectSetter(Object object, Unsafe unsafe) throws NoSuchFieldException {
    int fieldOffset = unsafe.fieldOffset(Dummy.class.getDeclaredField("value"));
    System.out.println("\nField (value) offset: " + fieldOffset);
    int field2Offset = unsafe.fieldOffset(Dummy.class.getDeclaredField("xyz"));
    System.out.println("\nField (value) offset: " + field2Offset);
    
    long address;
    ObjectSetter objStr = new ObjectSetter();
    objStr.setObject(object);
    
    int objOffset = unsafe.fieldOffset(ObjectSetter.class.getDeclaredField("obj"));
    System.out.println("Field (obj) offset: " + objOffset);

    address = unsafe.getInt(objStr, (long) objOffset);
    System.out.println("ObjectSetter's hashcode: " + objStr.hashCode());
    System.out.println("Dummy's hashcode: " + object.hashCode());
    System.out.println("Dummy's object address: " + address);
    return address;
  }

  public static void main(String[] args) {
    getObjectAddress(new Dummy());
    getObjectAddress(new Dummy());
    getObjectAddress(new Dummy());
    getObjectAddress(new Object());

  }

  private static Unsafe getUnsafe() {
    Unsafe unsafe = null;

    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      unsafe = (Unsafe) field.get(null);
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return unsafe;
  }
}

class ObjectSetter {
  private Object obj;

  public void setObject(Object obj) {
    this.obj = obj;
  }
}

class Dummy {
  private int value = 123;
  private String xyz = "123";

  public int getValue() {
    return value;
  }
}