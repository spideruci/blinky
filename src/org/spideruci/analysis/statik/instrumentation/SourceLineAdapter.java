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
    
    mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if (mv != null 
        && ((access & Opcodes.ACC_NATIVE) == 0)) {
      MethodProperties mid = new MethodProperties(className, name, access, desc);
      mv = new SourcelineMethodAdapter(mid, mv);
    }
    return mv;
  }
}
