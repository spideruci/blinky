package org.spideruci.analysis.statik.instrumentation;

import static org.mockito.Mockito.*;
import static org.spideruci.analysis.statik.instrumentation.Config.*;
import static org.spideruci.analysis.dynamic.Profiler.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.analysis.trace.Count;
import org.spideruci.analysis.trace.MethodDecl;
import org.spideruci.analysis.trace.TraceEvent;

/**
  * <b>Injected Probe:</b><br>
  * ... Source-code:<br>
  * ... ...{@code Profiler.printlnLineNumber(lineNumberProbeId, 'C')}<br>
  * ... ...OR<br>
  * ... ...{@code Profiler.printlnLineNumber(lineNumberProbeId, this.identityHashCode())}<br>
  * <br>
  * ... Bytecode for {@code STATIC} methods:<br>
  * ... ... {@code LDC lineNumberProbeId}<br>
  * ... ... {@code LDC STATIC_IDENT} // because `this` in a static method makes no sense<br>
  * ... ... {@code INVOKESTATIC Profiler printlnLineNumber (String, String)V}<br><br>
  * 
  * ... Bytecode for non-{@code STATIC} methods:<br>
  * ... ... {@code LDC lineNumberProbeId}<br>
  * ... ... {@code ALOAD 0}<br>
  * ... ... {@code CHECKCAST java/lang/Object}<br>
  * ... ... {@code INVOKESTATIC Profiler getHash (Ljava/lang/Object;)Ljava/lang/String;}<br>
  * ... ... {@code INVOKESTATIC Profiler printlnLineNumber (String, String)V}<br><br>
  * 
  * ({@code LDC} ==> LoaD Constant);
  * ({@code INVOKESTATIC} ==> INVOKE STATIC method) 
 * @author vpalepu
 * 
 */
public class SourcelineInstrumentationTest {
  
  @BeforeClass
  public static void beforeClass() {
    Profiler.logSourceLineNumber = true;
  }
  
  private BytecodeMethodAdapter methodAdapter = null;
  private MethodVisitor methodWriterMock = null;
  
  private void given_MethodStub_with_AccessMods(int access) {
    // Stub for Method Declaration (and Definition)
    // public class bar { public static bar() {} }
    MethodDecl methodDecl = mock(MethodDecl.class);
    when(methodDecl.getId()).thenReturn(1);
    when(methodDecl.getDeclOwner()).thenReturn("foo"); // className
    when(methodDecl.getDeclName()).thenReturn("bar()V"); // methodName
    when(methodDecl.getDeclAccess()).thenReturn(String.valueOf(access)); // e.g. public static
    
    // Mock for MethodWriter
    methodWriterMock = mock(MethodVisitor.class);
    
    // Create MethodAdapter to instrument method
    methodAdapter = BytecodeMethodAdapter.create(methodDecl, methodWriterMock);
    methodAdapter.enableInstrumentation();
  }
  
  @Test
  public void testLineInsnInStaticPublicMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
    when_VisitingSomeLinenumber();
    then_Verify_Static_MethodWriter();
  }
  
  @Test
  public void testLineInsnInProtectedStaticMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PROTECTED | Opcodes.ACC_STATIC);
    when_VisitingSomeLinenumber();
    then_Verify_Static_MethodWriter();
  }
  
  @Test
  public void testLineInsnInPrivateStaticMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC);
    when_VisitingSomeLinenumber();
    then_Verify_Static_MethodWriter();
  }

  /**
   * 
   */
  private void then_Verify_Static_MethodWriter() {
    verify(methodWriterMock).visitLdcInsn(String.valueOf(Count.getLastInsnProbeId()));
    verify(methodWriterMock).visitLdcInsn(STATIC_IDENT);
    verify(methodWriterMock).visitMethodInsn(
        Opcodes.INVOKESTATIC, PROFILER_NAME, LINENUMER, LINENUMER_DESC, false);
  }
  
  @Test
  public void testLineInsnInPublicNonStaticMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PUBLIC);
    when_VisitingSomeLinenumber();
    then_Verify_NonStatic_MethodWriter();
  }

  @Test
  public void testLineInsnInProtectedMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PROTECTED);
    when_VisitingSomeLinenumber();
    then_Verify_NonStatic_MethodWriter();
  }
  
  @Test
  public void testLineInsnInPrivateMethod() {
    given_MethodStub_with_AccessMods(Opcodes.ACC_PRIVATE);
    when_VisitingSomeLinenumber();
    then_Verify_NonStatic_MethodWriter();
  }
  
  private void when_VisitingSomeLinenumber() {
    Label start = new Label();
    int line = 100; // some number.
    
    methodAdapter.visitLineNumber(line, start);
  }
  
  /**
   * Profiler.getHash((Object) this);
   * ...
   * ALOAD 0
   * CHECKCAST java/lang/Object
   * INVOKESTATIC Profiler getHash (Ljava/lang/Object;)Ljava/lang/String;
   */
  private void then_Verify_NonStatic_MethodWriter() {
    verify(methodWriterMock).visitLdcInsn(String.valueOf(Count.getLastInsnProbeId()));
    verify(methodWriterMock).visitVarInsn(Opcodes.ALOAD, 0);
    verify(methodWriterMock).visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
    verify(methodWriterMock).visitMethodInsn(
        Opcodes.INVOKESTATIC, PROFILER_NAME, GETHASH, GETHASH_DESC, false);
    verify(methodWriterMock).visitMethodInsn(
        Opcodes.INVOKESTATIC, PROFILER_NAME, LINENUMER, LINENUMER_DESC, false);
  }

}
