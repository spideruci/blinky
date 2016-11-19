package org.spideruci.analysis.config.definer;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

public class InsnInterpreter extends Interpreter<InsnValue>  {
  
  private final BasicInterpreter basicIntx;

  protected InsnInterpreter() {
    super(Opcodes.ASM5);
    basicIntx = new BasicInterpreter();
  }

  @Override
  public InsnValue newValue(Type type) {
    
    if (type == null) {
      return InsnValue.UNINITIALIZED_VALUE;
    }
    switch (type.getSort()) {
    case Type.VOID:
      return null;
    case Type.BOOLEAN:
    case Type.CHAR:
    case Type.BYTE:
    case Type.SHORT:
    case Type.INT:
      return InsnValue.INT_VALUE;
    case Type.FLOAT:
      return InsnValue.FLOAT_VALUE;
    case Type.LONG:
      return InsnValue.LONG_VALUE;
    case Type.DOUBLE:
      return InsnValue.DOUBLE_VALUE;
    case Type.ARRAY:
    case Type.OBJECT:
      return InsnValue.REFERENCE_VALUE;
    default:
      throw new Error("Internal error");
    }
  }

  @Override
  public InsnValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
    BasicValue bVal = basicIntx.newOperation(insn);
    
    InsnValue iVal = InsnValue.create(bVal, insn);
    return iVal;
  }

  @Override
  public InsnValue copyOperation(AbstractInsnNode insn, InsnValue value) throws AnalyzerException {
    BasicValue bVal = basicIntx.copyOperation(insn, value);
    
    InsnValue iVal = InsnValue.create(bVal, insn, value);
    return iVal;
  }

  @Override
  public InsnValue unaryOperation(AbstractInsnNode insn, InsnValue value) throws AnalyzerException {
    BasicValue bVal = basicIntx.unaryOperation(insn, value);
    
    InsnValue iVal = InsnValue.create(bVal, insn, value);
    return iVal;
  }

  @Override
  public InsnValue binaryOperation(AbstractInsnNode insn, InsnValue value1, InsnValue value2) throws AnalyzerException {
    BasicValue bVal = basicIntx.binaryOperation(insn, value1, value2);
    
    InsnValue iVal = InsnValue.create(bVal, insn, value1, value2);
    return iVal;
  }

  @Override
  public InsnValue ternaryOperation(AbstractInsnNode insn, InsnValue value1, InsnValue value2, InsnValue value3)
      throws AnalyzerException {
    BasicValue bVal = basicIntx.ternaryOperation(insn, value1, value2, value3);
    
    InsnValue iVal = InsnValue.create(bVal, insn, value1, value2, value3);
    return iVal;
  }

  @Override
  public InsnValue naryOperation(AbstractInsnNode insn, List<? extends InsnValue> values) throws AnalyzerException {
    BasicValue bVal = basicIntx.naryOperation(insn, values);
    
    InsnValue iVal = InsnValue.create(bVal, insn, values.toArray(new InsnValue[0]));
    return iVal;
  }

  @Override
  public void returnOperation(AbstractInsnNode insn, InsnValue value, InsnValue expected) throws AnalyzerException {
  }

  @Override
  public InsnValue merge(InsnValue v, InsnValue w) {
    if (!(v.getType().equals(w.getType()) && v.insn().equals(w.insn()))) {
      return InsnValue.UNINITIALIZED_VALUE;
    }
    
    return v;
  }

}
