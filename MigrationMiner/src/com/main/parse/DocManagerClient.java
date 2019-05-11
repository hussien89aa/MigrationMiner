package com.main.parse;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import com.database.mysql.MigrationRule;
import com.database.mysql.MigrationRuleDB;
import com.database.mysql.ProjectLibrariesDB;
import com.library.Docs.ParseHTML;
import com.library.source.DownloadLibrary;
import com.project.info.Project;
import com.segments.build.TerminalCommand;

/*
 * This Class collect Library docs from Maven repository then it convert it to relation database and 
 * save it at 'LibraryDocumentation' Table
 */
public class DocManagerClient {
	public static String folderOfDocs= "/Docs";
	public static String pathDocs=Paths.get(".").toAbsolutePath().normalize().toString() + folderOfDocs;

	//Created needed folder
	TerminalCommand terminalCommand= new TerminalCommand();
	public DocManagerClient() {
		 terminalCommand.createFolder("Docs");
		// TODO Auto-generated constructor stub
	}
	DownloadLibrary downloadLibrary = new DownloadLibrary(folderOfDocs);
	ParseHTML  parseHTML  = new ParseHTML();
	public static void main(String[] args) {
		new DocManagerClient().run();
	}
	void run(){

	   
	 
		
		LinkedList<MigrationRule> migrationRules= new MigrationRuleDB().getMigrationRulesWithoutVersion(1);
		ProjectLibrariesDB projectLibrariesDB = new ProjectLibrariesDB();
		DocManagerClient docManagerClient = new DocManagerClient();
		ParseHTML parseHTML = new ParseHTML();
		
		System.out.println("From Library\t To library\n************************************");
		for (MigrationRule migrationRule : migrationRules) {
			// if(!migrationRule.FromLibrary.contains("testng") && !migrationRule.ToLibrary.contains("testng") ){
			//	 continue;
			// }
			System.out.println(migrationRule.FromLibrary+ "\t<==>\t"+migrationRule.ToLibrary);
	
			
			//Download and save 'FromLibrary' docuemantion in DB
			ArrayList<String> listOfProjectLibraries= new ArrayList<>();
			listOfProjectLibraries.addAll( new ProjectLibrariesDB().getMigrationProjectLibraries( 
					(migrationRule.FromLibrary.equals("json")|| migrationRule.FromLibrary.equals("slf4j")  ?migrationRule.FromLibrary +":":migrationRule.FromLibrary) )) ; 
			for (String libraryInfo : listOfProjectLibraries) {
               docManagerClient.collectDocs(   migrationRule.FromLibrary,   libraryInfo);   
		    }
			
			//Download and save 'FromLibrary' docuemantion in DB
			listOfProjectLibraries.clear();
			listOfProjectLibraries.addAll( projectLibrariesDB.getMigrationProjectLibraries( 
					(migrationRule.ToLibrary.equals("json") || migrationRule.ToLibrary.equals("slf4j")  ?migrationRule.ToLibrary +":":migrationRule.ToLibrary))) ;		 
			for (String libraryInfo : listOfProjectLibraries) {
			  docManagerClient.collectDocs(   migrationRule.ToLibrary,   libraryInfo);
		    }
			//System.out.flush();
			
		}
		
		 
	 
		
	}
	
	void collectDocs(String libraryName, String libraryInfo){
	         System.out.println("Collect Docs for : "+ libraryInfo);
        	 downloadLibrary.download(libraryInfo,true); 
    	     start(  libraryName,  libraryInfo,  1);
             start( libraryName,  libraryInfo,  2); 
         
	}
	
	void start( String libraryName, String LibraryInfo, int methodType){
        
		String[] LibraryInfos =LibraryInfo.split(":");
		if(LibraryInfos.length<3){ 
			System.err.println(" Error in library name ("+ LibraryInfo+")");		
			return;
	    }
		//String DgroupId=LibraryInfos[0];
		String DartifactId=LibraryInfos[1];
		String Dversion=LibraryInfos[2];
		String libraryFileName= DartifactId +"-"+ Dversion +"-javadoc.jar";

		String jarDocFolder= libraryFileName.replace("-javadoc.jar", "Docs");
		String jarDocNameZip= libraryFileName +".zip";
		try{
			System.out.println("==> Start generate HTML from docs ");
		    String cmdStr="cd " + pathDocs + " && mkdir "+ jarDocFolder + 
		    		" && cp " + libraryFileName +" " + jarDocFolder +"/"+jarDocNameZip  +
		    		" && cd "+ jarDocFolder + 
			  " && tar -xvf " + jarDocNameZip +" && rm -rf "+ jarDocNameZip;
		    System.out.println(cmdStr);
			Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
			p.waitFor();
			System.out.println("<== Complete Generate");
			
			//Collect library documenation
			parseHTML.start(pathDocs +"/"+jarDocFolder,libraryName,methodType);
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	//Download all Docs for all JavaDoc in folder
	void downloadByDIR(){
		File folder = new File(pathDocs );
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  String fileName = listOfFiles[i].getName();
		      System.out.println("Process Jar Docs==> " + fileName.substring(0,fileName.length()-4));
		      System.out.println("Parse using method 1");
		      start(  fileName,  fileName.substring(0,fileName.length()-4),  1);
		      System.out.println("Parse using method 2");
		      start(  fileName,  fileName.substring(0,fileName.length()-4),  2);
		   }
	    }
	}

}
