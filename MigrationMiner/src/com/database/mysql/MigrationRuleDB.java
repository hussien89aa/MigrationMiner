package com.database.mysql;
import java.sql.*;
import java.util.LinkedList;

import com.project.settings.DatabaseLogin;
public class MigrationRuleDB {
	
	public MigrationRuleDB() {
		 try { 
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      //System.out.println("Opened database successfully");
	}
	
	//for test only
	public static void main( String args[] ) {
		LinkedList<MigrationRule> migrationRules= new MigrationRuleDB().getMigrationRulesWithoutVersion(0);
		System.out.println("From Library\t To library\n************************************");
		for (MigrationRule migrationRule : migrationRules) {
			System.out.println(migrationRule.FromLibrary+ "\t<==>\t"+migrationRule.ToLibrary);
		}
		// get id
		int migrationRuleID= new MigrationRuleDB().getMigrationRuleID("okhttp:2.7.5","okhttp:3.4.1");
		System.out.println("MigrationRuleID=" +migrationRuleID);
	}
	
	public LinkedList<MigrationRule> getMigrationRules(){
		LinkedList<MigrationRule> migrationRules= new LinkedList<MigrationRule>();
	  Statement stmt = null;
	   try {
		   Connection c = null;
		   Class.forName("com.mysql.jdbc.Driver");  
		   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
	        
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM MigrationRules" ); // where isVaild=1
	      
	      while ( rs.next() ) {
	     	migrationRules.add(new MigrationRule(rs.getInt("ID"), rs.getString("FromLibrary"), rs.getString("ToLibrary"), rs.getInt("Frequency"), rs.getDouble("Accuracy")));
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	   } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      
	   }
	  
	  return migrationRules;
	}
	
	//This function return list of migration rules for database
	public LinkedList<MigrationRule> getMigrationRulesWithoutVersion(int isVaild){
		LinkedList<MigrationRule> migrationRules= new LinkedList<MigrationRule>();
	  Statement stmt = null;
	   try {
		   Connection c = null;
		   Class.forName("com.mysql.jdbc.Driver");  
		   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
	        
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	       //TODO: return 0 to 1
	      ResultSet rs = stmt.executeQuery( "select * from MigrationRules WHERE  isVaild="+ isVaild+" ORDER BY Frequency desc" );
	      
	      while ( rs.next() ) {
	    	  String FromLibrary= rs.getString("FromLibrary");
	    	  String[] FromLibrarySP= FromLibrary.split(":");
	    	  String ToLibrary=  rs.getString("ToLibrary");
	    	  String[] ToLibrarySP= ToLibrary.split(":");
	     	migrationRules.add(new MigrationRule(rs.getInt("ID"),
	     			   FromLibrarySP[1],
	     			 ToLibrarySP[1]
	     			, rs.getInt("Frequency"), 1.0));
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	   } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      
	   }
	  
	  return migrationRules;
	}
	
	//List of predefine migrations used for test purpose
	public LinkedList<MigrationRule> getMigrationRulesWithoutVersionClient(){
		LinkedList<MigrationRule> migrationRules= new LinkedList<MigrationRule>();
		//migrationRules.add(new MigrationRule(1, "log4j:log4j", "org.slf4j:slf4j-api", 1, 1.0));
		// migrationRules.add(new MigrationRule(351, "org.json:json","com.google.code.gson:gson",1, 1.0)); 
		// migrationRules.add(new MigrationRule(13,"com.google.code.gson:gson","com.fasterxml.jackson.core:jackson-databind",1, 1.0)); 
		//migrationRules.add(new MigrationRule(2231, "jmock:jmock", "junit:junit", 1, 1.0));
		migrationRules.add(new MigrationRule(1, "commons-logging", "slf4j", 1, 1.0));
		//migrationRules.add(new MigrationRule(1, "log4j:log4j", "org.slf4j:slf4j-log4j12", 1, 1.0));
		 //migrationRules.add(new MigrationRule(7, "testng", "junit", 1, 1.0));
		// migrationRules.add(new MigrationRule(3, "easymock", "mockito", 1, 1.0));
		return migrationRules;
	}
	
	//List of predefine migrations used for test purpose
		public LinkedList<MigrationRule> getBreakChangesMigrationRulesClient(){
			LinkedList<MigrationRule> migrationRules= new LinkedList<MigrationRule>();
			migrationRules.add(new MigrationRule(1, "spring-core", "spring-core", 1, 1.0));
			migrationRules.add(new MigrationRule(351, "junit","junit",1, 1.0)); 
			migrationRules.add(new MigrationRule(13,"slf4j","slf4j",1, 1.0)); 
			migrationRules.add(new MigrationRule(2231, "servlet-api", "servlet-api", 1, 1.0));
			migrationRules.add(new MigrationRule(1, "hibernate", "hibernate", 1, 1.0));
			migrationRules.add(new MigrationRule(1, "log4j", "log4j", 1, 1.0));
			migrationRules.add(new MigrationRule(1, "spring-security-core", "spring-security-core", 1, 1.0));
			migrationRules.add(new MigrationRule(7, "commons-collections", "commons-collections", 1, 1.0));
			migrationRules.add(new MigrationRule(3, "jackson-databind", "jackson-databind", 1, 1.0));
			migrationRules.add(new MigrationRule(3, "commons-io", "commons-io", 1, 1.0));
			// migrationRules.add(new MigrationRule(7, "testng", "junit", 1, 1.0));
			// migrationRules.add(new MigrationRule(7, "junit", "testng", 1, 1.0));
			return migrationRules;
		}
	
	// return 0 if not found
	int getMigrationRuleID(String FromLibrary, String ToLibrary){
		int migrationRuleID=0;
		  Statement stmt = null;
		   try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		      c.setAutoCommit(false);
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM MigrationRules where FromLibrary='"+ FromLibrary+"' and ToLibrary='"+ToLibrary+"' ;" );
		      
		      while ( rs.next() ) {
		    	  migrationRuleID =rs.getInt("ID") ;
		    	  break;
		      }
		      rs.close();
		      stmt.close();
	           c.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      
		   }		
				
	   return migrationRuleID;
	}
	
	public void addMigrationRule(String FromLibrary, String ToLibrary, int Frequency, Double Accuracy){
			// int ruleID=getMigrationRuleID( FromLibrary, ToLibrary);
			// if(ruleID!=0){
			//	 return;
			// }
		      Statement stmt = null;
		      try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			         c.setAutoCommit(false);
		         stmt = c.createStatement();
		         String sql = "INSERT INTO MigrationRules (FromLibrary,ToLibrary,Frequency,Accuracy) " +
		                        "VALUES ('"+FromLibrary+"', '" + ToLibrary+ "',"+Frequency +","+ Accuracy+");"; 
		         stmt.executeUpdate(sql);
		         stmt.close();
		         c.commit();
		         c.close();
		      } catch ( Exception e ) {
		         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      }
		    //  System.out.println("Records created successfully");
		   }
	
	public void updateMigrationRule(int MigrationRuleID, int isVaild){
	      Statement stmt = null;
	      try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		         c.setAutoCommit(false);
	         stmt = c.createStatement();
	         String sql = "update MigrationRules set isVaild="+ isVaild+" where ID="+ MigrationRuleID; 
	         stmt.executeUpdate(sql);
	         stmt.close();
	         c.commit();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	    //  System.out.println("Records created successfully");
	   }


	
}
