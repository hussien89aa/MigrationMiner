package com.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.library.Docs.MethodDocs;
import com.library.Docs.ParseHTML;
import com.library.source.MigratedLibraries;
import com.project.info.Project;
import com.project.settings.DatabaseLogin;
import com.segments.build.Segment;
import com.subversions.process.Commit;

public class LibraryDocumentationDB {
 
  
	public void add(String LibraryName,String PackageName, String ClassName,MethodDocs methodDocs){
   
	      
	      try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		         c.setAutoCommit(false);
	      
	         String sql = "INSERT INTO LibraryDocumentation (LibraryName,PackageName,ClassName,MethodFullName,MethodDescription, MethodParams,MethodReturn) VALUES ( ? , ? , ? , ? , ? , ? , ? );"; 
	         
	         PreparedStatement stmt = c.prepareStatement(sql);
	        	      stmt.setString(1,LibraryName.replaceAll("\\h", " "));
	        	      stmt.setString(2,PackageName.replaceAll("\\h", " "));
	        	      stmt.setString(3,ClassName.replaceAll("\\h", " "));
	        	      stmt.setString(4,methodDocs.fullName.replaceAll("\\h", " ") );
	        	      stmt.setString(5, methodDocs.description.replaceAll("\\h", " "));
	        	      stmt.setString(6,methodDocs.inputParams.replaceAll("\\h", " "));
	        	      stmt.setString(7,methodDocs.returnParams.replaceAll("\\h", " "));
	        	      stmt.executeUpdate();
	      
	         stmt.close();
	         c.commit();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	    //  System.out.println("Records created successfully");
	   }
	
	 
	//TODO: change to get list of docs
	public ArrayList<MethodDocs> getDocs(String LibraryName  ){
		 ArrayList<MethodDocs> listOfMethodDocs= new  ArrayList<MethodDocs> ();
		 
		  Statement stmt = null;
		   try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		      c.setAutoCommit(false);
		      stmt = c.createStatement();
		      //TODO: Set only one commit for testing
		      String sql = "SELECT * from LibraryDocumentation where LibraryName like '%"+ LibraryName +"%' and MethodFullName like '%(%)%' and MethodDescription !='' ";
		      ResultSet rs = stmt.executeQuery( sql); //where ProjectsID=192"
		     //System.out.println(sql);
		      while ( rs.next() ) {
		    	 String fullName= rs.getString("MethodFullName").replaceAll("\\h", " ");// incase decode space with 32 ASCII code and 160 change then to 32 only
		    	  fullName = fullName.replaceAll("[?(]+", "("); // clean data
		              listOfMethodDocs.add(new MethodDocs(
		            		  fullName ,
		               		  rs.getString("MethodDescription").replaceAll("\\h", " ") , 
		               		  rs.getString("MethodParams").replaceAll("\\h", " ")  ,
		               		  rs.getString("MethodReturn").replaceAll("\\h", " ") ,
		               		  rs.getString("ClassName").replaceAll("\\h", " ")  ,
		               		  rs.getString("PackageName").replaceAll("\\h", " ") 
		               		  ));
		      }
		      rs.close();
		      stmt.close();
		      c.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      
		   }
		  
		   return listOfMethodDocs;

		}
	 public static void main(String[] args) {
		 ArrayList<MethodDocs> listOfMethodDocs= new LibraryDocumentationDB().getDocs("junit");
		for (MethodDocs methodDocs : listOfMethodDocs) {
			methodDocs.printWithClass();
		}
 	}
	
}
