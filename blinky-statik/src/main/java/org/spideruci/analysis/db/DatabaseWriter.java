package org.spideruci.analysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.spideruci.analysis.statik.flow.data.SourceICFGNode;

import soot.Unit;

public class DatabaseWriter {
	private Connection c = null;
	private Map<SourceICFGNode, Integer> sourceICFGNodeIdMap = new HashMap<>();
	private Map<Unit, Integer> ICFGNodeIdMap = new HashMap<>();
	private int nodeId = 0; 

	public DatabaseWriter(Connection c) {
		this.c = c;
	}
	
	private int getSourceICFGNodeId(SourceICFGNode node){

		int id;
		if(sourceICFGNodeIdMap.get(node) == null){
			id = nodeId++;
			sourceICFGNodeIdMap.put(node, id);
		}
		else{
			id = sourceICFGNodeIdMap.get(node);
		}
		return id;
	}
	
	private int getICFGNodeId(Unit node){

		int id;
		if(ICFGNodeIdMap.get(node) == null){
			id = nodeId++;
			ICFGNodeIdMap.put(node, id);
		}
		else{
			id = ICFGNodeIdMap.get(node);
		}
		return id;
	}
	
	public void createSourceICFGEdgeTable(){
		String edgeTable="CREATE TABLE IF NOT EXISTS `EDGE` ( "
				+ "`SOURCE_ID`		INTEGER,"
				+ "`TARGET_ID`	    INTEGER"			
				+ ");";

		execute(edgeTable);
		
		System.out.println("Create EDGE table");
	}

	public void insertSourceICFGEdgeTable(SourceICFGNode source, SourceICFGNode target){
		System.out.println("Insert Edge");
		String sql = "INSERT INTO EDGE "
				+"VALUES(?,?)";
		executePsmt(sql, getSourceICFGNodeId(source), getSourceICFGNodeId(target));
	}
	
	public void createSourceICFGNodeTable(){
		String sourceICFGNodeTable="CREATE TABLE IF NOT EXISTS `NODE` ( "
				+ "`ID`				INTEGER,"
				+ "`CLASS`			TEXT,"
				+ "`METHOD`			TEXT,"
				+ "`RETURN_METHOD`	TEXT,"
				+ "`LINE_NUM`		INTEGER"			
				+ ");";
		
		execute(sourceICFGNodeTable);
		
		System.out.println("Create SourceICFG Node table");
	}
	
	public void insertSourceICFGNodeTable(SourceICFGNode node){
		
		if(sourceICFGNodeIdMap.containsKey(node))
			return;
		
		System.out.println("Insert node: " + node.getClassName() + " " + node.getMethodName() + " " + node.getLineNum());
		String[] methodName = node.getMethodName().split(" ");
		String sql = "INSERT INTO NODE "
				+"VALUES(?,?,?,?,?)";
		executePsmt(sql, getSourceICFGNodeId(node), node.getClassName(), methodName[1], methodName[0], node.getLineNum());
	}
	
	public void createICFGNodeTable(){
		String ICFGNodeTable="CREATE TABLE IF NOT EXISTS `NODE` ( "
				+ "`ID`				INTEGER,"
				+ "`UNIT`			TEXT"		
				+ ");";
		
		execute(ICFGNodeTable);
		
		System.out.println("Create ICFG Node table");
	}
	
	public void insertSourceICFGNodeTable(Unit node){
		
		if(ICFGNodeIdMap.containsKey(node))
			return;
		
		System.out.println("Insert node: " + node.toString());
		String sql = "INSERT INTO NODE "
				+"VALUES(?,?)";
		executePsmt(sql, getICFGNodeId(node), node.toString());
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
