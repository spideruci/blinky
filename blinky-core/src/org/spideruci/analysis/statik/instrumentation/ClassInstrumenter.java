package org.spideruci.analysis.statik.instrumentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.spideruci.analysis.dynamic.Profiler;

public class ClassInstrumenter {
  
  public static boolean FRAMES = false;
  public static boolean CONTROL_FLOW = false;
  
  public byte[] instrument(String className, byte[] bytecode, File classFile) {
    ClassReader cr = getClassReader(bytecode, classFile);
    
    if((cr.getAccess() & ACC_INTERFACE) == ACC_INTERFACE) {
      return cr.b;
    }
    
    byte[] bytecode2 = cr.b;
    
    try {
      
      {
        bytecode2 = writeProbes(className, bytecode2, true /*isControlAdapter*/);
        checkBytecode(bytecode2);
      }
      
      {
        bytecode2 = writeProbes(className, bytecode2, false /*isControlAdapter*/);
        checkBytecode(bytecode2);
      }
    } catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
    
    return bytecode2;
  }
  
  protected byte[] writeProbes(String className, byte[] bytecode, 
      boolean isControlAdapter) {
    
    byte[] bytecode2 = null;
    ClassReader cr = new ClassReader(bytecode);
    
    try {
      ClassWriter cw;
      ClassVisitor classAdapter;
      
      if(isControlAdapter) {
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classAdapter = new ControlDepAdapter(cw, className, false);
      } else {
        cw = FRAMES ? new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            : new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classAdapter = new BytecodeClassAdapter(cw, className);
      }
      
      cr.accept(classAdapter, ClassReader.EXPAND_FRAMES);
      bytecode2 = cw.toByteArray();
      checkBytecode(bytecode2);
      
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    
    return bytecode2;
  }
  
    private static boolean debug = false;
    private void checkBytecode(byte[] bytecode2) {
      if(!debug) return;
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      CheckClassAdapter.verify(new ClassReader(bytecode2), false, pw);
      if(sw.toString().length() != 0) {
        System.err.println(sw.toString());
        throw new RuntimeException();
      }
    }
    
    /**
     * @param bytecode
     * @param classFile
     * @return
     */
    private ClassReader getClassReader(byte[] bytecode, File classFile) {
      ClassReader cr = null;
      if(bytecode == null || bytecode.length == 0) {
        if(classFile == null) 
          throw new RuntimeException("classfile and byte array are empty.");
        try {
          FileInputStream in = new FileInputStream(classFile);
          cr = new ClassReader(in);
        } catch(IOException ioEx ) {
          ioEx.printStackTrace();
          RuntimeException ioRunEx = new RuntimeException(ioEx.getMessage());
          throw ioRunEx;
        }
      } else {
        cr = new ClassReader(bytecode);
      }
      return cr;
    }
}
