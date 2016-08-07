package org.spideruci.analysis.statik;

import java.util.ArrayList;
import java.util.List;

import soot.ArrayType;
import soot.Body;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.Jimple;

public class DummyMainManager {
  
  public static SootClass getMainClass() {
    List<Type> parameterTypes = new ArrayList<>();
    parameterTypes.add(ArrayType.v(RefType.v("java.lang.String"), 1));
    
    
    SootClass dummyClass = new SootClass("dummyClass");
    dummyClass.setModifiers(Modifier.PUBLIC);
    
    SootMethod dummyMain = new SootMethod("main", parameterTypes, soot.VoidType.v());
    dummyMain.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
    
    Body mainBody = Jimple.v().newBody(dummyMain);
    dummyMain.setActiveBody(mainBody);
    dummyMain.setDeclaringClass(dummyClass);
    
    dummyClass.addMethod(dummyMain);
    return dummyClass;
  }
  
  public static void setupDummyMain() {
    Scene.v().setMainClass(DummyMainManager.getMainClass());
  }

}
