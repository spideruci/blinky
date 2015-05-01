package org.spideruci.analysis.statik.instrumentation;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;



public class SourcelineMethodAdapter extends AdviceAdapter {
  
  private MethodProperties methodProps;
  private boolean shouldInstrument;
  
  public SourcelineMethodAdapter(MethodProperties methodProps, MethodVisitor mv) {
    super(Opcodes.ASM4, mv, methodProps.MethodAccess, methodProps.MethodName, 
        methodProps.MethodDescription);
    this.methodProps = methodProps;
    this.shouldInstrument = false;
  }
  
  @Override
  protected void onMethodEnter() {

    int lineNum = Profiler.latestLineNumber;

    int opcode = ((this.methodProps.MethodAccess & Opcodes.ACC_STATIC) == 
        Opcodes.ACC_STATIC) ? -3 : -2;
    if(methodProps.MethodName.contains("<init>")) {
      opcode = -5;
    }
    
    String instructionLog = buildInstructionLog(-1, Deputy.ENTER, opcode);
    //instruction log the arguments for the call
    StringBuffer callbackDesc = new StringBuffer();
    callbackDesc.append("(");
    
    mv.visitLdcInsn(methodProps.MethodOwnerName); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodProps.MethodName); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodProps.MethodDescription); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(instructionLog); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId(); 
    callbackDesc.append(Deputy.STRING_DESC);
    
    callbackDesc.append(")V");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_METHODENTER, 
                       callbackDesc.toString());

    Profiler.latestLineNumber = lineNum;
    shouldInstrument = true;
  }
  
  @Override
  protected void onMethodExit(int opcode) {
    int lineNum = Profiler.latestLineNumber;
    String symbolicName = (opcode == Opcodes.ATHROW) ? Deputy.ATHORW : Deputy.RETURN; 
    String instructionLog = buildInstructionLog(lineNum, symbolicName, opcode);
    
    StringBuffer callbackDesc = new StringBuffer();
    callbackDesc.append("(");
    
    mv.visitLdcInsn(methodProps.MethodOwnerName);
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodProps.MethodName);
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(methodProps.MethodDescription);
    callbackDesc.append(Deputy.STRING_DESC);
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC);
    
    callbackDesc.append(")V");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_METHODEXIT, 
                       callbackDesc.toString());

    Profiler.latestLineNumber = lineNum;
  }
  
  @Override
  public void visitLineNumber(int line, Label start) {
    Profiler.latestLineNumber = line;
    
    if(!shouldInstrument) {
      super.visitLineNumber(line, start); //make the actual call.
      return;
    }
    
    super.visitLineNumber(line, start); //make the actual call.
    
    StringBuffer callbackDesc = new StringBuffer();
    callbackDesc.append("(");
    
    String instructionLog = buildInstructionLog(line, Deputy.LINE, -1);
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC);
    
    callbackDesc.append(")V");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_LINENUMER, 
                       callbackDesc.toString());
  }
  
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    int lineNum = Profiler.latestLineNumber;
    if(!shouldInstrument) {
      super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
      return;
    }
    
    StringBuffer callbackDesc = new StringBuffer();
    callbackDesc.append("(");
    
    String methodName = owner + "/" + name + desc;
    String instructionLog = buildInstructionLog(lineNum, methodName, opcode);
    
    mv.visitLdcInsn(instructionLog);
    callbackDesc.append(Deputy.STRING_DESC);
    
    this.loadRecieverObjectId();
    callbackDesc.append(Deputy.STRING_DESC);
    
    callbackDesc.append(")V");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                       Deputy.PROFILER_NAME, 
                       Deputy.PROFILER_INVOKE, 
                       callbackDesc.toString());
    Profiler.latestLineNumber = lineNum;
    super.visitMethodInsn(opcode, owner, name, desc); //make the actual call.
  }
  
  @Override
  public void visitMaxs(int MaxStack, int maxLocals) {
    MaxStack += 15; 
    super.visitMaxs(MaxStack, maxLocals);
  }
  
    private void loadRecieverObjectId() {
      if((this.methodProps.MethodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
        //if the method is static
        mv.visitLdcInsn(this.methodProps.MethodOwnerName);
      } else {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.CHECKCAST, Deputy.desc2type(Deputy.OBJECT_DESC));
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                           Deputy.PROFILER_NAME, 
                           Deputy.PROFILER_GETHASH, 
                           Deputy.PROFILER_GETHASH_DESC);
      }
    }
    
    private String buildInstructionLog(int lineNum, 
                                       String symbolicName, 
                                       int opcode) {
      String[] instructionLogElements = new String[5];
      instructionLogElements[0] = String.valueOf(lineNum);
      instructionLogElements[1] = methodProps.MethodOwnerName;
      instructionLogElements[2] = methodProps.MethodName + methodProps.MethodDescription; 
      instructionLogElements[3] = symbolicName; 
      instructionLogElements[4] = String.valueOf(opcode);
      return Deputy.joinStrings(instructionLogElements, ",");
    }

}
