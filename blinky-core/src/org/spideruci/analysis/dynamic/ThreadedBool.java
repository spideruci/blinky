package org.spideruci.analysis.dynamic;

public class ThreadedBool {
  
  private static final int ARRAY_SIZE = 200;
  
  private int size = 0;
  private boolean[] bools = new boolean[ARRAY_SIZE];
  private long[] idIndex = new long[ARRAY_SIZE];
  
  public ThreadedBool() {}
  
  public boolean get() {
    long threadId = Thread.currentThread().getId();
    int threadIdx = getIdx(threadId);
    return bools[threadIdx];
  }
  
  public void set(boolean value) {
    long threadId = Thread.currentThread().getId();
    int threadIdx = getIdx(threadId);
    bools[threadIdx] = value;
  }
  
  private int getIdx(long threadId) {
    for(int i = 0; i < size; i += 1) {
      long id = idIndex[i];
      if(threadId == id)
        return i;
    }
    
    return addThreadSlot(threadId);
  }
  
  private int addThreadSlot(long threadId) {
    if(size >= idIndex.length)
      expandSlots();
    
    final int newIdx = size;
    idIndex[newIdx] = threadId;
    size += 1;
    
    return newIdx;
  }
  
  private void expandSlots() {
    boolean[] newBools = new boolean[ARRAY_SIZE + size];
    long[] newIdIndex = new long[ARRAY_SIZE + size];
    
    System.arraycopy(this.bools, 0, newBools, 0, size);
    System.arraycopy(this.idIndex, 0, newIdIndex, 0, size);
  }

}
