package com.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.project.info.Project;
import com.project.settings.DatabaseLogin;
import com.subversions.process.Commit;

public class RepositoriesDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new RepositoriesDB().addRepository("httm.googles.com", "java");
		System.out.println("ID:"+ new RepositoriesDB().getRepositoryID("httm.google.com"));
	}
	
	public void addRepository(String AppLink, String AppType ){
	  	       int id=getRepositoryID( AppLink);
		  	   if(id!=0){
		  		   return;
		  	   }
		  	   
		      Statement stmt = null;
		      try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			         c.setAutoCommit(false);
		         stmt = c.createStatement();
		         String sql = "INSERT INTO Repositories (AppLink,AppType) " +
		                        "VALUES ('"+AppLink+"', '" + AppType+ "');"; 
		         stmt.executeUpdate(sql);
		         stmt.close();
		         c.commit();
		         c.close();
		      } catch ( Exception e ) {
		         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      }
		    //  System.out.println("Records created successfully");
		   }
		
		public int getRepositoryID(String AppLink){
			 int AppID=0;
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "SELECT * from Repositories where AppLink='"+ AppLink +"'");
			     
			      while ( rs.next() ) {
			    	  AppID=  rs.getInt("AppID");
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return AppID;

			}
		
		public int getRepositoryIDDemo(String AppLink){
			 int AppID=0;
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "SELECT * from Repositories where AppType='"+ AppLink +"'");
			     
			      while ( rs.next() ) {
			    	  AppID=  rs.getInt("AppID");
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return AppID;

			}
		
		public String getRepositoriesLink(int AppID){
			 String appLink="";
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      ResultSet rs = stmt.executeQuery( "Select AppLink from Repositories where AppID="+ AppID ); //TODO  order by ProjectsID, commitID"
			     
			      while ( rs.next() ) {
			    	  appLink= rs.getString("AppLink");
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return appLink;

			}
		public	void addNewProject(String appLink,String projectType,ArrayList<Commit> commitList){
			 addRepository(appLink, projectType);
			int appID=getRepositoryID(appLink);
			AppCommitsDB appCommitsDB= new AppCommitsDB();
			for (Commit commit : commitList) {
				 appCommitsDB.addAppCommit(appID,  commit );
				 //TODO: save files changed
				 //for (String path : commit.filePath) {
					// new CommitFileChanges().addCommitFile(commit.commitID, path);
				//}
				 
			}
		}
		
		public ArrayList<Repository> getRepositories(){
			 ArrayList<Repository> listOfRepositories= new  ArrayList<Repository> ();
			 
			  Statement stmt = null;
			   try {
				   Connection c = null;
				   Class.forName("com.mysql.jdbc.Driver");  
				   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
			        
			      c.setAutoCommit(false);
			      stmt = c.createStatement();
			      //TODO: Set only one commit for testing
		    	  // avoid wrong data like ${project.groupId}:owlapi:xxx  or org.json:gson:null
			      ResultSet rs = stmt.executeQuery( "SELECT * from Repositories  where AppID in(SELECT AppID FROM MigrationSegments GROUP BY AppID)" ); //where ProjectsID=192"
			     
			      while ( rs.next() ) {
	  
			    	  listOfRepositories.add(new Repository(
	                		  rs.getInt("AppID"),
	                		  rs.getString("AppLink")  
	                		  ));
			      }
			      rs.close();
			      stmt.close();
			      c.close();
			   } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      
			   }
			  
			   return listOfRepositories;

			}

}
