package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;
import static org.spideruci.analysis.dynamic.Profiler.log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.MethodDecl;
import org.spideruci.analysis.trace.TraceEvent;

public class BytecodeClassAdapter extends ClassVisitor {
  private String className;
  private String sourceName;

  public BytecodeClassAdapter(ClassVisitor cv, String className) {
    super(Opcodes.ASM5, cv);
    this.className = className;
  }
  
  @Override
  public void visitSource(String source, String debug) {
    super.visitSource(source, debug);
    int lastsepIdx = className.lastIndexOf('/');
    final String packageName = lastsepIdx == -1 ? "" : className.substring(0, lastsepIdx);
    this.sourceName = packageName + "/" + source;
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    MethodVisitor mv;
    
    mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if (mv != null 
        && ((access & Opcodes.ACC_NATIVE) == 0)) {
      
      final String methodName = name + desc;
      
      MethodDecl methodDecl = 
          EventBuilder.buildMethodDecl(
              Profiler.useSourcefileName ? sourceName : className, 
              access, methodName);
      
      mv = new BytecodeMethodAdapter(methodDecl, access, name, desc, mv);
      if (log) {
        REAL_OUT.println(methodDecl.getLog());
      }
    }
    return mv;
  }
}
