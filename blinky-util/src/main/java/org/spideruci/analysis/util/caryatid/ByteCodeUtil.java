package org.spideruci.analysis.util.caryatid;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ByteCodeUtil {

	public static String opcodeToText(int opcode) {
		switch(opcode) {
		case Opcodes.NOP: return "NOP";
		case Opcodes.INEG: return "INEG";
		case Opcodes.LNEG: return "LNEG";
		case Opcodes.FNEG: return "FNEG";
		case Opcodes.DNEG: return "DNEG";
		case Opcodes.ARRAYLENGTH: return "ARRAYLENGTH";
		case Opcodes.CHECKCAST: return "CHECKCAST";
		case Opcodes.I2B: return "I2B";
		case Opcodes.I2C: return "I2C";
		case Opcodes.I2S: return "I2S";
		case Opcodes.D2L: return "D2L";
		case Opcodes.L2D: return "L2D";
		case Opcodes.I2F: return "I2F";
		case Opcodes.F2I: return "F2I";
		case Opcodes.GOTO: return "GOTO";
		case Opcodes.IINC: return "IINC";
		case Opcodes.RET: return "RET";
		case Opcodes.JSR: return "JSR";
		case Opcodes.RETURN: return "RETURN";
		case Opcodes.ACONST_NULL: return "ACONST_NULL";
		case Opcodes.ICONST_M1: return "ICONST_M1";
		case Opcodes.ICONST_0: return "ICONST_0";
		case Opcodes.ICONST_1: return "ICONST_1";
		case Opcodes.ICONST_2: return "ICONST_2";
		case Opcodes.ICONST_3: return "ICONST_3";
		case Opcodes.ICONST_4: return "ICONST_4";
		case Opcodes.ICONST_5: return "ICONST_5";
		case Opcodes.BIPUSH: return "BIPUSH";
		case Opcodes.SIPUSH: return "SIPUSH";
		case Opcodes.LDC: return "LDC";
		case Opcodes.LCONST_0: return "LCONST_0";
		case Opcodes.LCONST_1: return "LCONST_1";
		case Opcodes.FCONST_0: return "FCONST_0";
		case Opcodes.FCONST_1: return "FCONST_1";
		case Opcodes.FCONST_2: return "FCONST_2";
		case Opcodes.DCONST_0: return "DCONST_0";
		case Opcodes.DCONST_1: return "DCONST_1";
		case Opcodes.ILOAD: return "ILOAD";
		case Opcodes.FLOAD: return "FLOAD";
		case Opcodes.ALOAD: return "ALOAD";
		case Opcodes.LLOAD: return "LLOAD";
		case Opcodes.DLOAD: return "DLOAD";
		case Opcodes.AALOAD: return "AALOAD";
		case Opcodes.IALOAD: return "IALOAD";
		case Opcodes.BALOAD: return "BALOAD";
		case Opcodes.CALOAD: return "CALOAD";
		case Opcodes.SALOAD: return "SALOAD";
		case Opcodes.FALOAD: return "FALOAD";
		case Opcodes.LALOAD: return "LALOAD";
		case Opcodes.DALOAD: return "DALOAD";
		case Opcodes.I2L: return "I2L";
		case Opcodes.F2L: return "F2L";
		case Opcodes.I2D: return "I2D";
		case Opcodes.F2D: return "F2D";
		case Opcodes.L2I: return "L2I";
		case Opcodes.D2I: return "D2I";
		case Opcodes.L2F: return "L2F";
		case Opcodes.D2F: return "D2F";
		case Opcodes.ISTORE: return "ISTORE";
		case Opcodes.FSTORE: return "FSTORE";
		case Opcodes.ASTORE: return "ASTORE";
		case Opcodes.LSTORE: return "LSTORE";
		case Opcodes.DSTORE: return "DSTORE";
		case Opcodes.IASTORE: return "IASTORE";
		case Opcodes.BASTORE: return "BASTORE";
		case Opcodes.CASTORE: return "CASTORE";
		case Opcodes.SASTORE: return "SASTORE";
		case Opcodes.FASTORE: return "FASTORE";
		case Opcodes.AASTORE: return "AASTORE";
		case Opcodes.LASTORE: return "LASTORE";
		case Opcodes.DASTORE: return "DASTORE";
		case Opcodes.POP: return "POP";
		case Opcodes.IFEQ: return "IFEQ";
		case Opcodes.IFNE: return "IFNE";
		case Opcodes.IFLT: return "IFLT";
		case Opcodes.IFGE: return "IFGE";
		case Opcodes.IFGT: return "IFGT";
		case Opcodes.IFLE: return "IFLE";
		case Opcodes.IFNULL: return "IFNULL";
		case Opcodes.IFNONNULL: return "IFNONNULL";
		case Opcodes.IF_ICMPEQ: return "IF_ICMPEQ";
		case Opcodes.IF_ICMPNE: return "IF_ICMPNE";
		case Opcodes.IF_ICMPLT: return "IF_ICMPLT";
		case Opcodes.IF_ICMPGE: return "IF_ICMPGE";
		case Opcodes.IF_ICMPGT: return "IF_ICMPGT";
		case Opcodes.IF_ICMPLE: return "IF_ICMPLE";
		case Opcodes.IF_ACMPEQ: return "IF_ACMPEQ";
		case Opcodes.IF_ACMPNE: return "IF_ACMPNE";
		case Opcodes.TABLESWITCH: return "TABLESWITCH";
		case Opcodes.LOOKUPSWITCH: return "LOOKUPSWITCH";
		case Opcodes.ATHROW: return "ATHROW";
		case Opcodes.MONITORENTER: return "MONITORENTER";
		case Opcodes.MONITOREXIT: return "MONITOREXIT";
		case Opcodes.POP2: return "POP2";
		case Opcodes.DUP: return "DUP";
		case Opcodes.DUP_X1: return "DUP_X1";
		case Opcodes.DUP_X2: return "DUP_X2";
		case Opcodes.DUP2: return "DUP2";
		case Opcodes.DUP2_X1: return "DUP2_X1";
		case Opcodes.DUP2_X2: return "DUP2_X2";
		case Opcodes.SWAP: return "SWAP";
		case Opcodes.IADD: return "IADD";
		case Opcodes.ISUB: return "ISUB";
		case Opcodes.IMUL: return "IMUL";
		case Opcodes.IDIV: return "IDIV";
		case Opcodes.IREM: return "IREM";
		case Opcodes.IAND: return "IAND";
		case Opcodes.IOR: return "IOR";
		case Opcodes.IXOR: return "IXOR";
		case Opcodes.ISHL: return "ISHL";
		case Opcodes.ISHR: return "ISHR";
		case Opcodes.IUSHR: return "IUSHR";
		case Opcodes.FCMPL: return "FCMPL";
		case Opcodes.FCMPG: return "FCMPG";
		case Opcodes.FADD: return "FADD";
		case Opcodes.FSUB: return "FSUB";
		case Opcodes.FMUL: return "FMUL";
		case Opcodes.FDIV: return "FDIV";
		case Opcodes.FREM: return "FREM";
		case Opcodes.LADD: return "LADD";
		case Opcodes.LSUB: return "LSUB";
		case Opcodes.LMUL: return "LMUL";
		case Opcodes.LDIV: return "LDIV";
		case Opcodes.LREM: return "LREM";
		case Opcodes.LAND: return "LAND";
		case Opcodes.LOR: return "LOR";
		case Opcodes.LXOR: return "LXOR";
		case Opcodes.DADD: return "DADD";
		case Opcodes.DSUB: return "DSUB";
		case Opcodes.DMUL: return "DMUL";
		case Opcodes.DDIV: return "DDIV";
		case Opcodes.DREM: return "DREM";
		case Opcodes.LSHL: return "LSHL";
		case Opcodes.LSHR: return "LSHR";
		case Opcodes.LUSHR: return "LUSHR";
		case Opcodes.LCMP: return "LCMP";
		case Opcodes.DCMPL: return "DCMPL";
		case Opcodes.DCMPG: return "DCMPG";
		case Opcodes.GETSTATIC: return "GETSTATIC";
		case Opcodes.PUTSTATIC: return "PUTSTATIC";
		case Opcodes.GETFIELD: return "GETFIELD";
		case Opcodes.INVOKEINTERFACE: return "INVOKEINTERFACE";
		case Opcodes.INVOKESPECIAL: return "INVOKESPECIAL";
		case Opcodes.INVOKEVIRTUAL: return "INVOKEVIRTUAL";
		case Opcodes.INVOKESTATIC: return "INVOKESTATIC";
		case Opcodes.IRETURN: return "IRETURN";
		case Opcodes.FRETURN: return "FRETURN";
		case Opcodes.ARETURN: return "ARETURN";
		case Opcodes.LRETURN: return "LRETURN";
		case Opcodes.DRETURN: return "DRETURN";
		case Opcodes.NEW: return "NEW";
		case Opcodes.ANEWARRAY: return "ANEWARRAY";
		case Opcodes.NEWARRAY: return "NEWARRAY";
		case Opcodes.INSTANCEOF: return "INSTANCEOF";
		case Opcodes.MULTIANEWARRAY: return "MULTIANEWARRAY";
		default: return "NA(" + opcode + ")";
		}
	}
	
	public static String FrameTypeToText(int type) {
		switch(type) {
		case Opcodes.F_APPEND: return "F_APPEND";
		case Opcodes.F_CHOP: return "F_CHOP";
		case Opcodes.F_FULL: return "F_FULL";
		case Opcodes.F_NEW: return "F_NEW:";
		case Opcodes.F_SAME: return "F_SAME";
		case Opcodes.F_SAME1: return "F_SAME1";
		default: return "NA";
		}
	}

	public static String TypeToText(int type) {
		switch(type) {
		case Type.ARRAY: return "ARRAY";
		case Type.BOOLEAN: return "BOOLEAN";
		case Type.BYTE: return "BYTE";
		case Type.CHAR: return "CHAR";
		case Type.DOUBLE: return "DOUBLE";
		case Type.FLOAT: return "FLOAT";
		case Type.INT: return "INT";
		case Type.LONG: return "LONG";
		case Type.METHOD: return "METHOD";
		case Type.OBJECT: return "OBJECT";
		case Type.SHORT: return "SHORT";
		case Type.VOID: return "VOID";
		default: return "NA";
		}
	}
}
