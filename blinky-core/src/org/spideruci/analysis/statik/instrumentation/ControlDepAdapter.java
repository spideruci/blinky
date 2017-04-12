package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.spideruci.analysis.statik.controlflow.ControlFlowAnalyzer;

public class ControlDepAdapter extends ClassNode {
  
  public static final String BYTECODE_LDC_MARKER = "blinkybytecode@";
  
  private String className;
  private ClassVisitor cv;
  private boolean debug;
  
  public ControlDepAdapter(ClassVisitor cv, String className, boolean debug) {
    super(Opcodes.ASM5);
    this.className = className;
    this.cv = cv;
    this.debug = debug;
  }

  @Override 
  public void visitEnd() {
    if(debug) {
      System.out.println(this.className);
    }

    for(MethodNode methodNode : this.methods) {
      AbstractInsnNode[] insns = methodNode.instructions.toArray();
      
      if(ClassInstrumenter.CONTROL_FLOW) {
        try {
          final String methodName = methodNode.name + methodNode.desc;

          ControlFlowAnalyzer analyzer = ControlFlowAnalyzer.init(className + "/" + methodName, insns);
          analyzer.analyze(className, methodNode);

        } catch (NullPointerException | StackOverflowError | AnalyzerException e) {
          e.printStackTrace();
          continue;
        }
      }
      
      for(int i = 0 ; i < insns.length; i ++) {
        AbstractInsnNode ldcMark = new LdcInsnNode(BYTECODE_LDC_MARKER + i);
        methodNode.instructions.insertBefore(insns[i], ldcMark);
      }
    }
    
    accept(cv);
  }
}