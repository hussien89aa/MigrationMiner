package com.main.parse;
 
import java.util.ArrayList;
import java.util.LinkedList;
import com.database.mysql.LibraryDocumentationDB;
import com.database.mysql.MigrationRule;
import com.database.mysql.MigrationRuleDB;
import com.database.mysql.MigrationSegmentsDB;
import com.library.Docs.MethodDocs;
import com.library.source.MethodObj;
import com.segments.build.Segment;

/*
 * This client shows example of how to access to  fragments and Docs for every methods as objects
 * Note: You should run this client after running  Main.java
 */
public class TestClient {

	
	public static void main(String[] args) {
		 
		//Return list of migrations between two pairs of libraries( added/removed)
		LinkedList<MigrationRule> migrationRules= new MigrationRuleDB().getMigrationRulesWithoutVersion(1);
		 
		for (MigrationRule migrationRule : migrationRules) {
			System.out.println("== Migration Rule "+ migrationRule.FromLibrary +	" <==> "+  migrationRule.ToLibrary +"==");
		 
			/*
			 *  For every migrations, retrieve list of collected of fragments for migration at method level.
			 *  every fragment has N added methods M removed methods
			 */
            ArrayList<Segment> segmentList = new MigrationSegmentsDB().getSegmentsObj(migrationRule.ID);
		  
			for (Segment segment : segmentList) {
			       
				  segment.print();
		
				  // Print all removed method signatures With Docs
				  printMethodWithDocs( migrationRule.FromLibrary,segment.removedCode);  
				 
				  // Print all added method signatures With Docs
				  printMethodWithDocs( migrationRule.ToLibrary,segment.addedCode);

		     } // End fragment for every migration
	     
		}  // End library migration
		  
		 
		
	
	}
	
	
	/* 
	 * This method takes list of methods signatures with library that methods belong to.
	 * It will print the signatures and Docs for every method
	 */
	static void printMethodWithDocs(String libraryName,ArrayList<String> listOfMethods ) {
		
		  // For every add method print the Docs
		  for(String methodSignature: listOfMethods){
			   
			      // Convert  method signatures as String to Object
			      MethodObj methodFormObj= MethodObj.GenerateSignature(methodSignature);
			      
			      //retrieve Docs from the library for method has that name
			  	  ArrayList<MethodDocs>  toLibrary = new LibraryDocumentationDB().getDocs( libraryName,methodFormObj.methodName);
			  	 
			  	  //Map method signatures to docs
			      MethodDocs methodFromDocs = MethodDocs.GetObjDocs(toLibrary, methodFormObj);
		         
			      if(methodFromDocs.methodObj== null) {
		        	 System.err.println("Cannot find Docs for: "+ methodSignature);
		        	 continue;
		          }
			      methodFromDocs.print();	     
		 }
	}
	
	
	
}
