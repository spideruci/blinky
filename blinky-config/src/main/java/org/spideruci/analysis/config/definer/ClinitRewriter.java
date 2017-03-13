package org.spideruci.analysis.config.definer;

import static org.spideruci.analysis.config.definer.ConfigFieldsDefiner.toPrimitiveType;

import java.util.ArrayList;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClinitRewriter extends MethodVisitor {

  private Map<String, ?> config;
  private String className;
  
  public ClinitRewriter(MethodVisitor mv, String className, Map<String, ?> config) {
    super(Opcodes.ASM5, mv);
    this.className = className;
    this.config = config;
  }
  
  @Override
  public void visitInsn(int opcode) {
    if(opcode == Opcodes.RETURN) {
      for(String fieldName : config.keySet()) {
        Object fieldValue = config.get(fieldName);
        Class<?> fieldClass = fieldValue.getClass();
        Type fieldType = toPrimitiveType(fieldClass);

        switch(fieldType.getSort()) {
        case Type.INT: case Type.BOOLEAN: case Type.FLOAT: case Type.DOUBLE:
          break;

        case Type.OBJECT:
          if(fieldValue.getClass() != String.class) {
            throw new RuntimeException("Unhandled type: " + fieldClass.getName());
          }
          break;

        case Type.ARRAY:
          @SuppressWarnings("unchecked")
          ArrayList<String> list = (ArrayList<String>)fieldValue;
          assignField(list, fieldName);
          break;

        default:
          throw new RuntimeException("Unhandled type: " + fieldClass.getName());
        }
      }
    }
    
    super.visitInsn(opcode);
  }
  
  @Override
  public void visitEnd() {
    super.visitEnd();
  }
  
  @SuppressWarnings("unused")
  private void assignField(int value, String fieldName) {
    mv.visitLdcInsn(value);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, "I");
  }
  
  @SuppressWarnings("unused")
  private void assignField(double value, String fieldName) {
    assignField((float) value, fieldName);
  }
  
  private void assignField(float value, String fieldName) {
    mv.visitLdcInsn(value);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, "F");
  }
  
  @SuppressWarnings("unused")
  private void assignField(boolean value, String fieldName) {
    mv.visitLdcInsn(value);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, "Z");
  }
  
  @SuppressWarnings("unused")
  private void assignField(String value, String fieldName) {
    mv.visitLdcInsn(value);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, stringDesc);
  }
  
  private void assignField(ArrayList<String> value, String fieldName) {
    final int arraysize = value.size();
    
    mv.visitLdcInsn(arraysize);
    mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
    
    for(int index = 0; index < arraysize; index += 1) {
      mv.visitInsn(Opcodes.DUP);
      mv.visitLdcInsn(index);
      String arrayitem = value.get(index);
      mv.visitLdcInsn(arrayitem);
      mv.visitInsn(Opcodes.AASTORE);
    }
    
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, stringArrayDesc);
  }

  private static final String stringArrayDesc = "[Ljava/lang/String;";
  private static final String stringDesc = "Ljava/lang/String;";
}
