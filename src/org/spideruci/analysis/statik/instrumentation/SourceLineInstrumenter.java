package org.spideruci.analysis.statik.instrumentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

public class SourceLineInstrumenter {
  public byte[] instrument(String className, byte[] bytecode, File classFile) {
    ClassReader cr = null;
    if(bytecode == null || bytecode.length == 0) {
      if(classFile == null) 
        throw new RuntimeException("classfile and byte array are empty.");
      try {
        FileInputStream in = new FileInputStream(classFile);
        cr = new ClassReader(in);
      } catch(IOException ioEx ) {
        StackTraceElement[] stackTrace = ioEx.getStackTrace();
        RuntimeException ioRunEx = new RuntimeException(ioEx.getMessage());
        ioRunEx.setStackTrace(stackTrace);
        throw ioRunEx;
      }
    }
    
    if((cr.getAccess() & ACC_INTERFACE) == ACC_INTERFACE) {
      return cr.b;
    }
    
    byte[] instrumentedBytecode = null;
    try {
      instrumentedBytecode = instrumentSourcelines(className, cr);
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
  
  public byte[] instrumentSourcelines(String className, ClassReader cr) {
    byte[] bytecode2 = null;
    
    if(cr == null) {
      throw new RuntimeException("cr (classreader) is null for className:" + className);
    }
        
    try {
      ClassWriter cw = new ClassWriter(0);
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
