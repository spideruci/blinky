package org.spideruci.analysis.statik.instrumentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.dynamic.Profiler;

public class OfflineInstrumenter {

  private static PrintStream out;
  private static PrintStream err;
  private static PrintStream skipped;
  
  private static final ArrayList<String> permittedClasses = new ArrayList<>();
  private static final ArrayList<String> restrictedClasses = new ArrayList<>();
  
  public static boolean isActive = false;
  
  static {
    final String permitsPath = System.getProperty("permits");
    final String restrictsPath = System.getProperty("restricts");
    
    if(permitsPath != null && !permitsPath.trim().isEmpty()) {
      readClassList(permittedClasses, permitsPath);
    }
    
    if(restrictsPath != null && !restrictsPath.trim().isEmpty()) {
      readClassList(restrictedClasses, restrictsPath);
    }
    
    
    restrictedClasses.add("java/lang/");
    restrictedClasses.add("java/util/");
    restrictedClasses.add("java/security/");
    restrictedClasses.add("javax/swing/plaf/");
    restrictedClasses.add("com/sun/");
    restrictedClasses.add("sun/");
    restrictedClasses.add("java/net/URLClassLoader");
    restrictedClasses.add("com/oracle/");
    restrictedClasses.add("apple/");
    restrictedClasses.add("sunw/");
    
  }
  
  private static void readClassList(final ArrayList<String> classes, 
      final String classListPath) {
    final File permits = new File(classListPath);
    Scanner scanner;
    
    try {
      scanner = new Scanner(permits);
      
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String className = line == null ? "" : line.trim();
        
        if(className.isEmpty()) {
          continue;
        }
        
        classes.add(className);
      }
      scanner.close();
    } catch(FileNotFoundException fileEx) {
      System.out.println(fileEx.getMessage());
    }
  }
  
  public static void main(String[] args) 
      throws FileNotFoundException, IOException {
    isActive = true;
    Profiler.setLogFlags(true);
    String source = System.getProperty("src");
    String destination = System.getProperty("dest");
    
    out = new PrintStream(new File("offline-instrumenter.log"));
    skipped = new PrintStream(new File("offline-instrumenter.skip"));
    err = new PrintStream(new File("offline-instrumenter.err"));
    
    if(source == null || destination == null) {
      throw new RuntimeException("specify source and destination with -Dsrc and -Ddest options.");
    }

    ArrayList<ClassItem> classItems = ClassItem.getClassItems(source,"");
    OfflineInstrumenter driver = new OfflineInstrumenter();
    driver.offlineInstrumentation(classItems, source, destination);
  }

  public void offlineInstrumentation(ArrayList<ClassItem> classItems, 
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

      if(isRestricted(className)) {
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
    out.println("i am done!");
  }

  private void transferAndPrintExecption(ClassItem classItem,  String source, 
      String destination, Throwable e) throws FileNotFoundException, IOException {
    File inFile = new File(source + classItem.getFullName() + ".class");
    FileInputStream in = new FileInputStream(inFile);
    ClassReader classReader = new ClassReader(in);
    ByteCodeHelper.print(classReader.b, 
        destination + classItem.Package, 
        destination + classItem.getFullName() + ".class");
    in.close();

    if(e == null) {
      skipped.println(classItem.getFullName());
      return;
    }
    StringBuffer buffer = new StringBuffer();
    buffer.append(classItem.getFullName())
    .append("\t").append(e.getMessage())
    .append("\t").append(e).append("\n");
    
    for(Object object : (Object[]) e.getStackTrace()) {
      buffer.append(object).append("\n");
    }
    err.println(buffer.toString());
  }

  private void transferAndInstrument(ClassItem classItem,
      String source, 
      String destination) {
    String classFullName = classItem.getFullName();
    File inFile = new File(source + classFullName + ".class");

    ClassInstrumenter instrumenter = new ClassInstrumenter();
    byte[] bytecode = instrumenter.instrument(classFullName, null, inFile);

    ByteCodeHelper.print(bytecode, 
        destination + classItem.Package, 
        destination + classFullName + ".class");
    out.println(classFullName);
  }

  /**
   * 
   * @param classFullName
   * @return
   */
  private boolean isRestricted(String classFullName) {
    for(String classname : restrictedClasses) {
      if(classFullName.startsWith(classname)) {
        return true;
      }
    }
    
    if(!permittedClasses.isEmpty()) {
      for(String classname : permittedClasses) {
        if(classFullName.startsWith(classname)) {
          return false;
        }
      }
      
      return true;
    } else {
      return false;
    }
    
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
      out.println("total classes instrumented:" + instrumentationPasses);
      out.println("total classes summary instrumented:" + summaryInstrumentationPasses);
      out.println("total classes failed to instrument:" + instrumentationFails);
      out.println("total classes skipped from instrument:" + skippedClasses);
      out.println("total classes:" + ((int)(instrumentationFails + instrumentationPasses + skippedClasses)));
      out.println("total classes detected:" + classesDetected); 
      out.println("source:" + source);
      out.println("destination:" + destination);
      out.println("restrictions:" + restrictions);
    }
  }

  public static  class ByteCodeHelper implements Opcodes {

    private static PrintStream byteStream;


    public static void print(byte[] bytecode, String dirStructure, String filePath) {
      final String err = "transfer of bytecode file - " + filePath + " - failed.";
      try {
        File dir = new File(dirStructure);
        dir.mkdirs();
        byteStream = new PrintStream(filePath);
        byteStream.write(bytecode);
        byteStream.close();
      } catch (FileNotFoundException e) {
        System.err.println(err);
        throw new RuntimeException(err);
      } catch (IOException e) {
        System.err.println(err);
        throw new RuntimeException(err);
      }
    }
  }

  //jar cvfm javac-i.jar META-INF/MANIFEST.MF com javax jdk && cp javac-i.jar ../javac-i.jar

}
