package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.statik.instrumentation.Config.RUNTIME_TYPE_PROFILER_NAME;

import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.SETUP_INVOKE;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.GET;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.PUT;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.dynamic.util.Arrays;
import org.spideruci.analysis.dynamic.util.MethodDescSplitter;

public class InvokeSignCallBack implements Opcodes {
  
  public static void buildArgProfileProbe(MethodVisitor mv, int opcode, 
      String owner, String name, String desc, String callsite) {
    final boolean guard = Profiler.guard();
    final String[] argTypes = 
        MethodDescSplitter.getArgTypes(desc, owner, opcode == INVOKESTATIC);

    int argCount = argTypes.length;
    boolean isSpecial = name.equals("<init>");
    initInvokeSignature(mv, argCount, name + desc);
    popArguments(mv, argTypes, isSpecial, owner + "/" + name + desc + "::" + callsite + "###" + argCount);
    pushArguments(mv, argTypes, isSpecial);
    Profiler.reguard(guard);
  }
    
    /**
     * Results in the insertion of the following code:<br>
     * Java:<br>
     * {@code RuntimeTypeProfiler.setupForInvoke(argCount);}<br>
     * @param mv
     * @param arraylength
     * @param arrayName
     */
    private static void initInvokeSignature(MethodVisitor mv, int arraylength, String methodName) {
      mv.visitLdcInsn(arraylength);
      mv.visitLdcInsn(methodName);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, RUNTIME_TYPE_PROFILER_NAME, 
          SETUP_INVOKE, "(ILjava/lang/String;)V", false);
    }
    
    private static void pushArguments(MethodVisitor mv, String[] argTypes, boolean isCtor) {
      final String profiler = RUNTIME_TYPE_PROFILER_NAME;
      int minLimit = isCtor ? 1 : 0;
      for(int i = minLimit; i <= argTypes.length - 1; i += 1) {
        final String argType = argTypes[i];
        // LDC i
        mv.visitIntInsn(Opcodes.BIPUSH, i); 
        char argInitial = argType.charAt(0);
        // INVOKESTATIC RuntimeProfiler.getParameter[C|Z|B|S|I|F|J|D](I)Ljava/lang/Object;
        switch(argInitial) {
        case 'L' :
        case '[' :
          mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, GET, "(I)Ljava/lang/Object;", false);
          String type = argType;
          if(argInitial == 'L') {
            int semicolon = type.indexOf(';');
            type = type.substring(1, semicolon);
          }
          mv.visitTypeInsn(Opcodes.CHECKCAST, type);
          continue;
        case 'B' : 
        case 'C' :
        case 'D' :
        case 'F' :
        case 'I' :
        case 'J' :
        case 'S' :
        case 'Z' :
          mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, GET + argInitial, "(I)" + argInitial, false);
          continue;
        default:
          throw new RuntimeException(argInitial + " " + argType);
        }
      }
    }
    
    /**
     * INVOKE profiler.putParameter
     * @param mv
     * @param argTypes
     * @param isCtor
     */
    private static void popArguments(MethodVisitor mv, String[] argTypes, boolean isCtor, String methodName) {
      final String profiler = RUNTIME_TYPE_PROFILER_NAME;
      final String putObjectDesc = "(Ljava/lang/Object;Ljava/lang/String;I)V";
      int minLimit = isCtor ? 1 : 0;
      
      for(int argCount = argTypes.length, idx = argCount - 1; idx >= minLimit; idx -= 1) {
        final String staticTypeName = argTypes[idx];
        
        if(staticTypeName == null) {
          synchronized (Profiler.REAL_OUT) {
            Profiler.REAL_OUT.print("ARGTYPE-NULL>");
          }
          Arrays.printArray(argTypes);
        }
        
        final String description;
        char argInitial = staticTypeName.charAt(0);
        switch(argInitial) {
        case 'L':
        case '[':
          description =  putObjectDesc;
          mv.visitLdcInsn(methodName); // staticTypeName
          break;
        case 'B':
        case 'C':
        case 'D':
        case 'F':
        case 'I':
        case 'J':
        case 'S':
        case 'Z':
          description =  "(" + argInitial + "I)V";
          break;
        default:
          Profiler.REAL_OUT.println("ARGTYPE-UNKNOWN>" + idx + ">");
          Arrays.printArray(argTypes);
          throw new RuntimeException();
        }
        
        // BIPUSH {idx}
        mv.visitIntInsn(BIPUSH, idx);
        // INVOKESTATIC profiler.putParameter
        mv.visitMethodInsn(INVOKESTATIC, profiler, PUT, description, false);
      }
      
      if(minLimit == 1) {
        mv.visitInsn(ACONST_NULL);
        final String staticTypeName = argTypes[0];
        mv.visitLdcInsn(staticTypeName);
        // BIPUSH {i}
        mv.visitIntInsn(BIPUSH, 0);
        // INVOKESTATIC profiler.putParameter
        mv.visitMethodInsn(INVOKESTATIC, profiler, PUT, putObjectDesc, false);
      }
    }
}
