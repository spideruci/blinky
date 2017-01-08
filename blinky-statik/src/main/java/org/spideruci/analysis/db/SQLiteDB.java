package org.spideruci.analysis.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteDB {

	private static Connection c = null;

	public static void openConnection(String fileName){
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + fileName);

			System.out.println("Opened database successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void create(String root, String name){
		File output = new File(root);
		String db = output + name + ".db";

		File dbFile = new File(db);

		if(dbFile.exists()){
			dbFile.delete();
		}
		openConnection(db);
	}

	public void open(String root){
		File db = new File(root);

		if(!db.exists()){
			System.out.println("No database!");
			return;
		}
		openConnection(root);
	}
	
	public DatabaseWriter runDatabaseWriter(){
		DatabaseWriter dw = new DatabaseWriter(c);
		
		return dw;
	}

	public DatabaseReader runDatabaseReader(){
		DatabaseReader dr = new DatabaseReader(c);
		
		return dr;
	}
	
}
