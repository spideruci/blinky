package org.spideruci.analysis.config.definer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class PrototypicalClassNode extends ClassNode {
  
  protected final String className;
  
  /**
   * Because, an adapter means rewriting a class.
   */
  protected final ClassWriter classWriter;
  
  protected PrototypicalClassNode(int classWriterFlags, String className) {
    super(Opcodes.ASM5);
    this.className = className;
    this.cv = new ClassWriter(classWriterFlags);
    this.classWriter = (ClassWriter) cv;
  }
  
  public ClassWriter getClassWriter() {
    return classWriter;
  }
  

}
