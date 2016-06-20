package org.spideruci.analysis.tacoco;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.spideruci.analysis.dynamic.Profiler;
import org.spideruci.tacoco.cli.AbstractCli;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;

public class BlinkyListener implements ITacocoTestListener {
  
  public static void resetOut() {
    Profiler.REAL_OUT = System.out;
  }
  
  public static void setOut(Path path) {
    try {
      PrintStream out = new PrintStream(path.toString());
      Profiler.REAL_OUT = out;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
  }
  
  public static Path getDefaultTrace() {
    String outdir = System.getProperty(AbstractCli.OUTDIR);
    String project = System.getProperty(AbstractCli.PROJECT);
    Path defaultTrace = Paths.get(outdir).resolve(project + ".log");
    return defaultTrace;
  }
  
  public static Path getNewTracePath(Path defaultLog, String traceName) {
    Path outlog = defaultLog.resolveSibling(traceName);
    return outlog;
  }
  
  Path defaultTrace;
  int count = 0;
  final String project = System.getProperty(AbstractCli.PROJECT);
  

  @Override
  public void onStart() {
    defaultTrace = getDefaultTrace();
  }

  @Override
  public void onTestStart(String testName) {
    count += 1;
    String traceName = 
        String.format("%s-%05d-%s-%s", project, count, testName, ".trc");
    Path tracePath = getNewTracePath(defaultTrace, traceName);
    setOut(tracePath);
    Profiler.unsetGuard1();
  }

  @Override
  public void onTestPassed() {
    // not yet supported 
    
  }

  @Override
  public void onTestFailed() {
    // not yet supported
    
  }

  @Override
  public void onTestSkipped() {
    // not yet supported
    
  }

  @Override
  public void onTestEnd() {
    Profiler.setGuard1();
    resetOut();
  }

  @Override
  public void onEnd() {
    resetOut();
  }

}
