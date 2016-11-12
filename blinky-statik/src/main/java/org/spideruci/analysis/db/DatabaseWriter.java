package org.spideruci.analysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.spideruci.analysis.statik.flow.data.SourceICFGNode;

public class DatabaseWriter {
	private static Connection c = null;
	private static Map<SourceICFGNode, Integer> nodeIdMap = new HashMap<>();
	private static int nodeId = 0; 

	public DatabaseWriter(Connection c) {
		this.c = c;
	}
	
	private int getNodeId(SourceICFGNode node){

		int id;
		if(nodeIdMap.get(node) == null){
			id = nodeId++;
			nodeIdMap.put(node, id);
		}
		else id = nodeIdMap.get(node);
		return id;
	}
	
	public void createEdgeTable(){
		String edgeTable="CREATE TABLE IF NOT EXISTS `EDGE` ( "
				+ "`SOURCE_ID`		INTEGER,"
				+ "`TARGET_ID`	    INTEGER"			
				+ ");";

		execute(edgeTable);
		
		System.out.println("Create EDGE table");
	}

	public void insertEdgeTable(SourceICFGNode source, SourceICFGNode target){
		System.out.println("Insert Edge");
		String sql = "INSERT INTO EDGE "
				+"VALUES(?,?)";
		executePsmt(sql, getNodeId(source), getNodeId(target));
	}
	
	public void createNodeTable(){
		String nodeTable="CREATE TABLE IF NOT EXISTS `NODE` ( "
				+ "`ID`			INTEGER,"
				+ "`CLASS`		TEXT,"
				+ "`METHOD`		TEXT,"
				+ "`LINE_NUM`	INTEGER"			
				+ ");";
		
		execute(nodeTable);
		
		System.out.println("Create Node table");
	}
	
	public void insertNodeTable(SourceICFGNode node){
		
		if(nodeIdMap.containsKey(node))
			return;
		
		System.out.println("Insert node: " + node.getClassName() + " " + node.getMethodName() + " " + node.getLineNum());
		String sql = "INSERT INTO NODE "
				+"VALUES(?,?,?,?)";
		executePsmt(sql, getNodeId(node), node.getClassName(), node.getMethodName(), node.getLineNum());
	}
	
	protected void execute(String query){
		try {
			Statement s = c.createStatement();
			s.execute(query);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	protected void executePsmt(String sql, Object... args) {
		try{
			PreparedStatement psmt = c.prepareStatement(sql);
			for(int i=0; i<args.length; ++ i){
				psmt.setObject(i+1, args[i]);
			}
			psmt.executeUpdate();
			psmt.close();
		}catch(Exception e){
			System.out.println(sql);
			e.printStackTrace();
		}
	}
	
}
