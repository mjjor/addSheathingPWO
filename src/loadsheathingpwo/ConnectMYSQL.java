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
public class ConnectMYSQL {
   private static Connection mySqlConn; 
   private static String MYSQL_DRIVER     = "com.mysql.jdbc.Driver";
   private static String MYSQL_CONNECTION = "jdbc:mysql://192.168.0,170:3306/hsbCAD_DEV2";
   private static String MYSQL_USER       = "root";
   private static String MYSQL_PASSWORD   = "jym0814MJJ";
    
    public static Connection connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
       try{
            Class.forName(MYSQL_DRIVER).newInstance();
       } 
       catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
           System.out.println("Error : " + e);
       }
        mySqlConn = DriverManager.getConnection(MYSQL_CONNECTION, MYSQL_USER, MYSQL_PASSWORD);
        return mySqlConn;
    }
   
    public static Connection getConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	        if(mySqlConn !=null && !mySqlConn.isClosed()) {
                  return mySqlConn;
        }
	        connect();
	        return mySqlConn;
	    }   
}
