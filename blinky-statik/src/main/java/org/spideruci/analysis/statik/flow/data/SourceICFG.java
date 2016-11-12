package org.spideruci.analysis.statik.flow.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spideruci.analysis.db.DatabaseWriter;
import org.spideruci.analysis.db.SQLiteDB;
import org.spideruci.analysis.statik.controlflow.Graph;
import org.spideruci.analysis.statik.controlflow.Node;

public class SourceICFG {

	private Graph<String> icfg = null;
	private String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	private String dir = "./blinky-statik/";
	private String filename = "SOOT_static_analysis_" + timeStamp;
	private static Pattern FILTER = Pattern.compile("^([0-9]+):<(.*): (.*)>");
	
	public SourceICFG(Graph<String> icfg){
		this.icfg = icfg;
	}
	
	public void dumpData(){
		
		SQLiteDB db = new SQLiteDB();
		db.create(dir, filename);

		DatabaseWriter dw = db.runDatabaseWriter();
		dw.createNodeTable();
		dw.createEdgeTable();

		for(Node<String> source : icfg.getNodes()){
			if(source == null)
				continue;

			String sourceLabel = source.getLabel();
			
			Matcher sourceMatcher = FILTER.matcher(sourceLabel);
			
			if(!sourceMatcher.find()){
				System.err.println("Cannot process source label : " + sourceLabel);
				continue;
			}
			
			SourceICFGNode sourceNode = new SourceICFGNode(Integer.parseInt(sourceMatcher.group(1)), sourceMatcher.group(2), sourceMatcher.group(3));
			
			dw.insertNodeTable(sourceNode);

			for(Node<String> target : source.pointsTo()) {
				if(target == null)
					continue;
				
				String targetLabel = target.getLabel();
				
				Matcher targetMatcher = FILTER.matcher(targetLabel);
				
				if(!targetMatcher.find()){
					System.err.println("Cannot process target label : " + targetLabel);
					continue;
				}
				
				SourceICFGNode targetNode = new SourceICFGNode(Integer.parseInt(targetMatcher.group(1)), targetMatcher.group(2), targetMatcher.group(3));
				dw.insertNodeTable(targetNode);
				dw.insertEdgeTable(sourceNode, targetNode);
			}
		}
	}
}

