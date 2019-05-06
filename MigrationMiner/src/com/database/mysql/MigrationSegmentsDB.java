package com.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.library.source.MigratedLibraries;
 
import com.project.settings.DatabaseLogin;
import com.segments.build.Segment;

/*
 * CREATE TABLE  MigrationSegments  (
		 MigrationRuleID INTEGER,
	  AppID 	INTEGER,
	 CommitID 	varchar(200),
	 FromCode	TEXT,
	 ToCode 	TEXT
	
);
 */
public class MigrationSegmentsDB {
	
public static void main(String[] args){
	 HashMap<String, Integer> map = new  HashMap<String, Integer>();
	 
		LinkedList<MigrationRule> migrationRules= new MigrationRuleDB().getMigrationRulesWithoutVersion(0);
for (MigrationRule migrationRule : migrationRules) {
		 System.out.println(migrationRule.ID+":"+migrationRule.FromLibrary+ "\t<==>\t"+migrationRule.ToLibrary);
	 
	 ArrayList<Segment> segmentList= new MigrationSegmentsDB().getSegmentsObj(migrationRule.ID);
	 System.out.println("#Segments: "+ segmentList.size());
 for (Segment segment : segmentList) {
 
	//segment.print();
	int size= Math.round(segment.getTotalLinesNumbers()*1.0f/2.0f);
	String key= migrationRule.FromLibrary +"|"+ migrationRule.ToLibrary +":"+ size;
	if(map.get(key)==null){
		map.put(key, 1);
	}else{
		map.put(key, map.get(key)+1);
	}
	//System.out.println(key+"-->"+map.get(key));
}
 
		}
 
	for (MigrationRule migrationRule : migrationRules) {
		String info= migrationRule.FromLibrary +"|"+ migrationRule.ToLibrary ;

		 int zeroto2=0;
		 int three=0;
		 int foureto6=0;
		 int sevento10=0;
		 int big=0;
for (String key : map.keySet()) {

	if(key.contains(info)){
		String[] sp= key.split(":");
		int sSize= Integer.valueOf(sp[1]);
		if(sSize>=0 && sSize<=2)
			zeroto2=zeroto2+map.get(key);
		else if(sSize==3)
			three=three+map.get(key);
		else if(sSize>=4 && sSize<=6)
			foureto6=foureto6+map.get(key);
		else if(sSize>=7 && sSize<=10)
			sevento10=sevento10+map.get(key);
		else if(sSize>10)
			big=big+map.get(key);

	}
}
System.out.println(migrationRule.FromLibrary +"-\\textgreater "+ migrationRule.ToLibrary +" & "+
		   + zeroto2  
		 +" & "+ three
+" & "+ foureto6
+" & "+ sevento10
+" & "+ big +"\\\\");
 System.out.println("\\hline");
	}
 		//MigratedLibraries.ID=7; // This shared with safe method
	   //TestClient testClient= new TestClient();
	   //testClient.runAlgorithm(  segmentList);
	  
	   //SubstitutionAlgorithm substitutionAlgorithm = new SubstitutionAlgorithm(segmentList,10);
	   // substitutionAlgorithm.start();
	   System.out.println("done");
}
// get segments as obj from database
public ArrayList<Segment> getSegmentsObj(int migrationRuleID){
	 ArrayList<Segment> segmentList =  new ArrayList<Segment>();
	 
	 // read from database
	 int blockID=1;
		LinkedList<MigrationSegments> listOfSegmnets= new MigrationSegmentsDB().getMigrationSegments(migrationRuleID);
		for (MigrationSegments migrationSegments : listOfSegmnets) {
			Segment segment = new Segment();
			segment.AppID = migrationSegments.AppID;
			segment.CommitID = migrationSegments.CommitID;
			
			String[] linesRemoved = migrationSegments.FromCode.split(System.getProperty("line.separator"));
			segment.blockCode.add(String.valueOf(blockID++));
			for (String line : linesRemoved) {
				segment.removedCode.add(line);
				segment.blockCode.add("<  "+ line);
			}
		
			segment.blockCode.add("----------");
			String[] linesAdded = migrationSegments.ToCode.split(System.getProperty("line.separator"));
			for (String line : linesAdded) {
				segment.addedCode.add(line);
				segment.blockCode.add(">  "+ line);
			}
			segmentList.add(segment);
		}
	 
	 return segmentList;
}
	public void add( int MigrationRuleID,int AppID, String CommitID,Segment segment ){
 
	      //Statement stmt = null;
	      try {
			   Connection c = null;
			   Class.forName("com.mysql.jdbc.Driver");  
			   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
		        
		         c.setAutoCommit(false);
	        // stmt = c.createStatement();
	         String sql = "INSERT INTO MigrationSegments (MigrationRuleID,AppID,CommitID,FromCode,ToCode,fileName) VALUES (?,?,?,?,?,?);"; 
	         PreparedStatement stmt = c.prepareStatement(sql);
	        	      stmt.setInt(1,MigrationRuleID);
	        	      stmt.setInt(2,AppID);
	        	      stmt.setString(3,CommitID);
	        	      stmt.setString(4, arrayListToString(segment.removedCode));
	        	      stmt.setString(5, arrayListToString(segment.addedCode) );
	        	      stmt.setString(6,segment.fileName);
	        	      stmt.executeUpdate();
	         
	         //stmt.executeUpdate(sql);
	         stmt.close();
	         c.commit();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	      System.out.println("Records created successfully");
	   }
	
	public static String arrayListToString(ArrayList<String> listOfCode){
		StringBuilder textCode = new StringBuilder();
	    for (String lineCode : listOfCode)
	    	textCode.append(lineCode).append('\n');
	    return textCode.toString();
	}
	
	
	//This function return list of migration rules for database
	public LinkedList<MigrationSegments> getMigrationSegments(int migrationRuleID){
		LinkedList<MigrationSegments> listOfSegmnets= new LinkedList<MigrationSegments>();
	  Statement stmt = null;
	   try {
		   Connection c = null;
		   Class.forName("com.mysql.jdbc.Driver");  
		   c=DriverManager.getConnection( DatabaseLogin.url,DatabaseLogin.username,DatabaseLogin.password); 
	        
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "select * from MigrationSegments WHERE  MigrationRuleID="+ migrationRuleID );
	      
	      while ( rs.next() ) {
	    	  listOfSegmnets.add(new MigrationSegments(rs.getInt("AppID"),rs.getString("CommitID"),rs.getString("FromCode"),
	     			   rs.getString("ToCode") ));
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	   } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      
	   }
	  
	  return listOfSegmnets;
	}
}
