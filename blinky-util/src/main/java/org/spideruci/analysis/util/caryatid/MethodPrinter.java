package org.spideruci.analysis.util.caryatid;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


public class MethodPrinter extends MethodVisitor {
	static private int lineNumber = -1;
	
	public MethodPrinter(int api) {
		super(api);
	}
	
	public MethodPrinter(int api, MethodVisitor mv) {
		super(api, mv);
	}
	
	@Override
	public void visitCode() {
		mv.visitCode();
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		lineNumber = line;
		System.out.println(Helper.joinStrings(" ", "LINENUMBER", line, start));
	}
	
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), label));
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), owner, name, desc));
	}
	
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		System.out.println(Helper.joinStrings(" ", "FRAME", ByteCodeUtil.FrameTypeToText(type), nLocal, nStack));
	}
	
	@Override
	public void visitIincInsn(int var, int increment) {
		System.out.println(Helper.joinStrings(" ", "IINC", var, increment));
	}
	
	@Override
	public void visitInsn(int opcode) {
		System.out.println(ByteCodeUtil.opcodeToText(opcode));
	}
	
	@Override
	public void visitIntInsn(int opcode, int operand) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), operand));
	}
	
	@Override
	public void visitLabel(Label label) {
		System.out.println(Helper.joinStrings(" ", "LABEL", label));
	}
	@Override
	public void visitLdcInsn(Object cst) {
		System.out.println(Helper.joinStrings(" ", "LDC", cst));
	} 
	
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		System.out.println(Helper.joinStrings(" ", "LOCALVARIABLE", name, desc, signature, start, end, index));
	}
	
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		System.out.println(Helper.joinStrings(" ", "LOOKUPSWITCH:\n", "\t\t"+dflt));
		for(Label label : labels) {
			System.out.print("\t\t");
			System.out.println(label);
		}
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), owner, name, desc));
	}
	
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		System.out.println(Helper.joinStrings(" ", "MULTIANEWARRAY", desc, dims) + " ");
	}
	
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		System.out.println(Helper.joinStrings(" ", "TABLESWITCH:\n", "\t\t"+dflt));
		for(Label label : labels) {
			System.out.print("\t\t");
			System.out.println(label);
		}
	}
	
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		System.out.println(Helper.joinStrings(" ", "TRYCATCHBLOCK", start, end, handler, type));
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), type));
	}
	
	@Override
	public void visitVarInsn(int opcode, int var) {
		System.out.println(Helper.joinStrings(" ", ByteCodeUtil.opcodeToText(opcode), var));
	}
}