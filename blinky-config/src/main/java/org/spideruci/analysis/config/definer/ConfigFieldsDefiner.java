package org.spideruci.analysis.config.definer;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ConfigFieldsDefiner extends PrototypicalClassAdapter {
  
  private final ConfigClassScanner configScanner;
  private final Map<String, ?> config;

  public static byte[] rewrite(String className, File classFile, Map<String, ?> config) {
    
    try {
      final int writerFlags = ClassWriter.COMPUTE_MAXS;
      
//      ClinitAnalyzer clinitAnalyzer = new ClinitAnalyzer(writerFlags, className);
//      ClassAdapterRunner clinitAnalysisRunner = 
//          ClassAdapterRunner.create(clinitAnalyzer, classFile);
//      
//      clinitAnalysisRunner.run();
      
      ConfigClassScanner configClassScanner = 
          new ConfigClassScanner(writerFlags, className);
      ClassAdapterRunner configScanRunner = 
          ClassAdapterRunner.create(configClassScanner, classFile);
      
      configScanRunner.run();
      
      if(config == null || config.isEmpty()) {
        return null;
      }
      
      for(String fieldName : config.keySet()) {
        if(fieldName == null || fieldName.isEmpty()) {
          continue;
        }
        
        if(!configClassScanner.containsPublicStaticField(fieldName)) {
          final String msg = String.format(
              "Public, satic field for config'ing %s is not declared in %s", 
              fieldName, 
              className);
          throw new RuntimeException(msg);
        }
        
        
        System.out.println(fieldName);
        if(configClassScanner.publicStaticFieldIsDefined(fieldName)) {
          System.out.println(fieldName);
          continue;
        }
        
        // TODO - we are only defining undefined fields;
        Object value = config.get(fieldName);
        final Type valueType = value == null ? null : toPrimitiveType(value.getClass());
        final Type fieldType = configClassScanner.getPublicStaticField(fieldName);
        
        if(!fieldType.equals(valueType)) {
          final String msg = String.format(
              "TYPE MISMATCH: Field for config'ing %s is declared as **%s** "
              + "in %s; but as **%s** in the config file.",
              fieldName,
              fieldType.getClassName(),
              className,
              valueType.getClassName());
          throw new RuntimeException(msg);
        }
        
      }
      
      ConfigFieldsDefiner adapter = 
          new ConfigFieldsDefiner(
              writerFlags, 
              className, 
              configClassScanner, 
              config);
      
      ClassAdapterRunner runner = ClassAdapterRunner.create(adapter, classFile);
      byte[] bytecode = runner.run();
      return bytecode;
      
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    
  }
  
  public static Type toPrimitiveType(Class<?> klass) {
    if(klass == Integer.class)
      return Type.INT_TYPE;
    
    if(klass == Float.class)
      return Type.FLOAT_TYPE;
    
    if(klass == Double.class)
      return Type.DOUBLE_TYPE;
    
    if(klass == Boolean.class)
      return Type.BOOLEAN_TYPE;
    
    if(klass == ArrayList.class)
      return Type.getType("[Ljava/lang/String;");
    
    throw new RuntimeException();
  }
  
  private ConfigFieldsDefiner(
      int classWriterFlags, 
      String className, 
      ConfigClassScanner configScanner,
      Map<String, ?> config) {
    super(classWriterFlags, className);
    this.configScanner = configScanner;
    this.config = config;
  }
  
  @Override
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    FieldVisitor fv = cv.visitField(access, name, desc, signature, value);
    
    if(fv != null 
        && (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
        && (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
        && configScanner.containsPublicStaticField(name)) {
    }
    
    return fv;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if(name.startsWith("<clinit>")) {
      System.out.println(className + " " + access + " " + name + " " + desc + " " + signature + " " + exceptions);
      MethodVisitor clinitrw = new ClinitRewriter(mv, className, config);
      return clinitrw;
    }
    
    return mv;
  }
  
  @Override 
  public void visitEnd() {
    if(!this.configScanner.containsClinit()) {
      MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
      MethodVisitor clinitrw = new ClinitRewriter(mv, className, config);
      clinitrw.visitEnd();
    }
    
    cv.visitEnd();
  }


}
