package org.spideruci.analysis.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.spideruci.analysis.statik.DebugUtil;
import org.spideruci.analysis.statik.Items;
import org.spideruci.analysis.statik.SootCommander;
import org.spideruci.analysis.statik.calls.CallGraphManager;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.UnitGraph;

public class WriteToText {

	private String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	private String dir = System.getProperty("user.dir");
	private String filename = timeStamp + ".txt";

	public WriteToText() {
		// TODO Auto-generated constructor stub
	}


	public void dump(CallGraphManager cgm){
		String name = dir + "/CallGraph" + filename;

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(name));

			ArrayList<SootMethod> worklist = new ArrayList<>();
			ArrayList<SootMethod> visited = new ArrayList<>();

			for(SootMethod entrypoint : cgm.getEntryPoints()) {
				worklist.add(entrypoint);
				System.out.println(entrypoint.toString());
			}	    

			while(!worklist.isEmpty()) {

				SootMethod srcMethod = worklist.remove(0);
				
				if(methodIsInvalid(srcMethod)) {
					continue;
				}

				visited.add(srcMethod);

				
				Items<Edge> outEdges = new Items<>(cgm.getCallgraph().edgesOutOf(srcMethod));

				for(Edge edge : outEdges) {
					SootMethod tgtMethod = edge.tgt();

					if(methodIsInvalid(tgtMethod))
						continue;
					
					bw.write(srcMethod.toString());
					bw.write("\t↪\t" +tgtMethod.toString());
					bw.newLine();
					
//					Body tgtBody = tgtMethod.retrieveActiveBody();
//
//					Unit callUnit = edge.srcUnit();
//					Unit entryUnit = getEntryUnit(tgtBody);
//					//	        int tgtStartLine = tgtMethod.getJavaSourceStartLineNumber();
//					//	        entryUnit.addTag(new LineNumberTag(tgtStartLine));
//
//					List<Unit> tgtExitUnits = SootCommander.GET_UNIT_GRAPH(tgtMethod).getTails();
//
//					icfgMgr.addIcfgEdge(callUnit, srcMethod, entryUnit, tgtMethod);
//
//					for(Unit tgtExit : tgtExitUnits) {
//						icfgMgr.addIcfgEdge(tgtExit, tgtMethod, callUnit, srcMethod);
//					}

					if(!visited.contains(tgtMethod) && !worklist.contains(tgtMethod)) {
						worklist.add(tgtMethod);
					}
					bw.newLine();
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Saved " + name);

	}


	public <T> void dump(Graph<T> graph){
		Collection<Node<T>> shadowMethodNodes = graph.getNodes();

		String name = dir + "/Graph" + filename;

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(name));

			for(Node<T> visitedNode : shadowMethodNodes) {
				if(visitedNode == graph.startNode()
						|| visitedNode == graph.endNode())
					continue;

				T m = visitedNode.getBody();

				if(m == null)
					continue;


				if(m instanceof SootMethod)
					if(!((SootMethod) m).isConcrete())
						continue;

				ArrayList<Node<T>> neighbors = visitedNode.pointsTo();

				for(Node<T> neighbor : neighbors) {
					if(neighbor == null)
						continue;

					if(visitedNode != neighbor){
						bw.write(visitedNode.getLabel());
						bw.write("\t↪\t" + neighbor.getLabel());
						bw.newLine();
					}
				}
				bw.newLine();
				bw.newLine();
			}
			bw.flush();
			bw.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Saved " + name);
	}


	private boolean methodIsInvalid(SootMethod method) {
		return method == null 
				|| !method.isConcrete() 
				|| methodBodyIsInvalid(method.retrieveActiveBody());
	}

	private boolean methodBodyIsInvalid(Body body) {
		PatchingChain<Unit> units = body == null ? null : body.getUnits();
		return units == null || units.isEmpty();
	}
	

}
