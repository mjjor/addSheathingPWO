/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadsheathingpwo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mjordan
 */
public class ConnectAdjutant {
  private static Connection adjConn;
  private static String ADJ_DRIVER       = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private static String ADJ_CONNECTION   = "jdbc:sqlserver://98.129.206.13:1433;databaseName=Magest";
  private static String ADJ_USER         = "magestsql";
  private static String ADJ_PASSWORD     = "magest1";
    
    public static Connection connect()  throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
       try{
            Class.forName(ADJ_DRIVER).newInstance();
       } 
       catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
           System.out.println("Error : " + e);
       }
        adjConn = DriverManager.getConnection(ADJ_CONNECTION, ADJ_USER, ADJ_PASSWORD);
        return adjConn;
    }
   
    public static Connection getConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	        if(adjConn !=null && !adjConn.isClosed()) {
                  return adjConn;
        }
	        connect();
	        return adjConn;
	    }   
}
