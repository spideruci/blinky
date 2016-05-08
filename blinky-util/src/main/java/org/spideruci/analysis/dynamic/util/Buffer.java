package org.spideruci.analysis.dynamic.util;

import org.spideruci.analysis.util.MyAssert;

final public class Buffer {
  
  public static final String BUFFER_TYPENAME = "org/spideruci/analysis/dynamic/util/Buffer";
  public static final String BUFFER_DESC = "Lorg/spideruci/analysis/dynamic/util/Buffer;";
  
  public static Buffer init() {
    return new Buffer();
  }
  
  /**
   * The number of characters actually used in the Buffer.
   */
  private int count;
  
  /**
   * The array holding the characters of the Buffer.
   */
  private char[] value;
  
  public Buffer() {
    this.count = 0;
    this.value = new char[16];
  }
  
  public int length() {
    return this.count;
  }
  
  public Buffer append(char ch) {
    return this.append(String.valueOf(ch));
  }
  
  public Buffer append(String str) {
    MyAssert.assertThat(str != null, "String sent to the buffer was null.");
    int length = str.length();
    
    if(length > 0) {
      checkBufferSize(this.count + length);
      str.getChars(0, length, this.value, this.count);
      this.count += length;
    }
    
    return this;
  }
  
  public Buffer insert(int pos, String str) {
    MyAssert.assertThat(str != null, "String sent to the buffer was null.");
    int length = str.length();
    
    if(length != 0) {
      checkBufferSize(this.count + length);
      System.arraycopy(this.value, pos, this.value, pos + length, this.count - pos);
      str.getChars(0, length, this.value, pos);
      this.count += length;
    }
    
    return this;
  }
  
    private void checkBufferSize(int requiredSize) {
      if(this.value.length < requiredSize) {
        int newSize = value.length * 2 + 2;
        if (newSize - requiredSize < 0)
            newSize = requiredSize;
//        if (newSize < 0) {
//            if (requiredSize < 0) // overflow
//                throw new OutOfMemoryError();
//            newSize = Integer.MAX_VALUE;
//        }
        value = getArrayCopyOf(value, newSize);
      }
    }
  
  public Buffer clear() {
    count = 0;
    trimToSize();
    return this;
  }
  
  public char[] value() {
    trimToSize();
    char[] copy = new char[this.count];
    for(int i = 0; i < this.count; i += 1) {
      copy[i] = value[i];
    }
    
    return copy;
  }
  
  @Override
  public String toString() {
    trimToSize();
    return new String(this.value);
  }
  
    private void trimToSize() {
      if (this.count < this.value.length) {
          this.value = getArrayCopyOf(this.value, this.count);
      }
    }
  
  private static char[] getArrayCopyOf(char[] original, int newLength) {
    char[] copy = new char[newLength];
    System.arraycopy(original, 0, copy, 0, 
        Math.min(original.length, newLength));
    return copy;
  }

}
