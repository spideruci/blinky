package org.spideruci.analysis.config.definer;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * Holding Instruction type to indicate that the {@link InsnValue} is actually
 * pointing to two AbstractInsnNodes.
 * @author vpalepu
 *
 */
public class MergeInsnNode extends AbstractInsnNode {
  
  private final AbstractInsnNode n1;
  private final AbstractInsnNode n2;
  
  public MergeInsnNode(AbstractInsnNode n1, AbstractInsnNode n2) {
    super(-1);
    this.n1 = n1;
    this.n2 = n2;
  }

  @Override
  public int getType() {
    return -1;
  }

  @Override
  public void accept(MethodVisitor cv) {
    // Do nothing. This kind of insn will not be part of an actual class.
  }

  @Override
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
    // TODO Auto-generated method stub
    return new MergeInsnNode(n1, n2);
  }
  
  public AbstractInsnNode n1() {
    return n1;
  }
  
  public AbstractInsnNode n2() {
    return n2;
  }

}
