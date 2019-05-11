package com.main.parse;

 
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;

import com.database.mysql.AppCommit;
import com.database.mysql.AppCommitsDB;
 
import com.database.mysql.MigrationRule;
import com.database.mysql.MigrationRuleDB;
import com.database.mysql.MigrationSegmentsDB;
import com.database.mysql.ProjectLibrariesDB;
import com.database.mysql.RepositoriesDB;
import com.library.source.DownloadLibrary;
import com.library.source.MigratedLibraries;
 
import com.project.info.Operation;
import com.project.info.Project;
import com.segments.build.CleanJavaCode;
import com.segments.build.Segment;
import com.segments.build.TerminalCommand;
import com.subversions.process.GitHubOP;

/**
 * This Client responsible for detect all fragments by cloning the apps from GitHub
 */
public class DetectorClient {
	String LOG_FILE_NAME="app_commits.txt";
	static String pathClone=Paths.get(".").toAbsolutePath().normalize().toString() +"/Clone/Process/";
	String pathToSaveJAVALibrary= "/librariesClasses/jar";
	TerminalCommand terminalCommand= new TerminalCommand();
	CleanJavaCode cleanJavaCode= new CleanJavaCode();
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		new DetectorClient().start();
	    System.out.println("process done");
	}
	
	void start(){
		//Created needed folder
		terminalCommand.createFolder("librariesClasses/jar/tfs");
		
		 System.out.println("*****Loading all projects libraries (will take some time) *****");
		 AppCommitsDB appCommitsDB= new AppCommitsDB();
		// ArrayList<Repository> listOfRepositories= new RepositoriesDB().getRepositories();
		// HashMap<Integer, LinkedList<AppCommit>> appsCommitsList= appCommitsDB.getAppCommits();
		LinkedList<MigrationRule> migrationRules= new MigrationRuleDB().getMigrationRulesWithoutVersion(0);
		MigrationSegmentsDB migratedCommitDB = new MigrationSegmentsDB();
		ArrayList<Project> listOfProjectLibraries= new ProjectLibrariesDB().getProjectLibraries();
			
		ArrayList<Project> listOfAddedLibraries= new ArrayList<Project>();
		ArrayList<Project> listOfRemovedLibraries= new ArrayList<Project>();
		 
		// Hold list of project libraries at current commit
		ArrayList<Project> listOfCurrentProjectLibraries= new ArrayList<Project>();
 
		
		 String oldcommitID="";
		 String oldPomPath="";
         String newcommitID="";
         int currentProjectsID=0;
         MigrationRulesClient migrationRulesClient= new MigrationRulesClient();
			// search in all commits for rule
         	String appLink="";
 		

            //*********************************************************************
           //1- Search for migration  using library changes in pom file
          //*********************************************************************
           
		 for (MigrationRule migrationRule : migrationRules) {	
			 System.out.println("==> Start search for migration rule "+
					 migrationRule.FromLibrary +
					 	"<==> "+  migrationRule.ToLibrary);
			 MigratedLibraries.ID=migrationRule.ID;
			 
				
			 // get one vaild library signature to use it in self-admitted
				String fromLibraryVaild=null;
				String toLibraryVaild=null;;
			  //currentProjectSearch=0;
			// boolean exitSearch=false;
			 // collect all the segments that found in all the projects for specific rule
			 ArrayList<Segment> segmentList =  new ArrayList<Segment>();
			for (Project project : listOfProjectLibraries) {
			
				//if(currentProjectSearch>15){
				//	break; //Go check next rule
				//}
				newcommitID=project.CommitID;
  
				// move to next project to search in
				//TODO: This code cannot find migration in last commit in every project
				if (project.ProjectID!=currentProjectsID){
					   currentProjectsID=project.ProjectID;
					   listOfAddedLibraries.clear();
			    	   listOfRemovedLibraries.clear();
			    	   listOfCurrentProjectLibraries.clear();
			    	   oldcommitID="";
			    	   oldPomPath="";
			    	   appLink=new RepositoriesDB().getRepositoriesLink(project.ProjectID);
				}
				// Move  to next project in one repository
				if(oldPomPath.equals(project.PomPath)==false){
					   listOfAddedLibraries.clear();
			    	   listOfRemovedLibraries.clear();
			    	   listOfCurrentProjectLibraries.clear();
			    	   oldcommitID="";
			    	   oldPomPath=project.PomPath;
				}
				
				//*********************************************************************
				// if new commit there we need  to generate the CP for libraries changed
	           if ( oldcommitID.equals(newcommitID)==false ){
		          
	        	   // he may be only added new library but didnot remove old library
	        	   if(listOfAddedLibraries.size()>0 || listOfRemovedLibraries.size()>0){
		   
		        	  if(migrationRulesClient.isCommitsSequence(oldcommitID, newcommitID)==false){
		        		   System.err.println("==>This process ingored because uncorrect order commits in between " + oldcommitID+"==> "+ newcommitID);
		        		   return;
		        	   }else{
		        		   
		        			
	        				// make sure is not migrate process
	        				//TODO: make sure is not library separate process 
	        				//it happened when one library divided into multi-libraries
		        		   // we use ":" to make sure we search for artificate id
	        				String isFoundInPrevious=Project.isFound(listOfRemovedLibraries,":"+ migrationRule.ToLibrary);
	        				if(isFoundInPrevious.length()>0 ){continue;}
	        				
	        				String toLibraryFind =Project.isFound(listOfAddedLibraries, ":"+ migrationRule.ToLibrary);
	        				String fromLibraryFind=Project.isFound(listOfRemovedLibraries, ":"+ migrationRule.FromLibrary);
	        				
	        				//Case: for library that migrate but we didnot remove from the pom.xml
	        				String isStillFound=Project.isFound(listOfCurrentProjectLibraries, ":"+ migrationRule.FromLibrary);
	        				if( toLibraryFind.length()>0 && fromLibraryFind.length()==0){
	        					fromLibraryFind=isStillFound;
	        				}
	        				//Case: use removed old library and he have new library already added in prevous commits
	        				if( toLibraryFind.length()==0 && fromLibraryFind.length()>0 ){
	        					toLibraryFind=Project.isFound(listOfCurrentProjectLibraries, ":"+migrationRule.ToLibrary);
	        				}
	        				if( toLibraryFind.length()>0 && fromLibraryFind.length()>0 ){
	        					MigratedLibraries.toLibrary =toLibraryFind;
		        				MigratedLibraries.fromLibrary=fromLibraryFind;
		        				
	        				   String previousCommitID=appCommitsDB.previousCommitID(project.ProjectID, oldcommitID);
	        				   if(previousCommitID.length()>0){
	        					System.out.println("-----------------\n"+
	        							currentProjectsID+"- Fing migration\nCommit from :"+ previousCommitID +"==> "+ 
	        				       oldcommitID+ "\nLibrary from: "+MigratedLibraries.fromLibrary
	        							+ "==> "+  MigratedLibraries.toLibrary + "\nAppLink: "+ appLink);
	        			    
    						     ArrayList<Segment> listOfblocks = startCloning(appLink,previousCommitID,oldcommitID);
    				    	     // if we find fragments we add them to fragment list
    						     if(listOfblocks.size()>0){
    				    	    	 segmentList.addAll( listOfblocks );
    				    	    	 
    				    	    	 // get active library signature to use it in self admitted
    				    	    		fromLibraryVaild=fromLibraryFind;
    				    		    toLibraryVaild=toLibraryFind;
    				    				
    				    	    	   // Save all segments in database
    				 				System.out.println("==> Start saving all founded segmenst in database");
    				 				 for(Segment segment:listOfblocks){
    				 					 // save the commit and project that has the migration
    				 					 migratedCommitDB.add(migrationRule.ID,currentProjectsID, oldcommitID,segment);
        				    	    	 
    				 				 }
    				 				 System.out.println("<== complete saving all founded segmenst in database");
    				 			 
    				    	     }
    						    // currentProjectSearch++;
	        				   }else{
	        					   System.err.println("Cannot find prevous commit before :"+ oldcommitID);
	        				   }
	        				}
		        	   
		        	
		        			
		        	   }
		        
		             }// if there is migration

	        	   //clear and update current project libraries
	        	   listOfCurrentProjectLibraries.addAll(listOfAddedLibraries);
	        	   listOfAddedLibraries.clear();
	        	   // remove object from list of current  project library
	        	   for (Project projectRemoved : listOfRemovedLibraries) {
		    		   for(int i=0;i<listOfCurrentProjectLibraries.size();i++){
		    			   if(listOfCurrentProjectLibraries.get(i).LibraryName.equals(projectRemoved.LibraryName)){
		    				   listOfCurrentProjectLibraries.remove(i);
		    				   break;
		    			   }
		    		   }
	    			
					}
		    	   listOfRemovedLibraries.clear();
		           oldcommitID=project.CommitID;
		           //oldPomPath  = project.PomPath;
	           }
	         //*********************************************************************
	           
	           // added library in list and removed library in list
	           if(project.isAdded==Operation.added){
	    		   listOfAddedLibraries.add(project);
	    	   }else{
	    		   listOfRemovedLibraries.add(project); 		   
	    	   }
	           
	          
	           
			} // end of project search
			
	 
 
           
             // call self admitted search 
	         ArrayList<Segment> listOfblocks =findSelfAdmittedMigration(migrationRule.ID, fromLibraryVaild,  toLibraryVaild,migrationRule.FromLibrary,migrationRule.ToLibrary);
		     if(listOfblocks.size()>0){
		    	 segmentList.addAll( listOfblocks );
		     }
		     
			
			/*After find all migration segments start apply Algorithm
		      Run CP and Substitution Algorithm on cleaned files
			 */
			int isVaildMigration=0;
			if(segmentList.size()>0){
				isVaildMigration=1;
		 
				//Run  function mapping mapping
			   //SyntheticTestClient testClient= new SyntheticTestClient();
			   //testClient.runAlgorithm(  segmentList);
			}else{
				isVaildMigration=2;
				System.out.println("Didnot find any migration  segment for this rule");
			}
			
			// update the migration rule to state of valid=1 or not valid=2
            new MigrationRuleDB().updateMigrationRule(migrationRule.ID, isVaildMigration);
			
		  }// end check all migration rules

		
	}
 
	
    //*********************************************************************
   //2- Search for migration that defined using commit text
  //*********************************************************************
	
	ArrayList<Segment> findSelfAdmittedMigration(int migrationRuleID,String fromLibraryVaild, String toLibraryVaild,String fromLibraryName, String toLibraryName){
		ArrayList<Segment> segmentList= new ArrayList<Segment>();
		
		if(fromLibraryVaild==null || toLibraryVaild==null){
			System.err.println("Either fromLibrary="+fromLibraryVaild+"  or toLibrary="+ toLibraryVaild+" is not vaild");
			return segmentList;
		}
		AppCommitsDB appCommitsDB= new AppCommitsDB();
		MigrationSegmentsDB migratedCommitDB = new MigrationSegmentsDB();
        ArrayList<AppCommit> listOfAppsCommit =appCommitsDB.getAllCommitsHasMigration(fromLibraryName, toLibraryName,migrationRuleID);
       
		System.out.println("Start searching for self-admitted migration between "+ fromLibraryVaild +"==>"+ toLibraryVaild+" by the developer");
 	    MigratedLibraries.toLibrary =toLibraryVaild;
		MigratedLibraries.fromLibrary=fromLibraryVaild;
		
		 for (AppCommit appCommit : listOfAppsCommit) {		
			   String  appLink=new RepositoriesDB().getRepositoriesLink(appCommit.AppID);
			   String previousCommitID=appCommitsDB.previousCommitID(appCommit.AppID, appCommit.CommitID);
			   if(previousCommitID.length()>0){
				System.out.println("-----------------\n"+
						appCommit.AppID+"- Fing migration\nCommit from :"+ previousCommitID +"==> "+ 
						appCommit.CommitID+ "\nLibrary from: "+MigratedLibraries.fromLibrary
						+ "==> "+  MigratedLibraries.toLibrary + "\nAppLink: "+ appLink);
				ArrayList<Segment> listOfblocks = startCloning(appLink,previousCommitID,appCommit.CommitID);
	    	     // if we find fragments we add them to fragment list
			     if(listOfblocks.size()>0){
			    	 segmentList.addAll( listOfblocks );
	    	    	   // Save all segments in database
	 				System.out.println("==> Start saving all founded segmenst in database");
	 				 for(Segment segment:listOfblocks){
	 					 
	 					 // save the commit and project that has the migration
		    	    	 migratedCommitDB.add(MigratedLibraries.ID,appCommit.AppID, appCommit.CommitID,segment);
		    	    	 
	 				 }
	 				 System.out.println("<== complete saving all founded segmenst in database");
			     }
			   }
        }
        return segmentList;
	}
	

	// This function return list of cleaned segments
ArrayList<Segment> startCloning(String appURL,String previousCommitName,String migrateAtCommitName){
	
	ArrayList<Segment> segmentList =  new ArrayList<Segment>();
	// list of changed files
	ArrayList<String> listOfChangedFiles=cloneMigratedCommits( appURL, previousCommitName, migrateAtCommitName);
	if(listOfChangedFiles.size()>0){
		 String outputDiffsPath=pathClone +"../Diffs/" + MigratedLibraries.ID  +"/" + migrateAtCommitName +"/" ;
		// list of changed cleaned files
		 ArrayList<String> diffsFilePath=   generateFragments(listOfChangedFiles,previousCommitName,migrateAtCommitName,outputDiffsPath); 

		 if(diffsFilePath.size()>0){
			 //clean java code from the data
			 segmentList =cleanJavaCode.getListOfCleanedFiles(outputDiffsPath, diffsFilePath) ;	    	 
		 } 
	
	}
	  return segmentList;
}

 
	// This method responsible for clone two commits  that has migration to find file changes between them
ArrayList<String> cloneMigratedCommits(String appURL,
			 String previousCommitName,String migrateAtCommitName){
		 ArrayList<String> listOfChangedFiles=new  ArrayList<String>();
		
		 //Download The library Jar singatures
			DownloadLibrary downloadLibrary = new DownloadLibrary(pathToSaveJAVALibrary);
			
	        downloadLibrary.download(MigratedLibraries.fromLibrary,false);
	        downloadLibrary.buildTFfiles(MigratedLibraries.fromLibrary);
	        
	        downloadLibrary.download(MigratedLibraries.toLibrary,false);
	        downloadLibrary.buildTFfiles(MigratedLibraries.toLibrary);
	        
	        if(downloadLibrary.isLibraryFound(MigratedLibraries.fromLibrary)==false ||
					downloadLibrary.isLibraryFound(MigratedLibraries.toLibrary)==false	){
	        	
				System.err.println("Cannot download either "+ MigratedLibraries.fromLibrary+ " or "+ MigratedLibraries.toLibrary );
				return listOfChangedFiles;
			}
	        
	    
	        
	        
		 //Clone App if isnot cloned already
		 GitHubOP gitHubOP= new GitHubOP(appURL,pathClone);
		 if(gitHubOP.isAppExist(gitHubOP.appFolder)==false){
		 
			gitHubOP.deleteFolder(pathClone); // clear folder from prevous project process
			terminalCommand.createFolder(pathClone);
			gitHubOP.cloneApp( );
			gitHubOP.generateLogs(LOG_FILE_NAME);
		 } 
		 // get list of change files
		 listOfChangedFiles=gitHubOP.getlistOfChangedFiles(migrateAtCommitName);
		 if(listOfChangedFiles.size()==0){
			 System.out.println("\t==>Cannot find any changes in Java files in this commit");
			 return listOfChangedFiles;
		 }
		 //clone first commit
		 if(gitHubOP.isAppExist(previousCommitName)==false){
			 gitHubOP.copyApp( previousCommitName);
			 String firstCommitID=GitHubOP.getCommitID(previousCommitName);
			 gitHubOP.gitCheckout(previousCommitName,firstCommitID);
		 }
		 
		 //clone second commit
		 if(gitHubOP.isAppExist(migrateAtCommitName)==false){
			 gitHubOP.copyApp( migrateAtCommitName);
			 String secondCommitID=GitHubOP.getCommitID(migrateAtCommitName);
			 gitHubOP.gitCheckout(migrateAtCommitName,secondCommitID);
		 }

	 	 
		  return   listOfChangedFiles; 
		 
	 }
	 
	 
	 // This function will generate fragments of code changes
	 ArrayList<String> generateFragments(ArrayList<String> listOfChangedFiles,
		 String previousCommitName, String migrateAtCommitName,String outputDiffsPath){
	
	 // list of diffs files path
	 ArrayList<String> diffsFilePath = new ArrayList<String>();
	 boolean isDiffFolderCreated=false;
	 // generate diffs
		String outputDiffFilePath="";
		 for (String changedFilePath : listOfChangedFiles) {
				    String changedFilePathSplit[] =changedFilePath.split("/");
				    String chnagedFileName= changedFilePathSplit[changedFilePathSplit.length-1];
					System.out.print("Detect change in "+ changedFilePath);
					String newUpdatedFilePath= pathClone +migrateAtCommitName+"/" + changedFilePath;
					String oldFilePath= pathClone +previousCommitName+"/" + changedFilePath ;
					outputDiffFilePath=outputDiffsPath +"diff_"+ chnagedFileName+".txt";
					// Make sure the file has call from old function before update and call from new library after update 
				 if ( cleanJavaCode.isUsedNewLibrary(oldFilePath,MigratedLibraries.fromLibrary)
					  &&cleanJavaCode.isUsedNewLibrary(newUpdatedFilePath,MigratedLibraries.toLibrary) 
						 ){
					// create folder only for one time
					 if(isDiffFolderCreated==false){
						isDiffFolderCreated=true;
						terminalCommand.createFolder(outputDiffsPath);
					}
					terminalCommand.createDiffs(oldFilePath, newUpdatedFilePath, outputDiffFilePath);
					//Copy real file before and after migration
					terminalCommand.copyFile( oldFilePath, outputDiffFilePath.replace(".txt", "_before.java"));
					terminalCommand.copyFile( newUpdatedFilePath, outputDiffFilePath.replace(".txt", "_after.java"));
					  
					 
					diffsFilePath.add(outputDiffFilePath);
					
				
                }else{
                	System.err.print("|ignored because old or new file NOT used libraries functions \n");
                }	
		 }
 
	return diffsFilePath;
		
 }

}
