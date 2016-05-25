/*
@author Charles.Y.Feng
@date May 12, 2016 3:46:26 PM
 */

package org.spideruci.analysis.diagnostics.subjects.styleEx;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
public class PersistentTablesReader {
  public static void main(String[] args) {
    PersistentTablesReader ptr = new PersistentTablesReader();
    PersistentTablesWriter ptw = new PersistentTablesWriter();
    Connection connection = null;
    try {
      String dbPath = "tf.db";
      File f = new File(dbPath);
      if(!f.exists()){
        connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
        connection.setAutoCommit(false);
        ptw.create_db_schema(connection);
        ptw.load_file_into_database(Config.bookPath, connection);
        connection.close();
      }

      if (f.exists() && !f.isDirectory()) {
        connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
        connection.setAutoCommit(false);
        String res = ptr.read_word_freqs(connection);
        System.out.print(res);
        connection.close();
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String read_word_freqs(Connection connection) throws SQLException{
    StringBuilder sb = new StringBuilder();
    Statement stat = connection.createStatement();
    ResultSet rs = stat.executeQuery("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC");
    for (int i = 0; i < 25; i++) {
      rs.next();
      sb.append(rs.getString(1) + "  -  "
          + rs.getString(2)+"\n");
    }

    return sb.toString();
  }
}

