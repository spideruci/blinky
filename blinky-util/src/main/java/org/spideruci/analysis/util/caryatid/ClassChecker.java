package org.spideruci.analysis.util.caryatid;
import org.objectweb.asm.util.CheckClassAdapter;


public class ClassChecker {

	public static void main(String[] args) throws Exception {
		String x = "HelloWorld/Namaste";
		x = "org/apache/fop/apps/CommandLineStarter";
		x = "org/apache/fop/fo/expr/Numeric";
		//String[] args1 = {"/home/vijay/ProgramAnalysis/Scripts/results2/CollectedClasses/" + x.replace('/', '+').replace('$', '-') + ".class"};
		String[] args2 = {"/home/vijay/ProgramAnalysis/Scripts/Instrumenter/results/CollectedClasses2/" + x.replace('/', '+').replace('$', '-') + "2.class"};
		//CheckClassAdapter.main(args1);		
		CheckClassAdapter.main(args2);
	}

}
