/*
@author Charles.Y.Feng
@date May 12, 2016 3:48:22 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <h2>Style Requirement: PersistentTables</h2> 
 * <p> In this style, we want to model and store the data so that it is </br> 
 * amenable to future retrieval in all sorts of ways. For the term-frequency </br> 
 * task, if it's done several times, we might not always want to read and parse the </br> 
 * file in its raw form everytime we want to compute the term frequency. Also, we </br> 
 * might want to mine for more facts about the books, not just term frequencies. </br> 
 * So, instead of consuming the data in its raw form, this style encourages using </br> 
 * alternative representations of the input data that make it easier to mine, now </br> 
 * and in the future. In one approach to such goal, the types of data (domains) </br> 
 * that need to be stored are identified, and pieces of concrete data are associated </br> 
 * with those domains, forming tables. With an unambiguous entity-relationship </br> 
 * model in place, we can then fill in the tables with data, so to retrieve portions </br> 
 * of it using declarative queries.
 * </p>
 */
public class PersistentTablesWriter {

  public void create_db_schema(Connection connection) throws Exception{
    Statement s = connection.createStatement();
    s.execute("CREATE TABLE documents (id INTEGER PRIMARY KEY AUTOINCREMENT, name)");
    s.execute("CREATE TABLE words (id, doc_id, value)");
    s.execute("CREATE TABLE characters (id, word_id, value)");
    connection.commit();
  }

  @SuppressWarnings("unchecked")
  public void load_file_into_database(String path_to_file, Connection connection) throws Exception{
    List<String> word_list= (ArrayList<String>)(new IOFunction(){
      @Override
      public Object func(Object o) {
        BufferedReader br;
        List<String> all_words = new ArrayList<String>();
        try {
          br = new BufferedReader(new FileReader((String)o));
          StringBuilder sb = new StringBuilder();
          int v = -1;
          while ((v = br.read()) != -1) {
            sb.append((char) v);
          }
          String data = sb.toString();
          data = data.replaceAll("([\\W_])+", " ").toLowerCase();
          all_words.addAll(Arrays.asList(data.split(" ")));
          Set<String> stop_words = new HashSet<String>();
          br = new BufferedReader(new FileReader(Config.stopWordsPath));
          sb = new StringBuilder();
          v = -1;
          while ((v = br.read()) != -1) {
            sb.append((char) v);
          }
          stop_words.addAll(Arrays.asList(sb.toString().split(",")));
          for (char ch = 'a'; ch <= 'z'; ch++) {
            stop_words.add(ch + "");
          }

          for(int i=0;i<all_words.size();i++){
            if(stop_words.contains(all_words.get(i))){
              all_words.remove(i);
              i--;
            }
          }
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return all_words;
      }
    }).func(path_to_file);

    PreparedStatement pps = connection.prepareStatement("INSERT INTO documents(name) VALUES(?)");
    pps.setString(1, path_to_file);
    pps.execute();
    connection.commit();
    pps = connection.prepareStatement("SELECT ? from documents WHERE name = ?");
    pps.setString(1, "id");
    pps.setString(2, path_to_file);
    ResultSet rs = pps.executeQuery();

    int doc_id = rs.getInt(1);

    connection.commit();
    int word_id = 0;
    for(String w: word_list){
      StringBuilder word_sql = new StringBuilder();
      word_sql.append("INSERT INTO words VALUES").append("(?, ?, ?)");
      PreparedStatement word_sql_pps = connection.prepareStatement(word_sql.toString());
      word_sql_pps.setInt(1, word_id++);
      word_sql_pps.setInt(2, doc_id);
      word_sql_pps.setString(3, w);
      word_sql_pps.executeUpdate();
    }
    connection.commit();
  }

  // 	public static void main(String[] args){
  // 		PersistentTables pt = new PersistentTables();
  // 		Connection connection = null;  
  // 		try {
  // 			String dbPath = "D:\\testdb.db";
  // 			connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
  // 			connection.setAutoCommit(false);
  // 			pt.create_db_schema(connection);
  // 			pt.load_file_into_database("D:/KLive/GitRepository/inf212hw/src/pride-and-prejudice.txt", connection);
  // 			connection.close();
  // 		} catch (SQLException e) {
  // 			// TODO Auto-generated catch block
  // 			e.printStackTrace();
  // 		} catch (Exception e) {
  // 			// TODO Auto-generated catch block
  // 			e.printStackTrace();
  // 		}  
  // 	}
}

interface IOFunction{
  Object func(Object o);
}
