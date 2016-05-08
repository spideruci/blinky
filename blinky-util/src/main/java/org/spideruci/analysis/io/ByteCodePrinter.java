package org.spideruci.analysis.io;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;


public class ByteCodePrinter {
	
	private static final String bytecodePrintPath = 
			"/home/vijay/ProgramAnalysis/TestLogs/CollectedClasses/";
	
	public static void textify_ClassFile(boolean debug, String classfile_path) {
		String[] newArg = { debug ? "-debug" : classfile_path, classfile_path	};
		try { Textifier.main(newArg); } 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void textify_ByteCode(boolean debug, byte[] byte_code, 
			String class_name) {
		printToFile(class_name, byte_code, null);
		textify_ClassFile(debug, bytecodePrintPath + class_name + ".class");
	}
	
	public static void printToFile(String className, byte[] bytes, byte[] iBytes) {
		if(bytes != null) bytes_toFile(bytes, className);
		if(iBytes != null) bytes_toFile(iBytes, className + "2");
	}
	
	/**
	 * @param bytes
	 * @param class_name
	 * this is the name of the file, not the full-name, to which the bytecode
	 * will be writen to.
	 * Will not work if null.
	 */
	private static void bytes_toFile(byte[] bytes, String class_name) {
		if(bytes == null || bytes.length == 0) return;
		
		String file_pathname = bytecodePrintPath + 
				class_name.replace('/', '+').replace('$', '-') + ".class";
		File file = new File(file_pathname);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			out.close();
		} catch (Exception e) {	}
	}
	
//	@Deprecated
	public static void main(String[] args) throws Exception {
		String[] newArg = {
				"-debug", 
				"/home/vijay/phd-open-source/iota-test/bin/iota/tests/ControlStructureTests.class"};
		
		Textifier.main(newArg);
		System.out.println("-----------------------------------------------------");
		ASMifier.main(newArg);
	}
}
