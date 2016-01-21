package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.statik.instrumentation.Deputy.RUNTIME_TYPE_PROFILER_NAME;

import java.util.Arrays;

import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.SETUP_INVOKE;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.GET;
import static org.spideruci.analysis.dynamic.RuntimeTypeProfiler.PUT;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import org.spideruci.analysis.util.caryatid.Helper;

public class InvokeSignCallBack {
  
  public static void buildArgProfileProbe(MethodVisitor mv, int opcode, 
      String owner, String name, String desc) {
    String[] argTypes; 

    if(opcode == Opcodes.INVOKESTATIC) {
      argTypes = Helper.getArgTypeSplit(desc);
    }
    else {
      String[] temp = Helper.getArgTypeSplit(desc);
      argTypes = new String[temp.length + 1];
      argTypes[0] = "L" + owner + ";";
      for(int i = 0; i < temp.length; i += 1) {
        argTypes[i+1] = temp[i];
      }
    }

    int argCount = argTypes.length;
    boolean isSpecial = opcode == Opcodes.INVOKESPECIAL;
    initInvokeSignature(mv, argCount);
    popArguments(mv, argTypes, isSpecial);
    pushArguments(mv, argTypes, isSpecial);
  }
    
    /**
     * Results in the insertion of the following code:<br>
     * Java:<br>
     * {@code RuntimeTypeProfiler.setupForInvoke(argCount);}<br>
     * @param mv
     * @param arraylength
     * @param arrayName
     */
    private static void initInvokeSignature(MethodVisitor mv, int arraylength) {
      mv.visitLdcInsn(arraylength);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, RUNTIME_TYPE_PROFILER_NAME, 
          SETUP_INVOKE, "(I)V", false);
    }
    
    private static void pushArguments(MethodVisitor mv, String[] argTypes, boolean isSpecial) {
      final String profiler = RUNTIME_TYPE_PROFILER_NAME;
      int minLimit = isSpecial ? 1 : 0;
      for(int i = minLimit; i <= argTypes.length - 1; i += 1) {
        // LDC i
        mv.visitIntInsn(Opcodes.BIPUSH, i); 
        char argInitial = argTypes[i].charAt(0);
        // INVOKESTATIC Data.getParameter(I)Ljava/lang/Object;|C|Z|B|S|I|F|J|D
        switch(argInitial) {
        case 'L' :
        case '[' :
          mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, GET, "(I)Ljava/lang/Object;", false);
          String type = argTypes[i];
          if(type.charAt(0) == 'L' ) {
            int semicolon = type.indexOf(';');
            type = type.substring(1, semicolon);
          }
          mv.visitTypeInsn(Opcodes.CHECKCAST, type);
          continue;
        case 'Z' :
        case 'B' : 
        case 'S' : 
        case 'C' : 
        case 'I' : 
        case 'F' : 
        case 'J' : 
        case 'D' : 
          mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, GET + argInitial, "(I)" + argInitial, false);
          continue;
        default:
          throw new RuntimeException(argInitial + " " + argTypes[i]);
        }
      }
    }
    
    /**
     * INVOKE profiler.putParameter
     * @param mv
     * @param argTypes
     * @param isSpecial
     */
    private static void popArguments(MethodVisitor mv, String[] argTypes, boolean isSpecial) {
      final String profiler = RUNTIME_TYPE_PROFILER_NAME;
      final String putObjectDesc = "(Ljava/lang/Object;Ljava/lang/String;I)V";
      int minLimit = isSpecial ? 1 : 0;
      
      for(int i = argTypes.length - 1; i >= minLimit; i -= 1) {
        String description;
        if(argTypes[i] == null) {
          System.out.println("ARGTYPE-NULL>" + Arrays.asList(argTypes).toString());
        }
        char argInitial = argTypes[i].charAt(0);
        switch(argInitial) {
        case 'L':
        case '[':
          description =  putObjectDesc;
          mv.visitLdcInsn(argTypes[i]);
          break;
        case 'Z': 
        case 'B': 
        case 'S': 
        case 'C': 
        case 'I': 
        case 'F':
        case 'J': 
        case 'D': 
          description =  "(" + argInitial + "I)V";
          break;
        default:
          System.out.println();
          throw new RuntimeException("ARGTYPE-UNKNOWN>" + i + ">" + Arrays.asList(argTypes).toString());
        }
        
        // BIPUSH {i}
        mv.visitIntInsn(Opcodes.BIPUSH, i);
        // INVOKESTATIC profiler.putParameter(Ljava/lang/Object;I)V
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, PUT, description, 
            false);
      }
      
      if(isSpecial) {
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitLdcInsn(argTypes[0]);
        // BIPUSH {i}
        mv.visitIntInsn(Opcodes.BIPUSH, 0);
        // INVOKESTATIC profiler.putParameter(Ljava/lang/Object;I)V
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, profiler, PUT, putObjectDesc, 
            false);
      }
    }
}
