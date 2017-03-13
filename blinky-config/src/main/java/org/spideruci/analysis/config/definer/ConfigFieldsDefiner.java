package org.spideruci.analysis.config.definer;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spideruci.analysis.config.Main;

public class ConfigFieldsDefiner extends PrototypicalClassAdapter {
  
  private final ConfigClassScanner configScanner;
  private final Map<String, ?> config;

  private static ConfigClassScanner scanConfigurableClass(final String className, final File classFile) {
    final int writerFlags = ClassWriter.COMPUTE_MAXS;
    ConfigClassScanner configClassScanner = 
        new ConfigClassScanner(writerFlags, className);
    ClassAdapterRunner configScanRunner = 
        ClassAdapterRunner.create(configClassScanner, classFile);
    
    configScanRunner.run();
    return configClassScanner;
  }
  
  private static void checkConfig(
      String className, 
      Map<String, ?> config, 
      ConfigClassScanner configClassScanner) {
    for(String configableFieldName : config.keySet()) {
      if(configableFieldName == null || configableFieldName.isEmpty()) {
        continue;
      }
      
      if(!configClassScanner.containsPublicStaticField(configableFieldName)) {
        final String msg = String.format(
            "Public, satic field for config'ing %s is not declared in %s", 
            configableFieldName, 
            className);
        throw new RuntimeException(msg);
      }
      
      Log log = Main.getLog();
      log.debug(String.format("Public, static field %s present in %s", 
          configableFieldName, className));
      
      if(configClassScanner.publicStaticFieldIsDefined(configableFieldName)) {
        log.debug(String.format("Public, static field %s already defined in %s", 
            configableFieldName, className));
        continue;
      }
      
      log.debug(String.format("Public, static field %s not yet defined in %s", 
          configableFieldName, className));
      
      // TODO - we are only defining undefined fields;
      Object value = config.get(configableFieldName);
      final Type valueType = value == null ? null : toPrimitiveType(value.getClass());
      final Type fieldType = configClassScanner.getPublicStaticField(configableFieldName);
      
      if(!fieldType.equals(valueType)) {
        System.out.println(value);
        final String msg = String.format(
            "TYPE MISMATCH: Field for config'ing %s is declared as%n"
            + "**%s** in %s; but as %n"
            + "**%s** in the config file.",
            configableFieldName, fieldType.getClassName(),
            className, valueType.getClassName());
        log.error(msg);
        System.exit(1);
      }
    }
  }
  
  public static byte[] define(String className, File classFile, Map<String, ?> config) {
    if(className == null || classFile == null || config == null || config.isEmpty()) {
      return null;
    }
    
    try {
      ConfigClassScanner configClassScanner = scanConfigurableClass(className, classFile);
      checkConfig(className, config, configClassScanner);
      
      final int writerFlags = ClassWriter.COMPUTE_MAXS;
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
    
    if(klass == String.class)
      return Type.getType(String.class);
    
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
    FieldVisitor fv = null;
    
    if(config.containsKey(name)
        && (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
        && (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
      
      final Type type = Type.getType(desc);
      final int typeSort = type.getSort();

      boolean typeIsPrimitiveOrString = 
          typeSort == Type.BOOLEAN
          || typeSort == Type.BYTE
          || typeSort == Type.CHAR
          || typeSort == Type.DOUBLE
          || typeSort == Type.INT
          || typeSort == Type.FLOAT
          || typeSort == Type.LONG
          || typeSort == Type.SHORT
          || type.equals(Type.getType(String.class));
      
      final Object configValue = typeIsPrimitiveOrString ? config.get(name) : value;
      value = configValue;
    }
    
    fv = cv.visitField(access, name, desc, signature, value);
    
    return fv;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if(name.startsWith("<clinit>")) {
      MethodVisitor clinitrw = new ClinitRewriter(mv, className, config);
      return clinitrw;
    }
    
    return mv;
  }
  
  @Override 
  public void visitEnd() {
//    if(!this.configScanner.containsClinit()) {
//      MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
//      MethodVisitor clinitrw = new ClinitRewriter(mv, className, config);
//      clinitrw.visitEnd();
//    }
    
    cv.visitEnd();
  }


}
