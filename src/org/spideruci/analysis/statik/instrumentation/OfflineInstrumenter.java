package org.spideruci.analysis.statik.instrumentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class OfflineInstrumenter {

  static final String JavacSource = "~/TestSubjects/langtools-9d81ae1c417a/dist/bootstrap/lib/javac_o/";
  static final String JavacDestination = "~/TestSubjects/langtools-9d81ae1c417a/dist/bootstrap/lib/javac_i/";

  public static void main(String[] args) throws FileNotFoundException, IOException {
    String source, destination;
    if(args == null || args.length == 0) {
      source = JavacSource;
      destination = JavacDestination;
    } else {
      throw new UnsupportedOperationException();
    }

    ArrayList<ClassItem> classItems = ClassItem.getClassItems(source,"");
    OfflineInstrumenter driver = new OfflineInstrumenter();
    driver.compileTimeInstrumentation(classItems, source, destination);
  }

  public void compileTimeInstrumentation(ArrayList<ClassItem> classItems, 
      String source, String destination) 
          throws FileNotFoundException, IOException {
    for(ClassItem classItem : classItems) {
      if(classItem.Package.startsWith("/")) {
        classItem.Package = classItem.Package.replaceFirst("/", "");
      }
    }

    SummaryStatistics instrumentStats = 
        SummaryStatistics.init()
        .withClassDetected(classItems.size())
        .withSource(source)
        .withDestination(destination)
        .withRestrictions("javacrestrictions");

    for (ClassItem classItem : classItems) {
      String className = classItem.getFullName();

      if(javacRestrictions(className)) {
        transferAndPrintExecption(classItem, source, destination, null);
        instrumentStats.instrumentationSkipped();
      } else {
        try {
          transferAndInstrument(classItem, source, destination);
          instrumentStats.instrumentationPassed();
        } catch (Exception | StackOverflowError e) {
          transferAndPrintExecption(classItem, source, destination, e);
          instrumentStats.instrumentationFailed();
        }
      }
    }

    instrumentStats.displayStatistics();
    System.out.println("i am done!");
  }

  private void transferAndPrintExecption(ClassItem classItem, 
      String source, 
      String destination,
      Throwable e)
          throws FileNotFoundException, IOException {
    File inFile = new File(source+classItem.getFullName()+".class");
    FileInputStream in = new FileInputStream(inFile);
    ClassReader classReader = new ClassReader(in);
    ByteCodeHelper.print(classReader.b, 
        destination+classItem.Package, 
        destination+classItem.getFullName()+".class");
    in.close();

    if(e == null) {
      System.out.println(classItem.getFullName());
      return;
    }
    StringBuffer buffer = new StringBuffer();
    buffer.append(classItem.getFullName())
    .append("\t").append(e.getMessage())
    .append("\t").append(e).append("\n");
    
    for(Object object : (Object[]) e.getStackTrace()) {
      buffer.append(object).append("\n");
    }
    System.err.println(buffer.toString());
  }

  private void transferAndInstrument(ClassItem classItem,
      String source, 
      String destination) {
    String classFullName = classItem.getFullName();
    File inFile = new File(source + classFullName + ".class");

    SourceLineInstrumenter instrumenter = new SourceLineInstrumenter();
    byte[] bytecode = instrumenter.instrument(classFullName, null, inFile);

    ByteCodeHelper.print(bytecode, 
        destination + classItem.Package, 
        destination + classFullName + ".class");
    System.out.println(classFullName);
  }


  private boolean javacRestrictions(String classFullName) {
    boolean resitrictionsApply = false;
    resitrictionsApply = resitrictionsApply || 
        classFullName.startsWith("java/lang/") || 
        classFullName.startsWith("javax/swing/plaf/");
    return resitrictionsApply;    
  }

  public static class ClassItem {
    public String Name;
    public String Package;
    public String getFullName() {
      return (Package.equals("")?"":Package+"/") + Name;
    }
    public static ArrayList<ClassItem> getClassItems(String source, String startPackage) {
      ArrayList<ClassItem> classList = new ArrayList<ClassItem>();
      File dir = new File(source);
      String[] dummy = dir.list();
      for(int i = 0; i < dummy.length; i++) {
        ClassItem obj = new ClassItem();
        File file = new File(source + "/" + dummy[i]);
        if(dummy[i].endsWith(".class")) {
          obj.Name = dummy[i].substring(0, dummy[i].lastIndexOf(".class"));
          obj.Package = startPackage;
          classList.add(obj);
        }
        else if(file.isDirectory()) {
          classList.addAll(getClassItems(source+"/"+dummy[i], startPackage + "/" + dummy[i]));
        }
      }
      return classList; 
    }
  }


  public static class SummaryStatistics {
    private int skippedClasses = 0;
    private int instrumentationPasses = 0;
    private int instrumentationFails = 0;
    private int classesDetected = 0; // classItems.size()
    private int summaryInstrumentationPasses = 0;
    private String source;
    private String destination;
    private String restrictions;

    public static SummaryStatistics init() {
      return new SummaryStatistics();
    }

    public void instrumentationPassed() {
      instrumentationPasses += 1;
    }

    public void summaryInstrumentationPassed() {
      summaryInstrumentationPasses += 1;
    }

    public void instrumentationFailed() {
      instrumentationFails += 1;
    }

    public void instrumentationSkipped() {
      skippedClasses += 1;
    }

    public SummaryStatistics withClassDetected(int count) {
      classesDetected = count;
      return this;
    }

    public SummaryStatistics withSource(String source) {
      this.source = source; return this;
    }

    public SummaryStatistics withDestination(String destination) {
      this.destination = destination; return this;
    }

    public SummaryStatistics withRestrictions(String restrictions) {
      this.restrictions = restrictions; return this;
    }

    public void displayStatistics() {
      System.out.println("total classes instrumented:" + instrumentationPasses);
      System.out.println("total classes summary instrumented:" + summaryInstrumentationPasses);
      System.out.println("total classes failed to instrument:" + instrumentationFails);
      System.out.println("total classes skipped from instrument:" + skippedClasses);
      System.out.println("total classes:" + ((int)(instrumentationFails + instrumentationPasses + skippedClasses)));
      System.out.println("total classes detected:" + classesDetected); 
      System.out.println("source:" + source);
      System.out.println("destination:" + destination);
      System.out.println("restrictions:" + restrictions);
    }
  }

  public static  class ByteCodeHelper implements Opcodes {

    private static PrintStream byteStream;


    public static void print(byte[] bytecode, String dirStructure, String filePath) {
      try {
        File dir = new File(dirStructure);
        dir.mkdirs();
        byteStream = new PrintStream(filePath);
        byteStream.write(bytecode);
        byteStream.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //jar cvfm javac-i.jar META-INF/MANIFEST.MF com javax jdk & cp javac-i.jar ../javac-i.jar

}
