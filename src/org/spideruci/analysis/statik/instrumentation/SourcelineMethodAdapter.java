package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;


public class SourcelineMethodAdapter extends AdviceAdapter {
  
  private MethodProperties methodProps;
  
  public SourcelineMethodAdapter(MethodProperties methodProps, MethodVisitor mv) {
    super(Opcodes.ASM4, mv, methodProps.MethodAccess, methodProps.MethodName, 
        methodProps.MethodDescription);
    this.methodProps = methodProps;
  }
  
  @Override
  public void visitLineNumber(int line, Label start) {
    super.visitLineNumber(line, start); //make the actual call.
  }

}
