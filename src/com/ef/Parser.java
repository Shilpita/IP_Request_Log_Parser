package com.ef;

import java.sql.*;
import java.text.*;
import java.util.*;

public class Parser {
	private static  ConnectToDB connectDBCon ;
	private static Connection con ;
	private PreparedStatement stmt ;
	private ResultSet rs;
	private String query;
	
	/** 
	 * Inner class for parameters.
	 */
	 public static class Params {
		    public static final String ACCESS_LOG = "accesslog";
		    public static final String DURATION = "duration";
		    public static final String THRESHOLD = "threshold";
		    public static final String STARTDATE = "startDate";
	 }
	 
	 /**
	  * The function formats the command line arguments into a hashmap with the arguments values
	  * @param args
	  * @return HashMap of argument values
	  */
	  public static HashMap<String, String> argumentParser(String args[]) {
		    final HashMap<String, String> parameters = new HashMap<String, String>();
		    for (int i = 0; i < args.length; i++) {
		      String keyValue[] = args[i].split("=");
		      if (keyValue.length == 2 && keyValue[0].startsWith("--")) {
		    	  parameters.put(keyValue[0].substring(2), keyValue[1]);
		      } else {
		        System.err.println("Invalid argument");
		      }

		    }
		    return parameters;
	  }
	 
	/**
     * Query to get IP with request count more than given threshold for input duration.
     * Insert the IP exceeding threshold requests into Blocked ip table.
     * @param con, start time, duration , threshold
     * @return list of blocked ip
     */
	
    public ArrayList<String> getExceededHitsIP(Connection con, String startDate, String duration , int threshold){ 
    	ArrayList<String> iplist = new ArrayList<String>();
    	Timestamp startTime = null, endTime = null;
    	
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
    	
		try {
			    java.util.Date startdate = dateformat.parse(startDate);
			    startTime = new Timestamp(startdate.getTime());
			    
		    	Calendar cal = Calendar.getInstance();
		    	cal.setTime(startdate);
		    	
		    	// increment the end time as per the hourly or daily duration
		    	if(duration.equals("hourly")){
		    		cal.add(Calendar.HOUR, 1);
		    	}else if(duration.equals("daily")){
		    		cal.add(Calendar.HOUR, 24);
		    	}
		    	endTime = new Timestamp(cal.getTimeInMillis());
		    	
		    	//System.out.println(startTime + "\n"+ endTime);
		    	
		    	//Constructed query to get the IP with exceeded count
    			query = "SELECT  IP_Address AS IP, COUNT( IP_Address ) AS count "
    	        		+ "FROM ACCESS_LOG WHERE  Date BETWEEN  ? AND  ? GROUP BY  IP_Address "
    	        		+ "HAVING count > ?";
    			
    	        stmt  = con.prepareStatement(query);
    	        stmt.setString(1, dateformat.format(startTime));
    	        stmt.setString(2, dateformat.format(endTime));
    	        stmt.setLong(3, threshold);
    			rs	  = stmt.executeQuery();
		    			
    			//System.out.println("executed ");
    			
    			
    			while(rs.next()){
    				//Print the retrieved IP address with exceeded count to console
    				System.out.println(rs.getString(1));	
    				iplist.add(rs.getString(1));
    				
    				//Insert the retrieved IP address with exceeded count SQL table for blocked IP
    				query = "INSERT INTO BLOCKED_IP_ADDRESS VALUES(?,?)";
    				stmt  = con.prepareStatement(query);
    				stmt.setString(1, rs.getString(1));
    				stmt.setString(2, "Exceeded "+ duration +" threshold request count of "+ threshold);
    				stmt.executeUpdate();
    				stmt.close();
    			}
    	} catch ( ParseException | SQLException e) {
			e.printStackTrace();
    	} 
		
		return iplist;
    }
    
    
	public static void main(String[] args) {
		
		if(args.length < 4){
			System.out.println("ERROR! INSUFFICIENT ARGUMENTS");
		}
		// Format the commandline arguments
		 Map<String, String> arguments = argumentParser(args);
		 String filePath = arguments.get(Params.ACCESS_LOG);
		 String startDate= arguments.get(Params.STARTDATE);
		 String duration = arguments.get(Params.DURATION);
		 String threshold= arguments.get(Params.THRESHOLD);
		 
		 
		// Load data from log to DB
		InsertToDB log = new InsertToDB();
		log.parseLoadLog(filePath);
		
		//Query DB to get the IP which exceed threshold requests
		Parser par = new Parser();
    	    try {
				connectDBCon    = new ConnectToDB();
				con             = connectDBCon.openConnection();
				ArrayList<String> list = par.getExceededHitsIP(con, startDate, duration, Integer.parseInt(threshold));
				//ArrayList<String> list = ba.getExceededHitsIP(con, "2017-01-01.13:00:00" , "hourly", 200) ;
				//ArrayList<String> list = ba.getExceededHitsIP(con, args[1] , args[2], Integer.parseInt(args[3])) ;
			} catch (ClassNotFoundException | SQLException e) {

				e.printStackTrace();
			}
    	   
	}

}
