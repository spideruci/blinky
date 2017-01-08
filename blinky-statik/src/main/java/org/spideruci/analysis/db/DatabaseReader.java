package org.spideruci.analysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseReader {
	private Connection c = null;
	HashMap<String, HashSet<String>> classMethod = new HashMap<>();
	
	public DatabaseReader(Connection c) {
		this.c = c;
	}

	public HashMap<String, HashSet<String>> getClassMethod(){
		return classMethod;
	}
	
	public void getTestCase(){
		Statement s;
		
		try {
			s = c.createStatement();
			String sql = "SELECT FQN FROM TESTCASE";
			ResultSet rs = s.executeQuery(sql);
			
			while(rs.next()){
				String fqn = rs.getString("FQN");
				
				// value in fqn is testMethodName(packageName)
				
				int index = fqn.indexOf('(');
				
//				String testMethodName = fqn.substring(0, index).replaceAll("\\[.*\\]", "");
				String testMethodName = fqn.substring(0, index).replaceAll("\\W.*", "");
				String packageName = fqn.substring(index + 1, fqn.length() - 1);
				
//				System.out.println(packageName + "   " + testMethodName);
				
				if(classMethod.get(packageName) == null)
					classMethod.put(packageName, new HashSet<String>());
				
				classMethod.get(packageName).add(testMethodName);
			}
			s.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected ResultSet executePsmt(String sql, Object... args) {
		ResultSet rs = null;
		
		try{
			PreparedStatement psmt = c.prepareStatement(sql);
			for(int i=0; i<args.length; ++ i){
				psmt.setObject(i+1, args[i]);
			}
			rs = psmt.executeQuery();			
			psmt.close();
		}catch(Exception e){
			System.out.println(sql);
			e.printStackTrace();
		}
		return rs;
	}
	
}
