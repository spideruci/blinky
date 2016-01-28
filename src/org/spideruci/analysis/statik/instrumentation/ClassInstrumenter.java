package org.spideruci.analysis.statik.instrumentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

import org.spideruci.analysis.statik.instrumentation.ControlDepAdapter;

public class ClassInstrumenter {
  
  public static boolean FRAMES = false;
  
  public byte[] instrument(String className, byte[] bytecode, File classFile) {
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
    
    if((cr.getAccess() & ACC_INTERFACE) == ACC_INTERFACE) {
      return cr.b;
    }
    
    byte[] instrumentedBytecode = null;
    try {
      instrumentedBytecode = instrumentBytecode(className, cr);
      checkBytecode(instrumentedBytecode);
    } catch(RuntimeException rex) {
      rex.printStackTrace();
      throw rex;
    } catch(Exception e) { 
      e.printStackTrace();
      throw e;
    }

    return instrumentedBytecode;
  }
  
  protected byte[] instrumentControlProbes(String className, byte[] bytecode) 
      throws Exception {
    byte[] bytecode2 = null;
    try {
      //1. read byte-code
      ClassReader cr = new ClassReader(bytecode);
      
      if((cr.getAccess() & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE) {
        return bytecode;
      }
      // 2. prepare to write byte-code
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); 
      // 3. adapter for the class writer above to deploy the Snitch in the class
      ControlDepAdapter classAd = new ControlDepAdapter(cw, className);
      cr.accept(classAd, ClassReader.EXPAND_FRAMES);//4. deploy the Snitch
      bytecode2 = cw.toByteArray();
      checkBytecode(bytecode2);
    }
    catch(Exception e) {
      throw e;
    }

    return bytecode2;
  }
  
  protected byte[] instrumentBytecode(String className, ClassReader cr) {
    byte[] bytecode2 = null;
    
    if(cr == null) {
      throw new RuntimeException("cr (classreader) is null for className:" + className);
    }
        
    try {
      ClassWriter cw = FRAMES ? new ClassWriter(ClassWriter.COMPUTE_FRAMES)
          : new ClassWriter(ClassWriter.COMPUTE_MAXS);
      SourceLineAdapter sourcelineAdapter = new SourceLineAdapter(cw, className);
      cr.accept(sourcelineAdapter, ClassReader.EXPAND_FRAMES);
      bytecode2 = cw.toByteArray();
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
}
