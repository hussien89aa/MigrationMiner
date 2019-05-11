/*
 * This file responsbile for work with github for get projects, logs
 */
package com.subversions.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
 
import com.database.mysql.RepositoriesDB;
import com.main.parse.CollectorClient;
import com.project.info.*;
import com.project.settings.GithubLogin;
public class GitHubOP {

	int commitNumber;
	public static String gitUserInfo=GithubLogin.userName+ ":"+ GithubLogin.Password;
	public static String pathCloneTest=Paths.get(".").toAbsolutePath().normalize().toString() +"/Clone/";
	Operations operations= new Operations();
	CollectorClient projectLibrary= new CollectorClient();
	public String clonePath;
	public String appFolder;
	public String appURL;
	public GitHubOP(String appURL,String clonePath){
		this.appURL=appURL;
		this.clonePath=clonePath;
		this.appFolder=getAppName(appURL);
	}
	public static void main(String[] args) {
		String LOG_FILE_NAME="app_commits.txt";
		// TODO Auto-generated method stub
		//String appURL="https://github.com/opentripplanner/OpenTripPlanner.git";
		//GitHubOP gitHubOP=new GitHubOP(appURL, pathCloneTest);
		//gitHubOP.cloneApp( );
		//gitHubOP.generateLogs(LOG_FILE_NAME);
		//ArrayList<Commit> commitList= gitHubOP.readLogFile(pathCloneTest +LOG_FILE_NAME);
		//RepositoriesDB repositoriesDB= new RepositoriesDB();
	    //repositoriesDB.addNewProject(appURL,"Java", commitList);
	    
       //  for (Commit commit : commitList) {
       // 	 gitHubOP.printCommitInfo(  commit);
		// }
      
         
        // new GitHubOP("HH", pathClone,"ActionBarSherlock");
        // new ProjectLibrary().listOfJavaProjectLibrary(pathCloneTest+"ActionBarSherlock");
  
	}

	public	String addGitUserLogin(String appURL){
		 // Print the content on the console
		String[] split=appURL.split("//");
	     if (split.length<2) {
	    	 System.err.println("Git url isnot correct: "+appURL);	 
			return "";
		}
	  //System.out.println ();
	  return(split[0]+ "//"+ gitUserInfo+"@"+ split[1]);
	}
	// clone the link in machine
	public	void cloneApp( ){
			try{
				String appLinkLogin=addGitUserLogin(appURL);
				System.out.println("==> Start cloning: "+ appLinkLogin);
			    String cmdStr="cd " + clonePath + " && git clone " + appLinkLogin;
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== Complete clone");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
	public	void deleteFolder(String folderPath){
			try{
				System.out.println("==> Start deleting ...");
			    String cmdStr=" rm -rf "+ folderPath +"";
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== Complete delete");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	 // Clear clone folder from other apps data
	public	void clearCloneFolder(){
		try{
			System.out.println("==> Start clear clone folder ...");
			String cmdStr="cd " + clonePath + " && rm -rf *";
			Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
			p.waitFor();
			System.out.println("<== Complete delete");
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public boolean isPageExisit(String pageLink){
	
		boolean isExisit=false;
		try{
			URL u = new URL (pageLink);
			HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection (); 
			huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD"); 
			huc.connect () ; 
			int code = huc.getResponseCode() ;
			//System.out.println(code);
			isExisit= code==404?false:true;
		}catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("isPageExisit: "+pageLink +"? "+ (isExisit==true?"Yes exist":" file Not found"));
		return isExisit;
	}
	
	public boolean isAppExist(String appName){
		
		boolean isExist=false;
		 String AppPath = clonePath+  appName;
		 if (Files.isDirectory(Paths.get(AppPath))) {
			 isExist=true;
		 }
		return isExist;
	}
	// clone the link in machine
	public	void generateLogs(String appName ){
			try{
				System.out.println("==> Start generate logs for: "+ appName);
			    String cmdStr="cd " + clonePath+ "/"+ appFolder + " && git log --name-status  --reverse >../"+ appName ;
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== Complete generate logs");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	
	// clone the link in machine
		public	void generateVersions(String appName ){
				try{
					System.out.println("==> Start generate Versions for: "+ appName);
				    String cmdStr="cd " + clonePath+ "/"+ appFolder + " && git show-ref --tags >../"+ appName ;
					Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
					p.waitFor();
					System.out.println("<== Complete generate versions");
					
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
	 
	// checkout repo
		public	void gitCheckout(String appName,String commitID ){

				if(commitID.contains("_")){
					String[]commitIDSP = commitID.split("_");
					commitID=commitIDSP[1];
				}
				try{
					System.out.println("==> Start checkout: "+ commitID);
				    String cmdStr="cd " + clonePath+ appName + " && git checkout "+ commitID ;
					Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
					p.waitFor();
					System.out.println("<== Complete checkout");
					
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		// copy App
		public	void copyApp( String newCopyName ){
				try{
					System.out.println("==> Start copy: "+ appFolder +", to:"+ newCopyName);
				    String cmdStr="cd " + clonePath + " && cp -r "+ appFolder + " "+ newCopyName ;
					Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
					p.waitFor();
					System.out.println("<== copy done");
					
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}	
		//Collect all vaild App commits
		 //We get only commit that have changes in .java files otherwise ignored
	public ArrayList<Commit> readLogFile(String logFilePath,String searchForFiles ){
		System.out.println("We collect only commit that have changes in .java files otherwise ignored");
		ArrayList<Commit> commitList= new ArrayList<Commit>();
		commitNumber=1;
		try {
			FileInputStream fstream;

				fstream = new FileInputStream(logFilePath);
		
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;
            String commitID="";
            String developerName="";
            String commitDate="";
            //String filePath="";
            //boolean iscommit==""
            ArrayList<CommitFiles> filePath= new ArrayList<CommitFiles>();
            int countOfSpaces=0;
            String commitText="";
			//Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null)   {
				// System.out.println(strLine);
				 //get commit ID
				 if(strLine.startsWith("commit ")){
			 
					    
					   // if(filePath.size()>=0)
					    if(commitID!="")
					   {
					    	//printCommitInfo( commitID, developerName, commitDate,commitText,filePath);
					    	commitList.add(new Commit(commitID, developerName, commitDate, commitText, filePath));
					    }
					     
						 commitID=strLine.substring("commit ".length()).trim();
						 
						 commitID= "v"+commitNumber + "_"+ commitID;
					     commitNumber++;
						 //init
						 countOfSpaces=0;
						 commitText="";
						 filePath.clear();
				  }
				 //get developer name
				 if(strLine.startsWith("Author:")){
						 developerName=strLine.substring("Author:".length()).trim();		 
				  }
				 
			 
				 //get commit date
				 if(strLine.startsWith("Date:")){ 
					    commitDate=strLine.substring("Date:".length()).trim();
					    commitDate= operations.correctGitDate(commitDate);
					    
				  }
				 if(countOfSpaces==1){
					 commitText=strLine.trim();
					 countOfSpaces++;
					 
				  }
				 if(strLine.trim().length()==0){
					 // System.out.println("****");		
					 countOfSpaces++;
				  }
				 
				 //We get only commit that have changes in .java files otherwise ignored
				 //TODO: if you want to support other languages you need to add extension here
				 if(strLine.endsWith(searchForFiles) ){
					    String[] spPath = strLine.split("\\s+");
					    String fileOperation= null;
					    String firstFile= null;
					    String secodFile= null;
					    
					    //Not alwasy this one is file
					    if(spPath.length>=2){
					    	fileOperation=spPath[0].trim();
					    	firstFile=spPath[1].trim();
					    }
					    
					    //File rename new name
					    //ex: R072	resthub-core/src/main/java/org/resthub/core/domain/model/identity/Group.java	resthub-identity/src/main/java/org/resthub/identity/domain/model/Group.java
					    if(spPath.length>=3){
					    	secodFile=spPath[2].trim();
						 }
					    
					    if(spPath.length>=2){
						     filePath.add(new CommitFiles(fileOperation, firstFile, secodFile));
					     }
					   // System.out.println("file: "+strLine);
					
				  }
			
				 
				  }
			    //if(filePath.size()>=0)
			    if(commitID!="")
			    { 
			    	//printCommitInfo( commitID, developerName, commitDate,commitText,filePath);
			    	commitList.add(new Commit(commitID, developerName, commitDate, commitText, filePath));
					   
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Close the input stream
			br.close();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return commitList;
		 
	}

	//This method find commit id from String
 public static String getCommitID(String commitID){
	 String[] spCommit=commitID.split("_");
	 if(spCommit.length!=2){
		 System.err.println("The commit ("+ spCommit +") isnot correct need to have _ between");
		 System.exit(0);
	 }
	 return spCommit[1];
 }
 
	//This method find commit id from String
	public static String getAppName(String appLink){
		String[] sp = appLink.split("/");
		if(sp.length<5){	
			 System.err.println("Cannot get App name from  ("+ appLink +") ");
			 return "";
			}
		String appName=sp[sp.length-1];
		if(appName.endsWith(".git")){
			appName=appName.substring(0, appName.length()-4);
		}
		 return appName;
	}
	//This method find commit id from String
	public static String getAppNameAndUserName(String appLink){
		String[] sp = appLink.split("/");
		if(sp.length<5){	
			 System.err.println("Cannot get App name from  ("+ appLink +") ");
			 return "";
			}
		String appName=sp[sp.length-2] +"__"+sp[sp.length-1];
		if(appName.endsWith(".git")){
			appName=appName.substring(0, appName.length()-4);
		}
		 return appName;
	}
	void printCommitInfo( Commit commit){
	     if(commit.filePath.size()==0){
	    	 return;
	     }
	
	     System.out.println("-------------------");
	     System.out.println("commit Number: "+commitNumber);
		 System.out.println("commit ID: "+commit.commitID);
	     System.out.println("Author: "+commit.developerName);
	     System.out.println("Correct Date: "+commit.commitDate);
	     System.out.println("commitText: "+commit.commitText);
	     for (CommitFiles path : commit.filePath) {
		     System.out.println("file: "+path.firstFile);
		}
	    projectLibrary.listOfJavaProjectLibrary(clonePath+"/"+ appFolder);
	     
	}
	
	
	// get list of changed files at specific commit
	public ArrayList<String> getlistOfChangedFiles(String commitID){
		commitID =getCommitID( commitID);
		ArrayList<String> listOfChangedFiles=new  ArrayList<String>();
		 String commintsPath=clonePath+ "app_commits.txt";
		 try{
		 
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commintsPath) ));         

		    String line;
		    boolean commitInfo=false;
		    while ((line = br.readLine()) != null) {
		    	
		    	if(commitInfo==true){
		    		if(line.startsWith("M") && line.endsWith(".java")){
		    			listOfChangedFiles.add(line.substring(2).trim());
 
		    			
		    		}
		    	}
		    	

		    	if(line.contains(commitID)){
		    		commitInfo=true;
				}else if(line.startsWith("commit")){
		    		commitInfo=false;
				}
		    	

		    }
	
		    } catch (IOException e) {
		       // do something
		    }
		 
		 return listOfChangedFiles;
	 }
}
