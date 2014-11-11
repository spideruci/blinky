package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class SourceLineAdapter extends ClassVisitor {
  private String className;

  public SourceLineAdapter(ClassVisitor cv, String className) {
    super(Opcodes.ASM4, cv);
    this.className = className;
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    MethodVisitor mv;
    
    MethodProperties mid;
    mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
//    if(name.startsWith("palepuin_") || name.startsWith("palepuout_")) {
//      return mv;
//    }
    
    if(className.startsWith("java/util/AbstractCollection")) {
      if (mv != null 
          && (name.startsWith("toArray")
//              || name.startsWith("isEmpty")
//              || name.startsWith("contains")
//              || name.startsWith("finishToArray")
//              || name.startsWith("hugeCapacity")
//              || name.startsWith("add")
//              || name.startsWith("remove")
//              || name.startsWith("containsAll")
//              || name.startsWith("addAll")
//              || name.startsWith("removeAll")
//              || name.startsWith("retainAll")
//              || name.startsWith("clear")
//              || name.startsWith("toString")
              )
          && ((access & Opcodes.ACC_NATIVE) == 0)) {
        mid = new MethodProperties(className, name, access, desc);
        mv = new SourcelineMethodAdapter(mid, mv);
      }
      return mv;
    }
    
    if (mv != null 
        && ((access & Opcodes.ACC_NATIVE) == 0)) {
      mid = new MethodProperties(className, name, access, desc);
      mv = new SourcelineMethodAdapter(mid, mv);
    }
    return mv;
  }
}
