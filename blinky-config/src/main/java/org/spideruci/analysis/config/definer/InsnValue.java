package org.spideruci.analysis.config.definer;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;

public class InsnValue extends BasicValue {
  
  public static final InsnValue UNINITIALIZED_VALUE = 
      InsnValue.create((Type)null, null);

  public static final InsnValue INT_VALUE = 
      InsnValue.create(Type.INT_TYPE, null);

  public static final InsnValue FLOAT_VALUE = 
      InsnValue.create(Type.FLOAT_TYPE, null);

  public static final InsnValue LONG_VALUE = 
      InsnValue.create(Type.LONG_TYPE, null);

  public static final InsnValue DOUBLE_VALUE = 
      InsnValue.create(Type.DOUBLE_TYPE, null);

  public static final InsnValue REFERENCE_VALUE = 
      InsnValue.create(Type.getObjectType("java/lang/Object"), null);

  public static final InsnValue RETURNADDRESS_VALUE = 
      InsnValue.create(Type.VOID_TYPE, null);
  
  public static InsnValue merge(BasicValue bVal, InsnValue val1, InsnValue val2) {
    
    AbstractInsnNode mergeInsn = new MergeInsnNode(val1.instruction, val2.instruction);
    InsnValue mergedInsnValue = InsnValue.create(bVal.getType(), mergeInsn, val1, val2);
    return mergedInsnValue;
  }
  
  public static InsnValue create(BasicValue bVal, AbstractInsnNode insn, InsnValue ... arguments) {
    return (bVal == null) ? null : InsnValue.create(bVal.getType(), insn, arguments);
  }

  private final AbstractInsnNode instruction;
  private final InsnValue[] arguments; // should this be instructionValue?
  
  public static InsnValue create(
      Type type, 
      AbstractInsnNode insn, 
      InsnValue ... arguments) {
    
    int argcount = arguments.length;
    InsnValue[] args = new InsnValue[argcount];
    int idx = 0;
    for(InsnValue n : arguments) {
      args[idx] = n;
      idx += 1;
    }
    
    InsnValue value = new InsnValue(type, insn, arguments);
    return value;
  }
  
  private InsnValue(Type type, AbstractInsnNode insn, InsnValue[] args) {
    super(type);
    this.instruction = insn;
    this.arguments = args;
  }
  
  public AbstractInsnNode insn() {
    return instruction;
  }
  
  public InsnValue[] getArguments() {
    return arguments;
  }
  
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer().append(super.toString());
    for(InsnValue val : arguments) {
      buffer.append(":").append(Integer.toHexString(val.instruction.hashCode()));
    }
    
    return buffer.toString();
    
  }

}
