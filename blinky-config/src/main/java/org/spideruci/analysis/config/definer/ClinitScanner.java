package org.spideruci.analysis.config.definer;

import java.util.TreeSet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClinitScanner extends MethodVisitor {

  private final TreeSet<String> definedStaticFields;
  
  protected ClinitScanner(MethodVisitor mv, TreeSet<String> definedStaticFields) {
    super(Opcodes.ASM5, mv);
    this.definedStaticFields = definedStaticFields;
  }
  
  @Override
  public void visitFieldInsn(int opcode, String owner, String name,
      String desc) {
    
    if(opcode == Opcodes.PUTSTATIC) {
      definedStaticFields.add(name);
    }
  }
  
  

}
