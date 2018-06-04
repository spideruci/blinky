package org.spideruci.analysis.statik.controlflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class ClassCfgBuilder extends ClassNode {
  
  private final String className;
  
  public ClassCfgBuilder(String className) {
    super(Opcodes.ASM4);
    this.className = className;
  }

  public static void main(String[] args) {
    try {
      ClassReader cr;
      final String classfilePath = "/Users/vpalepu/phd-open-source/blinky/target/test-classes/org/spideruci/analysis/dynamic/tests/ControlStructureTests.class";
      try {
        File classfile = new File(classfilePath);
        FileInputStream in = new FileInputStream(classfile);
        cr = new ClassReader(in);
      } catch(IOException ioEx ) {
        ioEx.printStackTrace();
        RuntimeException ioRunEx = new RuntimeException(ioEx.getMessage());
        throw ioRunEx;
      }
      
      if((cr.getAccess() & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE) {
        return;
      }
      
      final String[] classfilePathSplit = classfilePath.split("/");
      final String className = classfilePathSplit[classfilePathSplit.length - 1];
      ClassCfgBuilder classAd = new ClassCfgBuilder(className);
      cr.accept(classAd, ClassReader.EXPAND_FRAMES);
    } catch(Exception e) {
      throw e;
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override 
  public void visitEnd() {
    System.out.println(className);
    List<MethodNode> methods = this.methods;

    CfgBuilder cfgBuilder = CfgBuilder.init();
    
    for(MethodNode method_node : methods) {
      System.out.println("\t" + method_node.name);
      try {
        Graph<String> graph = cfgBuilder.buildGraph(className, method_node);
        System.out.println(graph.toString());
      } catch (AnalyzerException e) {
        e.printStackTrace();
      }
    }
    
  }

}
