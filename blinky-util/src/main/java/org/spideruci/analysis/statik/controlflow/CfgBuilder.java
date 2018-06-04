package org.spideruci.analysis.statik.controlflow;

import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;
import org.spideruci.analysis.util.MyAssert;
import org.spideruci.analysis.util.caryatid.ByteCodeUtil;

public class CfgBuilder extends Analyzer {
  
  public static CfgBuilder init() {
    return new CfgBuilder(new BasicInterpreter());
  }

  public CfgBuilder(BasicInterpreter interpreter) {
    super(interpreter);
  }
  
  protected Frame newFrame(int nLocals, int nStack) {
    return new ByteNode<BasicValue>(nLocals, nStack);
  }

  protected Frame newFrame(Frame src) {
    return new ByteNode<BasicValue>(src);
  }

  protected void newControlFlowEdge(int src, int dst) {
    addControlFlowEdge(src, dst);
  }

  protected boolean newControlFlowExceptionEdge(int src, int dst) {
    addControlFlowEdge(src, dst);
    return true;
  }

  @SuppressWarnings("unchecked")
  private void addControlFlowEdge(int src, int dst) {
    ByteNode<BasicValue> s = (ByteNode<BasicValue>) getFrames()[src];
    ByteNode<BasicValue> d = (ByteNode<BasicValue>) getFrames()[dst];
    
    if(s == null || d == null) {
      return;
    }
    
    s.successors.add(d);
    if(!s.label.equals("")) {
      MyAssert.assertThat(String.valueOf(src).equals(s.label));
    }
    s.label = String.valueOf(src);
    
    
    d.predecessors.add(s);
    if(!d.label.equals("")) {
      MyAssert.assertThat(String.valueOf(dst).equals(d.label));
    }
    d.label = String.valueOf(dst);
  }
  
  @SuppressWarnings("unchecked")
  public Graph<String> buildGraph(String owner, MethodNode mn) throws AnalyzerException {
    this.analyze(owner, mn);
    AbstractInsnNode[] insnArray = mn.instructions.toArray();
    Frame[] byte_nodes = this.getFrames();
    checkState(insnArray.length == byte_nodes.length);

    Graph<String> controlFlowGraph = Graph.create();
    for(int i = 0; i < byte_nodes.length; i += 1) {
      if(byte_nodes[i] == null) continue;
      String label = ((ByteNode<Value>)byte_nodes[i]).label;

      if(!String.valueOf(i).equals(label)) {
        ((ByteNode<Value>)byte_nodes[i]).label = String.valueOf(i);
        String errMessage = String.valueOf(insnArray[i].getOpcode() + " " +
            insnArray.length);
        System.err.println(errMessage);
      }
      
      final int opcode = insnArray[i].getOpcode();
      final int type = insnArray[i].getType();
      controlFlowGraph.nowHas(label, ByteCodeUtil.opcodeToText(opcode) + '~' + type);
      if(i == 0) {
        controlFlowGraph.startPointsTo(controlFlowGraph.node(label));
      }
      
      if(opcode == Opcodes.RETURN ||
          opcode == Opcodes.IRETURN ||
          opcode == Opcodes.FRETURN ||
          opcode == Opcodes.LRETURN ||
          opcode == Opcodes.DRETURN ||
          opcode == Opcodes.ARETURN ||
          opcode == Opcodes.ATHROW) {
        Node<String> temp = controlFlowGraph.node(label);
        controlFlowGraph.endIsPointedBy(temp);
      }

      if(opcode == Opcodes.INVOKESTATIC && 
          ((MethodInsnNode)insnArray[i]).name.equals("exit") &&
          ((MethodInsnNode)insnArray[i]).owner.equals("java/lang/System")) {
        //        control_flow_graph.setEndNodePredecessors
        //        (control_flow_graph.getNode(label));
        controlFlowGraph.node(label).clearSuccessors();
      }
    }

    for(int i = 0; i < byte_nodes.length; i += 1) {
      if(byte_nodes[i] == null) continue;
      Node<String> node = controlFlowGraph.node(String.valueOf(i));
      Set<ByteNode<Value>> succs = ((ByteNode<Value>)byte_nodes[i]).successors;
      for(ByteNode<Value> succ : succs) {
        String succ_label = succ.label;
        node.pointsTo(controlFlowGraph.node(succ_label));
      }
    }
    return controlFlowGraph;
  }

}
