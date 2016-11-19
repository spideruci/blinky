package org.spideruci.analysis.config.definer;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class ClinitAnalyzer extends PrototypicalClassNode {
  
  private final boolean debug;
  
  public ClinitAnalyzer(int classWriterFlags, String className) {
    super(classWriterFlags, className);
    this.debug = true;
  }
  
  public ClinitAnalyzer(int classWriterFlags, String className, boolean debug) {
    super(classWriterFlags, className);
    this.debug = debug;
  }

  @Override 
  public void visitEnd() {
    if(debug) {
      System.out.println(this.className);
    }

    for(MethodNode methodNode : this.methods) {
      
      if(!methodNode.name.equals("<clinit>")) {
        continue;
      }
      
      AbstractInsnNode[] insns = methodNode.instructions.toArray();
      for(AbstractInsnNode i : insns) {
        System.out.println(i.toString());
      }
      
      System.out.println("~~~");
      
      try {
        
        Analyzer<InsnValue> a = new Analyzer<>(new InsnInterpreter());
        a.analyze(className, methodNode);
        Frame<InsnValue>[] frames = a.getFrames();
        
        int i = 0;
        for(Frame<InsnValue> frame : frames) {
          AbstractInsnNode insn = insns[i];
          System.out.println(i + " " + insn.toString() + " " + frame.toString());
          i += 1;
        }
        
        
        
      } catch (NullPointerException | StackOverflowError e) {
        e.printStackTrace();
        continue;
      } catch(AnalyzerException e) {
        System.out.println("OH FUCK~! " +  e.node.toString());
        e.printStackTrace();
        continue;
      }
      
    }
    
    accept(cv);
  }

}


