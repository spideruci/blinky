package org.spideruci.analysis.config.definer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

public class ClassAdapterRunner {
  
  private final ClassReader classReader;
  private final ClassVisitor classAdapter;
  private final ClassWriter classWriter;
  
  public static ClassAdapterRunner create(
      PrototypicalClassAdapter classAdapter, File classFile) {
    
    ClassWriter cw = classAdapter.getClassWriter();
    ClassVisitor ca = classAdapter;
    ClassReader cr = getClassReader(classFile);
    
    ClassAdapterRunner runner = new ClassAdapterRunner(cr, ca, cw);
    return runner;
  }
  
  public static ClassAdapterRunner create(
      PrototypicalClassAdapter classAdapter, 
      byte[] bytecode) {
    
    ClassWriter cw = classAdapter.getClassWriter();
    ClassVisitor ca = classAdapter;
    ClassReader cr = new ClassReader(bytecode);
    
    ClassAdapterRunner runner = new ClassAdapterRunner(cr, ca, cw);
    return runner;
  }
  
  public static ClassAdapterRunner create(
      PrototypicalClassNode classAdapter, 
      File classFile) {
    
    ClassWriter cw = classAdapter.getClassWriter();
    ClassVisitor ca = classAdapter;
    ClassReader cr = getClassReader(classFile);
    
    ClassAdapterRunner runner = new ClassAdapterRunner(cr, ca, cw);
    return runner;
  }
  
  /**
   * @param bytecode
   * @param classFile
   * @return
   */
  private static ClassReader getClassReader(File classFile) {
    if(classFile == null) {
      throw new RuntimeException("classfile is null.");
    }

    try {
      FileInputStream in = new FileInputStream(classFile);
      ClassReader cr = new ClassReader(in);
      return cr;
    } catch(IOException ioEx ) {
      ioEx.printStackTrace();
      throw new RuntimeException(ioEx.getMessage());
    }
  }
  
  public ClassAdapterRunner(ClassReader cr, ClassVisitor ca, ClassWriter cw) {
    this.classReader = cr;
    this.classAdapter = ca;
    this.classWriter = cw;
  }
  
  /**
   * Adapts, i.e. transforms, the classfile based off the given class adapter, 
   * and returns the adapted, i.e. transformed, classfile.
   *   
   * @return the bytecode for the adapted classfile.
   */
  public byte[] run() {
    byte[] bytecode = this.classReader.b;
    
    try {
      classReader.accept(classAdapter, ClassReader.EXPAND_FRAMES);
      bytecode = classWriter.toByteArray();
      checkBytecode(bytecode);
      return bytecode;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  private static boolean debug = false;
  private void checkBytecode(byte[] bytecode2) {
    if(!debug) {
      return;
    }
    
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    CheckClassAdapter.verify(new ClassReader(bytecode2), false, pw);
    if(sw.toString().length() != 0) {
      System.err.println(sw.toString());
      throw new RuntimeException();
    }
  }

}
