package org.spideruci.analysis.tacoco;

import java.nio.file.Path;

import org.spideruci.tacoco.analysis.TacocoAnalyzer;

public class BlinkyAnalyzer extends TacocoAnalyzer {

  @Override
  public void setup() {
    super.setup();
    Path defaultTrace = BlinkyListener.getDefaultTrace();
    
    // expect defaultTraceName to be project name
    final String defaultTraceName = defaultTrace.getFileName().toString();
    final String projectName = 
        defaultTraceName.substring(0, defaultTraceName.lastIndexOf('.'));
    
    final String newTraceName = projectName + "-00000.trc";
    Path newTrace = BlinkyListener.getNewTracePath(defaultTrace, newTraceName);
    BlinkyListener.setOut(newTrace);
  }
  
  @Override
  public String getName() {
    return "BLINKY";
  }

}
