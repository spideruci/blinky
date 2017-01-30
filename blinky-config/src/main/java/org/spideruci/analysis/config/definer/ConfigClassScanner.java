package org.spideruci.analysis.config.definer;

import java.util.TreeMap;
import java.util.TreeSet;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class ConfigClassScanner extends PrototypicalClassAdapter {

  private boolean containsClinit;
  private final TreeMap<String, Type> publicStaticFields;
  private final TreeMap<String, Object> publicStaticFieldsValues;
  private final TreeSet<String> undefinedPublicStaticFields;
  private final TreeSet<String> staticFieldsDefinedInClinit;
  
  protected ConfigClassScanner(int classWriterFlags, String className) {
    super(classWriterFlags, className);
    containsClinit = false;
    publicStaticFields = new TreeMap<>();
    publicStaticFieldsValues = new TreeMap<>();
    undefinedPublicStaticFields = new TreeSet<>();
    staticFieldsDefinedInClinit = new TreeSet<>();
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if (name.equals("<clinit>")) {
      this.containsClinit = true;
      mv = new ClinitScanner(mv, staticFieldsDefinedInClinit);
    }
    
    return mv;
  }
  
  static int PUBLIC_STATIC = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
  
  @Override
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    FieldVisitor fv = cv.visitField(access, name, desc, signature, value);
    
    if((access & PUBLIC_STATIC) == PUBLIC_STATIC) {
      publicStaticFields.put(name, Type.getType(desc));
      publicStaticFieldsValues.put(name, value);
      
      if(value == null) {
        undefinedPublicStaticFields.add(name);
      }
      
    }
    
    return fv;
  }
  
  public boolean containsPublicStaticField(String fieldName) {
    return publicStaticFields.containsKey(fieldName);
  }
  
  public Type getPublicStaticField(String fieldName) {
    return publicStaticFields.get(fieldName);
  }
  
  public Object getPublicStaticFieldValue(String fieldName) {
    return publicStaticFieldsValues.get(fieldName);
  }
  
  public boolean containsClinit() {
    return this.containsClinit;
  }
  
  public boolean publicStaticFieldIsDefined(final String fieldName) {
    if(containsPublicStaticField(fieldName)) {
      return !undefinedPublicStaticFields.contains(fieldName) 
          || staticFieldsDefinedInClinit.contains(fieldName);
    }
    
    String errMsg = String.format(
        "%s does not exist in class %s", fieldName, this.className);
    throw new RuntimeException(errMsg);
  }
  
  private static class ClinitScanner extends MethodVisitor {

    private final TreeSet<String> definedStaticFields;
    
    protected ClinitScanner(MethodVisitor mv, TreeSet<String> definedStaticFields) {
      super(Opcodes.ASM5, mv);
      this.definedStaticFields = definedStaticFields;
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name,
        String desc) {
      
      if(opcode == Opcodes.PUTSTATIC) {
        definedStaticFields.add(name);
      }
    }

  }

}
