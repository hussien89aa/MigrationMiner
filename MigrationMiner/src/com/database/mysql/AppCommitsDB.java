package com.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import com.project.settings.DatabaseLogin;
import com.subversions.process.Commit;

public class AppCommitsDB {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//new AppCommitsDB().addAppCommit(1, "1234","12 12 2012","hussein" );
		System.out.println("ID:"+ new AppCommitsDB().getAppCommit("1234"));
	}
	
	public void addAppCommit(int AppID, Commit commit){
	  	       //boolean isfoundCommit=getAppCommit( commit.commitID);
		  	 //  if(isfoundCommit==true){
		  		//   return;
		  	 //  }
		  	   
		      //Statement stmt = null;
		      try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
				     //Class.forName("org.sqlite.JDBC");
			         
			        // c = DriverManager.getConnection("jdbc:sqlite:DataSet/Sqlite/FunctionMappingDB.db");
			         c.setAutoCommit(false);
		        // stmt = c.createStatement();
		         String sql = "INSERT INTO AppCommits (AppID,CommitID,CommitDate,DeveloperName,CommitText) " +
		                        "VALUES (?,?,?,?,?);"; 
		         PreparedStatement stmt = c.prepareStatement
		        	      (sql);
		        	      stmt.setInt(1,AppID);
		        	      stmt.setString(2,commit.commitID);
		        	      stmt.setString(3,commit.commitDate);
		        	      stmt.setString(4, commit.developerName);
		        	      stmt.setString(5,commit.commitText);
		        	      stmt.executeUpdate();
		         
		         //stmt.executeUpdate(sql);
		         stmt.close();
		         c.commit();
		         c.close();
		      } catch ( Exception e ) {
		         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      }
		      
		      /* TODO: save all commit files changes for now we donot use them
		      
		      for (String filePath : commit.filePath) {
		    	  new CommitFileChanges().addCommitFile(commit.commitID, filePath);
			   }
		     */
		   }
		
		public boolean getAppCommit(String CommitID){
			 boolean isFound=false;
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "SELECT * from AppCommits where CommitID='"+ CommitID +"'");
			     
			      while ( rs.next() ) {
			    	  isFound=true;
			    	  break;
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return isFound;

			}
		
		public ArrayList<AppCommit> getAllCommits(){
			ArrayList<AppCommit> listOfAppsCommit = new ArrayList<AppCommit>();
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "SELECT * from AppCommits where AppID in(SELECT ProjectsID from ProjectLibraries GROUP BY ProjectsID)");
			     
			      while ( rs.next() ) {
			    	  listOfAppsCommit.add(new AppCommit( rs.getInt("AppID"),  rs.getString("CommitID")));
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return listOfAppsCommit;

			}
		
		public ArrayList<AppCommit> getAllCommitsHasMigration(String fromLibrary, String toLibrary, int migrationRuleID){
			ArrayList<AppCommit> listOfAppsCommit = new ArrayList<AppCommit>();
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "SELECT AppID, CommitID FROM AppCommits WHERE CommitText LIKE '%"+ fromLibrary+"%' and CommitText LIKE '%"+ toLibrary+"%' and  AppID not in( SELECT AppID FROM MigrationSegments WHERE MigrationRuleID="+ migrationRuleID+" GROUP BY AppID  ) ");
			     
			      while ( rs.next() ) {
			    	  listOfAppsCommit.add(new AppCommit( rs.getInt("AppID"),  rs.getString("CommitID")));
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return listOfAppsCommit;

			}
		
		public String previousCommitID(int AppID,String CommitID){
			// get prevouius commit number
			String[] commitIDsp= CommitID.split("_");
			int prevCommitNumber=Integer.parseInt(commitIDsp[0].substring(1))-1 ;
			String prevCommitID="";
			 
		  Statement stmt = null;
		   try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		      c.setAutoCommit(false);
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "select CommitID from AppCommits where AppID="+ AppID+" and CommitID  like 'v"+ prevCommitNumber+"_%'");
		 
		      while ( rs.next() ) {
		    	  prevCommitID=rs.getString("CommitID");
		    	  break;
		      }
		      rs.close();
		      stmt.close();
		      c.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      
		   }
		  
		  return prevCommitID;
		}
		
		
		public AppCommit getCommitInfo(int AppID,String CommitID){
			AppCommit appCommit = new AppCommit();
		  Statement stmt = null;
		   try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		      c.setAutoCommit(false);
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "select * from AppCommits where AppID="+ AppID+" and CommitID  ='"+ CommitID+"'");
		 
		      while ( rs.next() ) {
		    	   appCommit.CommitID=rs.getString("CommitID");
		    	   appCommit.AppID=rs.getInt("AppID");
		    	   appCommit.CommitDate=rs.getTimestamp("CommitDate");
		    	   appCommit.DeveloperName=rs.getString("DeveloperName");
		    	   break;
		      }
		      rs.close();
		      stmt.close();
		      c.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      
		   }
		  
		  return appCommit;
		}
		
		public int getCommitYear(int AppID,String CommitID){
			// get prevouius commit number
			int yearOfCommit=0;
		 
		  Statement stmt = null;
		   try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		      c.setAutoCommit(false);
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "select year(CommitDate) as CommitYear from AppCommits where AppID="+ AppID+" and CommitID='"+ CommitID +"'");
		 
		      while ( rs.next() ) {
		    	  yearOfCommit=rs.getInt("CommitYear");
		    	  break;
		      }
		      rs.close();
		      stmt.close();
		      c.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      
		   }
		  
		  return yearOfCommit;
		}
}
