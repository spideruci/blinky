package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.trace.EventBuilder.*;
import static org.spideruci.analysis.statik.instrumentation.ControlDepAdapter.BYTECODE_LDC_MARKER;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.ProfilerB;
import org.spideruci.analysis.dynamic.util.MethodDescSplitter;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitcher;
import org.spideruci.analysis.statik.instrumentation.zerooperand.ZeroOperandSwitchListerner;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.MethodDecl;
import org.spideruci.analysis.trace.TraceEvent;
import org.spideruci.analysis.util.MyAssert;
import org.spideruci.analysis.util.caryatid.Helper;

public class BytecodeMethodAdapter extends AdviceAdapter {
  
  private MethodDecl methodDecl;
  private boolean shouldInstrument;
  
  public static BytecodeMethodAdapter create(MethodDecl methodDecl, MethodVisitor mv) {
    int access = Integer.parseInt(methodDecl.getDeclAccess());
    
    String[] declName = methodDecl.getDeclName().split("\\(");
    String name = declName[0];
    String desc = "(" + declName[1];
    
    return new BytecodeMethodAdapter(methodDecl, access, name, desc, mv);
  }
  
  public BytecodeMethodAdapter(MethodDecl methodDecl, int access, String name, 
      String desc, MethodVisitor mv) {
    super(Opcodes.ASM4, mv, access, name, desc);
    this.methodDecl = methodDecl;
    this.shouldInstrument = false;
    Profiler.latestLineNumber = -1;
    Profiler.latestBytecodeIndex = -1;
  }
  
  public void enableInstrumentation() {
    shouldInstrument = true;
  }
  
  @Override
  protected void onMethodEnter() {
    int lineNum = Profiler.latestLineNumber;
    int byteIndex = Profiler.latestBytecodeIndex;
    
    int access = Integer.parseInt(methodDecl.getDeclAccess());
    final boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    int opcode = isStatic ? -3 : -2;
    if(methodDecl.getDeclName().contains("<init>")) {
      opcode = -5;
    }
    
    if(Profiler.logEnterRuntimeSign) {
      
      final String methodName = methodDecl.getDeclName();
      final String[] argTypes = MethodDescSplitter.getArgTypeSplit(methodName);
      
      int varOffset = (isStatic? 0 : 1);
      for(int i = 0; i < argTypes.length; i += 1) {
        String argType = argTypes[i];
        int varIndex = i + varOffset;
        
        char argInitial = argType.charAt(0);
        
        if(argInitial == 'D' || argInitial == 'J') {
          varOffset += 1;
        }
        
        if(argInitial == 'L') {
          mv.visitVarInsn(Opcodes.ALOAD, varIndex);
          mv.visitLdcInsn(argType);
          mv.visitMethodInsn(Opcodes.INVOKESTATIC, Config.PROFILER_NAME, 
              Profiler.GETTYPENAME, Profiler.GETTYPENAME_DESC, false);
        } else {
          mv.visitLdcInsn(argType);
        }
        
        ProbeBuilder.start(mv)
        .appendDesc(Config.STRING_DESC)
        .passArg(String.valueOf(varIndex))
        .passArg(i == 0) // isFirst?
        .passArg(i == (argTypes.length - 1)) // isLast?
        .build(Profiler.ARGLOG, profilerToUse(methodDecl.getDeclOwner()));
      }
      
    }
    
    String instructionLog = buildInstructionLog(-1, -1, EventType.$enter$, 
        opcode, methodDecl.getId());
    
    ProbeBuilder.start(mv)
    .passArg(methodDecl.getDeclOwner())
    .passArg(methodDecl.getDeclName())
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Profiler.METHODENTER, profilerToUse(methodDecl.getDeclOwner()));
    
    Profiler.latestLineNumber = lineNum;
    Profiler.latestBytecodeIndex = byteIndex;
    enableInstrumentation();
  }
  
  @Override
  protected void onMethodExit(int opcode) {
    int lineNum = Profiler.latestLineNumber;
    int byteIndex = Profiler.latestBytecodeIndex;
    
    EventType eventType = 
        (opcode == Opcodes.ATHROW) ? EventType.$athrow$ : EventType.$return$; 
    String instructionLog = buildInstructionLog(byteIndex, lineNum, eventType, 
        opcode, methodDecl.getId());
    
    ProbeBuilder.start(mv)
    .passArg(methodDecl.getDeclOwner())
    .passArg(methodDecl.getDeclName())
    .passArg(instructionLog)
    .passThis(methodDecl.getDeclAccess())
    .build(Profiler.METHODEXIT, profilerToUse(methodDecl.getDeclOwner()));
    
    Profiler.latestBytecodeIndex = byteIndex;
    Profiler.latestLineNumber = lineNum;
  }
  
  @Override
  public void visitLineNumber(int line, Label start) {
    super.visitLineNumber(line, start); //make the actual call.
    Profiler.latestLineNumber = line;
    
    if(shouldInstrument && Profiler.logSourceLineNumber) {
      final int opcode = -1;
      String instructionLog = buildInstructionLog(-1, line, EventType.$line$, 
          opcode, methodDecl.getId());
      
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.LINENUMER, profilerToUse(methodDecl.getDeclOwner()));
    }
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    if(!shouldInstrument || !Profiler.logMethodInvoke) {
      super.visitMethodInsn(opcode, owner, name, desc); // make the actual call.
      return;
    }
    
    if(Profiler.logMethodInvoke) {
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;
      
      if(Profiler.logInvokeRuntimeSign) {
        synchronized (Profiler.REAL_OUT) {
          Profiler.REAL_OUT.println(name+desc);
        }
        
        InvokeSignCallBack.buildArgProfileProbe(mv, opcode, owner, name, desc, 
            this.methodDecl.getDeclOwner() + "/" + this.methodDecl.getDeclName());
      }
      
      String instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$invoke$, opcode, methodDecl.getId(), owner, name, desc);
      
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.INVOKE, profilerToUse(methodDecl.getDeclOwner()));
      
      final boolean callingValidRtJar = 
          isWithinRtJar(owner) 
          && !owner.startsWith("java/lang")
          && !(owner.equals("java/io/PrintStream") && name.startsWith("print"))
          && !(owner.equals("java/io/Writer") && name.startsWith("write"));
      final boolean withinRtJar = isWithinRtJar(this.methodDecl.getDeclOwner());
      final boolean invokingRtFromApp = !withinRtJar && callingValidRtJar;
      
      if(invokingRtFromApp) {
        ProbeBuilder.start(mv)
        .build(ProfilerB.ACTIVATE, Config.PROFILER_B_NAME);
      }
      
      super.visitMethodInsn(opcode, owner, name, desc); // make the actual call.
      
      instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$complete$, -4, methodDecl.getId(), owner, name, desc);
      
      if(invokingRtFromApp) {
        ProbeBuilder.start(mv)
        .build(ProfilerB.DEACTIVATE, Config.PROFILER_B_NAME);
      }
      
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.COMPLETE, profilerToUse(methodDecl.getDeclOwner()));
      
      Profiler.latestLineNumber = lineNum;
      Profiler.latestBytecodeIndex = byteIndex;
    }
  }
  
  @Override
  public void visitVarInsn(int opcode, int operand) { // TODO $ret$
    if(shouldInstrument && Profiler.logVar) {
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;
      
      String instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$var$, opcode, methodDecl.getId(), String.valueOf(operand));

      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.VAR, profilerToUse(methodDecl.getDeclOwner()));

      Profiler.latestLineNumber = lineNum;
      Profiler.latestBytecodeIndex = byteIndex;
    }
    
    super.visitVarInsn(opcode, operand); //make the actual call.
  }
  
  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if(!shouldInstrument || !Profiler.logField) {
      super.visitFieldInsn(opcode, owner, name, desc); //make the actual call.
      return;
    }

    if(Profiler.logField) {
      
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;

      String instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$field$, opcode, methodDecl.getId(), owner, name, desc);

      if(opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC) {
        ProbeBuilder.start(mv)
        .passPutInsnStackArgs(opcode, desc, owner)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.FIELD, profilerToUse(methodDecl.getDeclOwner()));
        
        super.visitFieldInsn(opcode, owner, name, desc); //make the actual call.
      } else {
        ProbeBuilder getProbeCallback = ProbeBuilder.start(mv);
        getProbeCallback.setupGetInsnStackArgs(opcode, owner);
        
        super.visitFieldInsn(opcode, owner, name, desc); //make the actual call.
        
        MyAssert.assertThat(getProbeCallback != null && instructionLog != null);
        
        getProbeCallback
        .passGetInsnStackArgs(opcode, desc, owner)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.FIELD, profilerToUse(methodDecl.getDeclOwner()));
      }

      Profiler.latestLineNumber = lineNum;
      Profiler.latestBytecodeIndex = byteIndex;
    }
  }
  
  @Override
  public void visitInsn(int opcode) {
    if(shouldInstrument && Profiler.logZero) {
      
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;
      
      ZeroOperandSwitchListerner listerner = 
          ZeroOperandSwitchListerner.create(mv, byteIndex, lineNum, methodDecl);
      ZeroOperandSwitcher switcher = new ZeroOperandSwitcher(listerner);
      switcher.svitch(opcode);

      Profiler.latestBytecodeIndex = byteIndex;
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitInsn(opcode); //make the actual call.
  }
  
  @Override
  public void visitJumpInsn(int opcode, Label label) {
    if(shouldInstrument && Profiler.logJump) {
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;
      
      String instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$jump$, opcode, methodDecl.getId());
      
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.JUMP, profilerToUse(methodDecl.getDeclOwner()));
      
      Profiler.latestBytecodeIndex = byteIndex;
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitJumpInsn(opcode, label);
  }
  
  @Override
  public void visitIincInsn(int var, int increment) {
    if(shouldInstrument && Profiler.logVar) {
      final int lineNum = Profiler.latestLineNumber;
      final int byteIndex = Profiler.latestBytecodeIndex;
      
      String instructionLog = buildInstructionLog(byteIndex, lineNum, 
          EventType.$iinc$, Opcodes.IINC, methodDecl.getId(), 
          String.valueOf(increment));
      
      ProbeBuilder.start(mv)
      .passArg(instructionLog)
      .passThis(methodDecl.getDeclAccess())
      .build(Profiler.IINC, profilerToUse(methodDecl.getDeclOwner()));
      
      Profiler.latestBytecodeIndex = byteIndex;
      Profiler.latestLineNumber = lineNum;
    }
    
    super.visitIincInsn(var, increment);
  }
  
  @Override
  public void visitIntInsn(int opcode, int operand) {
    switch(opcode) {
    case BIPUSH:
    case SIPUSH:
      onConstantInsn(opcode, String.valueOf(operand));
      break;
    case NEWARRAY:
      onTypeInsn(opcode, Deputy.primitiveCode2String(operand), 1);
      break;
    default:
      throw new RuntimeException("unexpected opcode: " + opcode);
    }
    
    super.visitIntInsn(opcode, operand);
  }
  
  @Override
  public void visitLdcInsn(Object cst) {
    if(cst != null
        && cst instanceof String
        && ((String) cst).startsWith(BYTECODE_LDC_MARKER)) {
      String marker = ((String) cst).substring(BYTECODE_LDC_MARKER.length());
      int bytecodeIndex = Helper.s2i(marker);
      Profiler.latestBytecodeIndex = bytecodeIndex;
      return;
    }
    
    String metadata;
    if(cst instanceof Long || cst instanceof Double) {
      metadata = Config.LDC_16;
    } else {
      metadata = Config.LDC_8;
    }
    
    onConstantInsn(Opcodes.LDC, metadata);
    super.visitLdcInsn(cst);
  }
  
    private void onConstantInsn(int opcode, String value) {
      if(shouldInstrument && Profiler.logConstant) {
        final int lineNum = Profiler.latestLineNumber;
        final int byteIndex = Profiler.latestBytecodeIndex;
        
        String instructionLog = buildInstructionLog(byteIndex, lineNum, 
            EventType.$constant$, opcode, methodDecl.getId(), value);
        
        ProbeBuilder.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.CONSTANT, profilerToUse(methodDecl.getDeclOwner()));
        
        Profiler.latestBytecodeIndex = byteIndex;
        Profiler.latestLineNumber = lineNum;
      }
    }
  
  @Override
  public void visitTypeInsn(int opcode, String type) {
    int dims = 0;
    if(opcode == Opcodes.ANEWARRAY) {
      dims = 1;
    }
    onTypeInsn(opcode, type, dims);
    super.visitTypeInsn(opcode, type);
  }
  
  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {
    onTypeInsn(Opcodes.MULTIANEWARRAY, desc, dims);
    super.visitMultiANewArrayInsn(desc, dims);
  }
  
    private void onTypeInsn(int opcode, String type, int dims) {
      if(shouldInstrument && Profiler.logType) {
        final int lineNum = Profiler.latestLineNumber;
        final int byteIndex = Profiler.latestBytecodeIndex;
        
        String instructionLog = buildInstructionLog(byteIndex, lineNum, 
            EventType.$type$, opcode, methodDecl.getId(), type, 
            String.valueOf(dims));
        
        ProbeBuilder.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.TYPE, profilerToUse(methodDecl.getDeclOwner()));
        
        Profiler.latestBytecodeIndex = byteIndex;
        Profiler.latestLineNumber = lineNum;
      }
    }
  
  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    onSwtich(Opcodes.LOOKUPSWITCH);
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }
  
  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    onSwtich(Opcodes.TABLESWITCH);
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }
  
    private void onSwtich(int opcode) {
      if(shouldInstrument && Profiler.logSwitch) {
        final int lineNum = Profiler.latestLineNumber;
        final int byteIndex = Profiler.latestBytecodeIndex;
        
        String instructionLog = buildInstructionLog(byteIndex, lineNum, 
            EventType.$switch$, opcode, methodDecl.getId());
        
        ProbeBuilder.start(mv)
        .passArg(instructionLog)
        .passThis(methodDecl.getDeclAccess())
        .build(Profiler.TYPE);
        
        Profiler.latestBytecodeIndex = byteIndex;
        Profiler.latestLineNumber = lineNum;
      }
    }
  
  @Override
  public void visitTryCatchBlock(Label start, Label end, 
      Label handler, String type) {
    super.visitTryCatchBlock(start, end, handler, type);
  }
  
  @Override
  public void visitLocalVariable(String name, String desc, String signature, 
      Label start, Label end, int index) {
    super.visitLocalVariable(name, desc, signature, start, end, index);
  }
  
  @Override
  public void visitMaxs(int MaxStack, int maxLocals) {
    super.visitMaxs(MaxStack, maxLocals);
  }
  
  private static final String[] rtNameSpaces = new String[] {
      "java", // "javax"
      "sun", //"sunw",
      "apple/",
      "com/apple",
      "com/sun",
      "com/oracle",
      "org/ietf",
      "org/jcp",
      "org/omg",
      "org/w3c",
      "org/xml",
  };
  
  private static boolean isWithinRtJar(final String className) {
    for(int i = 0; i < rtNameSpaces.length; i += 1) {
      if(className.startsWith(rtNameSpaces[i]))
        return true;
    }
    
    return false;
  }
  
  public static String profilerToUse(final String className) {
    final boolean isWithinRtJar = isWithinRtJar(className);
    return isWithinRtJar ? Config.PROFILER_B_NAME : Config.PROFILER_NAME;
  }
}