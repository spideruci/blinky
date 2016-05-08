package org.spideruci.analysis.statik.instrumentation.zerooperand;

import org.objectweb.asm.Opcodes;

public class ZeroOperandSwitcher implements Opcodes {

  private final ZeroOperandSwitchListerner listener;

  public ZeroOperandSwitcher(ZeroOperandSwitchListerner listerner) {
    this.listener = listerner;
  }

  public void svitch(final int opcode) {
    switch(opcode) {
    case NOP:
      return;
    case ACONST_NULL:
    case ICONST_M1:
    case ICONST_0:
    case ICONST_1:
    case ICONST_2:
    case ICONST_3:
    case ICONST_4:
    case ICONST_5:
    case FCONST_0:
    case FCONST_1:
    case FCONST_2:
    case LCONST_0:
    case LCONST_1:
    case DCONST_0:
    case DCONST_1:
      listener.onConstantLoad(opcode);
      break;
    case IALOAD:
    case FALOAD:
    case BALOAD:
    case CALOAD:
    case SALOAD:
      listener.onPrimitiveArrayLoad(opcode);
      break;
    case LALOAD:
    case DALOAD:
      listener.onWidePrimitiveArrayLoad(opcode);
      break;
    case AALOAD:
      listener.onReferenceArrayLoad(opcode);
      break;
    case LASTORE:
    case DASTORE:
      listener.onWidePrimitiveArrayStore(opcode);
      break;
    case IASTORE:
    case FASTORE:
    case BASTORE:
    case CASTORE:
    case SASTORE:
      listener.onPrimitiveArrayStore(opcode);
      break;
    case AASTORE:
      listener.onReferenceArrayStore(opcode);
      break;
    case POP:
    case POP2:
    case DUP:
    case DUP_X1:
    case DUP_X2:
    case DUP2:
    case DUP2_X1:
    case DUP2_X2:
    case SWAP:
      listener.onStackManipulation(opcode);
      break;
    case IADD:
    case LADD:
    case FADD:
    case DADD:
    case ISUB:
    case LSUB:
    case FSUB:
    case DSUB:
    case IMUL:
    case LMUL:
    case FMUL:
    case DMUL:
    case IDIV:
    case LDIV:
    case FDIV:
    case DDIV:
    case IREM:
    case LREM:
    case FREM:
    case DREM:
    case INEG:
    case LNEG:
    case FNEG:
    case DNEG:
    case ISHL:
    case LSHL:
    case ISHR:
    case LSHR:
    case IUSHR:
    case LUSHR:
    case IAND:
    case LAND:
    case IOR:
    case LOR:
    case IXOR:
    case LXOR:
      listener.onMathBoolOrBit(opcode);
      break;
    case I2L:
    case I2F:
    case I2D:
    case L2I:
    case L2F:
    case L2D:
    case F2I:
    case F2L:
    case F2D:
    case D2I:
    case D2L:
    case D2F:
    case I2B:
    case I2C:
    case I2S:
      listener.onPrimitiveTypeConversion(opcode);
      break;
    case LCMP:
    case FCMPL:
    case FCMPG:
    case DCMPL:
    case DCMPG:
      listener.onComparison(opcode);
      break;
    case IRETURN:
    case LRETURN:
    case FRETURN:
    case DRETURN:
    case ARETURN:
    case RETURN:
      listener.onReturn(opcode);
      break;
    case ARRAYLENGTH:
      listener.onArraylength(opcode);
      break;
    case ATHROW:
      listener.onAthrow(opcode);
      break;
    case MONITORENTER:
    case MONITOREXIT:
      listener.onMonitor(opcode);
      break;
    }
  }
}
