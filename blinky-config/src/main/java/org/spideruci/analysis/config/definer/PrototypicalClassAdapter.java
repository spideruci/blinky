package org.spideruci.analysis.config.definer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public abstract class PrototypicalClassAdapter extends ClassVisitor {
  
  protected final String className;
  
  /**
   * Because, an adapter means rewriting a class.
   */
  protected final ClassWriter classWriter;
  
  protected PrototypicalClassAdapter(int classWriterFlags, String className) {
    super(Opcodes.ASM5, new ClassWriter(classWriterFlags));
    this.className = className;
    this.classWriter = (ClassWriter) this.cv;
  }
  
  public ClassWriter getClassWriter() {
    return classWriter;
  }
  
}
