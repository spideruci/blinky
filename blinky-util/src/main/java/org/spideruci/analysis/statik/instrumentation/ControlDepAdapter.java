package org.spideruci.analysis.statik.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.spideruci.analysis.statik.controlflow.Algorithms;
import org.spideruci.analysis.statik.controlflow.ControlFlowAnalyzer;
import org.spideruci.analysis.statik.controlflow.Graph;

public class ControlDepAdapter extends ClassNode {
  private String className;
  private ClassVisitor cv;
  private boolean debug;
  public ControlDepAdapter(ClassVisitor cv, String className, boolean debug) {
    super(Opcodes.ASM4);
    this.className = className;
    this.cv = cv;
    this.debug = debug;
  }

  @SuppressWarnings("unchecked")
  @Override 
  public void visitEnd() {
    List<MethodNode> methods = this.methods;

    if(debug) {
      System.out.println(this.className);
    }

    for(MethodNode method_node : methods) {
      Graph domTree = null;
      Graph cfg = null;
      HashMap<String, ArrayList<String>> imm_post_dominators = null; 
      try {
        cfg = ControlFlowAnalyzer.getCFG(this.className, method_node);
        if(cfg == null) {
          return;
        }
        if(debug) {
          System.out.println(method_node.name);
          System.out.println(cfg);
        }
        domTree = Algorithms.computeDomTree(cfg.reverseEdges().startNode());
        imm_post_dominators = domTree.toMap();
      } catch (AnalyzerException e) {
        e.printStackTrace();
      } catch (NullPointerException | StackOverflowError nullExp) {
        imm_post_dominators = null;
      }

      AbstractInsnNode[] insns = method_node.instructions.toArray();
      for(int i = 0 ; i < insns.length; i ++) {
        String ipd;
        if(imm_post_dominators != null 
            && imm_post_dominators.containsKey(String.valueOf(i))) {
          ipd = imm_post_dominators.get(String.valueOf(i)).get(0);
        } else {
          ipd = "END";
        }

        while(true) {
          boolean _break = false;
          int index;
          try {
            index = Integer.parseInt(ipd);
          } catch(NumberFormatException ex) {
            break;
          }
          int type = insns[index].getType();
          switch(type) {
          case AbstractInsnNode.LABEL: 
          case AbstractInsnNode.FRAME: 
          case AbstractInsnNode.LINE:
            ipd = imm_post_dominators == null ? "END" : imm_post_dominators.get(String.valueOf(index)).get(0);
            break;
          default:
            _break = true;
          }
          if(_break) break;
        }
        MethodNode mn = get_printlog_insn_nodes("palepu@bytecode@" + i + "@ipd@" + ipd);
        method_node.instructions.insertBefore(insns[i], mn.instructions);
      }
    }
    accept(cv);
  }

  @SuppressWarnings({ "unused" })
  private MethodNode getMethodnode(String method_name) {
    List<MethodNode> methods = this.methods; 
    for(MethodNode mn : methods) {
      if(mn.name.equals(method_name)) {
        return mn;
      }
    }
    return null;
  }

  private MethodNode get_printlog_insn_nodes(String log_message) {
    MethodNode mn = new MethodNode();
    mn.visitLdcInsn(log_message);
    return mn;
  }
}