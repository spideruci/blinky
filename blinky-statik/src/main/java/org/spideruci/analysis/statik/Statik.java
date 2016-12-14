package org.spideruci.analysis.statik;

import static org.spideruci.analysis.statik.SootCommander.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.spideruci.analysis.statik.blocks.AnalysisBlock;
import org.spideruci.analysis.statik.blocks.CallGraphBlock;
import org.spideruci.analysis.statik.blocks.FlowBlock;
import org.spideruci.analysis.statik.calls.CallGraphManager;
import org.spideruci.analysis.statik.calls.StatikCallGraphBuilder;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.flow.StatikFlowGraph;
import org.spideruci.analysis.statik.flow.data.SourceICFG;

import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * @author vpalepu
 */
public class Statik {
  
  public static final String STARTUP_MSG = 
      "Starting ``Blinky Statik``, a static analysis framework for JVM executables."
      + "\nThis framework currently uses algorithms from Soot and Heros to"
      + "\nperform static analysis.\n";
  
  public static final String CP_SEP = System.getProperty("path.separator");
  public static final String FP_SEP = System.getProperty("file.separator");
  
  public static final String RTJAR = "/rt.jar";
  public static final String JCEJAR = "/jce.jar";
  
  public static List<SootMethod> GET_ENTRY_METHODS() {
    return Scene.v().getEntryPoints();
  }
  
  private static AnalysisConfig startup(final String[] args) {
    System.out.println(STARTUP_MSG);
    
    final String configPath = args[0];
    AnalysisConfig analysisconfig = AnalysisConfig.init(configPath);
    final String jre7path = analysisconfig.get(AnalysisConfig.JRE7_LIB);
    final String classpath = analysisconfig.get(AnalysisConfig.CLASSPATH);
    
    DebugUtil.IS_DEBUG = 
        Boolean.parseBoolean(analysisconfig.get(AnalysisConfig.DEBUG));
    
    System.out.println(jre7path);
    System.out.println(classpath);
    
    List<String> argsList = new ArrayList<>();
    argsList.addAll(Arrays.asList(new String[] {
        "-keep-line-number",
        "-cp",
//        jre7path + "/rt.jar:" + jre7path + "/jce.jar:" + "./target/test-classes/",
        ".:" + jre7path + RTJAR + ":" + jre7path + JCEJAR + ":" + classpath,
        "-w",
        analysisconfig.get(AnalysisConfig.ARG_CLASS)
    }));
    
    analysisconfig.setArgs(argsList);
    return analysisconfig;
  }
  
  public static void main(String[] args) {
    AnalysisConfig analysisconfig = startup(args);
    DummyMainManager.setupDummyMain();
            
    StatikCallGraphBuilder cgBuilder = 
        StatikCallGraphBuilder.build("call-graph", analysisconfig);

    CallGraph cg = cgBuilder.getCallGraph();
    
    CallGraphManager cgm = CallGraphManager.init(cg, GET_ENTRY_METHODS());

    List<Class<? extends AnalysisBlock>> analysisBlocks = analysisconfig.getBlocks();
    for(Class<? extends AnalysisBlock> block : analysisBlocks) {
      try {
        AnalysisBlock liveBlock = block.newInstance();
        liveBlock.execute(cgm);
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    
//    new CallGraphBlock().run(cgm);
    
    
//    new FlowBlock().run(cgm);
    
  }

}
