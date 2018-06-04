package org.spideruci.analysis.statik.controlflow;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;
import org.spideruci.analysis.util.caryatid.MethodPrinter;

public class ControlFlowAnalyzer {
  
  ArrayList<AbstractInsnNode> leaders = new ArrayList<AbstractInsnNode>();
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Graph getCFG(String owner, MethodNode mn) throws AnalyzerException {

    Analyzer analyzer = new Analyzer(new BasicInterpreter()) {

      protected Frame newFrame(int nLocals, int nStack) {
        return new ByteNode<BasicValue>(nLocals, nStack);
      }

      protected Frame newFrame(Frame src) {
        return new ByteNode<BasicValue>(src);
      }

      protected void newControlFlowEdge(int src, int dst) {
        add_control_flow_edge(src, dst);
      }

      protected boolean newControlFlowExceptionEdge(int src, int dst) {
//        add_control_flow_edge(src, dst);
        return true;
      }

      private void add_control_flow_edge(int src, int dst) {
        ByteNode<BasicValue> s = (ByteNode<BasicValue>) getFrames()[src];
        s.successors.add((ByteNode<BasicValue>) getFrames()[dst]);
        if(!s.label.equals("")) {
          checkState(String.valueOf(src).equals(s.label));
        }
        s.label = String.valueOf(src);
        ByteNode<BasicValue> d = (ByteNode<BasicValue>) getFrames()[dst];
        d.predecessors.add((ByteNode<BasicValue>) getFrames()[src]);
        if(!d.label.equals("")) {
          checkState(String.valueOf(dst).equals(d.label));
        }
        d.label = String.valueOf(dst);
      }
    };

    analyzer.analyze(owner, mn);
    AbstractInsnNode[] insnArray = mn.instructions.toArray();
    Frame[] byte_nodes = analyzer.getFrames();
    checkState(insnArray.length == byte_nodes.length);

    Graph control_flow_graph = Graph.create();
    for(int i = 0; i < byte_nodes.length; i += 1) {
      if(byte_nodes[i] == null) continue;
      String label = ((ByteNode<Value>)byte_nodes[i]).label;

      if(!String.valueOf(i).equals(label)) {
        ((ByteNode<Value>)byte_nodes[i]).label = String.valueOf(i);
        String errMessage = String.valueOf(insnArray[i].getOpcode() + " " +
            insnArray.length);
        System.err.println(errMessage);
      }
      control_flow_graph.nowHas(label);
      if(i == 0) {
        control_flow_graph.startPointsTo
        (control_flow_graph.node(label));
      }

      int opcode = insnArray[i].getOpcode();

      if(opcode == Opcodes.RETURN ||
          opcode == Opcodes.IRETURN ||
          opcode == Opcodes.FRETURN ||
          opcode == Opcodes.LRETURN ||
          opcode == Opcodes.DRETURN ||
          opcode == Opcodes.ARETURN ||
          opcode == Opcodes.ATHROW) {
        Node temp = control_flow_graph.node(label);
        control_flow_graph.endIsPointedBy(temp);
      }

      if(opcode == Opcodes.INVOKESTATIC && 
          ((MethodInsnNode)insnArray[i]).name.equals("exit") &&
          ((MethodInsnNode)insnArray[i]).owner.equals("java/lang/System")) {
        //				control_flow_graph.setEndNodePredecessors
        //				(control_flow_graph.getNode(label));
        control_flow_graph.node(label).clearSuccessors();
      }
    }

    for(int i = 0; i < byte_nodes.length; i += 1) {
      if(byte_nodes[i] == null) continue;
      Node node = control_flow_graph.node(String.valueOf(i));
      Set<ByteNode<Value>> succs = ((ByteNode<Value>)byte_nodes[i]).successors;
      for(ByteNode succ : succs) {
        String succ_label = succ.label;
        node.pointsTo(control_flow_graph.node(succ_label));
      }
    }
    return control_flow_graph;
  }

  @SuppressWarnings("unchecked")
  public void printBasicBlocks(String owner, MethodNode mn, boolean withLeaders) 
      throws AnalyzerException {
    Analyzer a = new Analyzer(new BasicInterpreter()) {
      protected Frame newFrame(int nLocals, int nStack) {
        return new ByteNode<BasicValue>(nLocals, nStack);
      }
      protected Frame newFrame(Frame src) {
        return new ByteNode<BasicValue>(src);
      }
      protected void newControlFlowEdge(int src, int dst) {
        ByteNode<BasicValue> s = (ByteNode<BasicValue>) getFrames()[src];
        s.successors.add((ByteNode<BasicValue>) getFrames()[dst]);
        if(!s.label.equals("")) {
          checkState(String.valueOf(src).equals(s.label));
        }
        s.label = String.valueOf(src);
        ByteNode<BasicValue> d = (ByteNode<BasicValue>) getFrames()[dst];
        d.predecessors.add((ByteNode<BasicValue>) getFrames()[src]);
        if(!d.label.equals("")) {
          checkState(String.valueOf(dst).equals(d.label));
        }
        d.label = String.valueOf(dst);
      }
    };
    a.analyze(owner, mn);
    AbstractInsnNode[] insnArray = mn.instructions.toArray();
    Frame[] frames = a.getFrames();
    //get the leaders
    leaders.add(insnArray[0]); // First instruction of the method
    System.out.println(mn.name + "() {");
    if(withLeaders) {
      //get the other leaders
      findLeaders(frames, insnArray);
      printByteCodeWithLeaders(frames, insnArray);
    } else {
      printByteCode(frames, insnArray);
    }
    System.out.println("}\n");
  }

  private void printByteCode(Frame[] frames, AbstractInsnNode[] insnArray) {
    for(int count = 0; count < frames.length; count++) {
      if(frames[count] != null) { // avoids deadcode
        System.out.print("\t");
        insnArray[count].accept(new MethodPrinter(Opcodes.ASM4));
      }
    }
  }

  private void printByteCodeWithLeaders(Frame[] frames, AbstractInsnNode[] insnArray) {
    for(int count = 0; count < frames.length; count++) {
      if(frames[count] != null) { // avoids deadcode
        if(leaders.contains(insnArray[count])) {
          System.out.print("LEADER:");
        }
        System.out.print("\t");
        insnArray[count].accept(new MethodPrinter(Opcodes.ASM4));
        insnArray[count].getPrevious();
        
      }
    }
  }

  @SuppressWarnings({ "unchecked" })
  private void findLeaders(Frame[] frames, AbstractInsnNode[] insnArray) {
    for(int count = 1; count < frames.length; count++) {
      if(frames[count] != null) { // avoids deadcode
        switch(insnArray[count].getType()) {
        case AbstractInsnNode.JUMP_INSN:
          // Any instruction that immediately follows a jump
          leaders.add(insnArray[count + 1]);
          // Target of a jump
          leaders.add(((JumpInsnNode)insnArray[count]).label);
          break;
        case AbstractInsnNode.TABLESWITCH_INSN:
          // Any instruction that immediately follows a jump
          leaders.add(insnArray[count + 1]);

          // Targets of a switch
          TableSwitchInsnNode node1 = (TableSwitchInsnNode)insnArray[count]; 
          leaders.add(node1.dflt);
          for(LabelNode labelNode : (List<LabelNode>)node1.labels) {
            leaders.add(labelNode);
          }
          break;
        case AbstractInsnNode.LOOKUPSWITCH_INSN:
          // Any instruction that immediately follows a jump
          leaders.add(insnArray[count + 1]);

          // Targets of a switch
          LookupSwitchInsnNode node2 = (LookupSwitchInsnNode)insnArray[count]; 
          leaders.add(node2.dflt);
          for(LabelNode labelNode : (List<LabelNode>)node2.labels) {
            leaders.add(labelNode);
          }
          break;
        default:
        }
      }
    }
  }
}