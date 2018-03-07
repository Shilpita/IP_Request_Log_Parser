package com.ef;

/**
 * The class to connect to RDBMS MySQL through JDBC 
 * Function to Open and Close Connection
 * @author Shilpita Roy
 *
 */

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectToDB {
	   public String smtInsert ;
	   public Connection con ;
	  // public PreparedStatement preparedStatement ;
	  
	   public ConnectToDB() throws ClassNotFoundException, SQLException{
		   this.con    		 = null;
		   this.smtInsert    = null;      
	  //   this.preparedStatement    = null;
	   } 
	   
       /** 
        * A function to construct DB URL and setup connection using DB user access.
        * @return a database connection 
        * @throws java.sql.SQLException when there is an error when trying to connect database 
        * @throws ClassNotFoundException when the database driver is not found. 
        */ 
	   
       public Connection openConnection() throws SQLException, ClassNotFoundException { 
    	   Class.forName("com.mysql.jdbc.Driver");
    	   
           /* 
           Here is the information needed when connecting to a database server.
           These values are now hard-coded in the program. In general, they should be stored in some 
           configuration file and read at run time. 
           */ 
    	   
           String host     = "138.68.242.150";
           String port     = "3306"; 
           String dbName   = "WB_LogAccess_DB"; 
           String userName = "root"; 
           String password = "EXTTZKja8l"; 
    
          // Construct the JDBC URL 
          String dbURL = "jdbc:mysql://" + host + ":" + port + "/" + dbName; 
          System.out.println("\n Open connection");
          return DriverManager.getConnection(dbURL, userName, password); 
      } 
     
      
      /** 
       * Close the database connection 
       * @param con 
       */ 
       
      public void closeConnection(Connection con) { 
          try { 
              con.close(); 
              System.out.println("\n Closed Connection");
          } catch (SQLException e) { 
              System.err.println("Cannot close connection: " + e.getMessage()); 
          } 
      }
      
      /*
      public static void main(String[] args) { 
    	    ConnectToDB dbcon = null;
			try {
				dbcon = new ConnectToDB();
				dbcon.con             = dbcon.openConnection();
			    dbcon.smtInsert         = "INSERT INTO TEST_TABLE VALUES (?,?)" ;
				dbcon.preparedStatement       = dbcon.con.prepareStatement(dbcon.smtInsert );
			
				dbcon.preparedStatement.setInt(1, 2);
				dbcon.preparedStatement.setString(2, "Shilpita Roy");
				dbcon.preparedStatement.executeUpdate();
				dbcon.preparedStatement.close();
				
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}finally {
				if (dbcon.con != null)
					dbcon.closeConnection(dbcon.con);
			}
	 } 
     */ 
  }