package com.ef;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The class to insert the data to DB from Access log file
 * @author Shilpita Roy
 *
 */
public class InsertToDB {
	private static String file ;
	private static String line ;
	private static  ConnectToDB connectDBCon;
	private static Connection con ;
	private PreparedStatement preparedStatement ;
	private String insertStmt ;
	
	public InsertToDB() {
		this.file = null; //"access.log";
		this.line = null;
	}
	///Create Insert Statement
	public void insertLog(String[] tokens){
			try {
					insertStmt              = "INSERT INTO ACCESS_LOG VALUES(?,?,?,?,?)" ;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					java.util.Date date = format.parse(tokens[0]);
					Timestamp sqlDate = new Timestamp(date.getTime());  
				    //System.out.println(sqlDate);  
				    
					preparedStatement       = con.prepareStatement(insertStmt);
					preparedStatement.setTimestamp(1, sqlDate);   
					preparedStatement.setString(2, tokens[1]);
					preparedStatement.setString(3, tokens[2]);
					preparedStatement.setLong(4, Long.parseLong(tokens[3]));
					preparedStatement.setString(5, tokens[4]);
					//preparedStatement.setString(5, tokens[4]);
					
					preparedStatement.executeUpdate();
					preparedStatement.close();
					
			} catch (ParseException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
	}
	
	public void parseLoadLog(String filename){
	//public static void main(String[] args) { 
		   //InsertToDB log = new InsertToDB();
	       try {
	    	    connectDBCon    = new ConnectToDB();
	    	   	con             = connectDBCon.openConnection();
	    	   	FileInputStream fstream  = new FileInputStream(filename);
	    	   	BufferedReader br 		 = new BufferedReader(new InputStreamReader(fstream));
	    	   	String delimiter = "\\|";
	    	  //Read File Line By Line
                while ((line = br.readLine()) != null)   {
								String[] tokens = line.split(delimiter);
								//System.out.println(tokens[0] +"\t "+tokens[1]);
								//log.insertLog(tokens);
								insertLog(tokens);
                }
	       } catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException | SQLException ex) {
				ex.printStackTrace();
			} finally {
					if (con != null)
					   connectDBCon.closeConnection(con);
			}
	}
}
