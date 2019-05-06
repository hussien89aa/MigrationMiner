package com.main.parse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.database.mysql.AppCommitsDB;
import com.database.mysql.MigrationRuleDB;
import com.database.mysql.ProjectLibrariesDB;
import com.library.source.DownloadLibrary;
import com.project.info.CPObject;
import com.project.info.CartesianProduct;
import com.project.info.FilterCartesianProduct;
import com.project.info.Operation;
import com.project.info.Project;

/**
 * This file responsible for generate migration rules using Cartesian Product
 *
 */
public class MigrationRulesClient {

	public static void main(String[] args) {
		new MigrationRulesClient().start();
		
	}
	
	void start(){
		//HashMap<Integer, Integer> yearOfMigration= new HashMap<Integer, Integer>();
		ProjectLibrariesDB projectDB=	new ProjectLibrariesDB();
	    System.out.println("*****Loading all projects libraries (will take some time) *****");
		ArrayList<Project> listOfProjectLibraries= projectDB.getProjectLibraries();
		ArrayList<Project> listOfAddedLibraries= new ArrayList<Project>();
		ArrayList<Project> listOfRemovedLibraries= new ArrayList<Project>();
		 ArrayList<CPObject> listOfProjectLibrariesCP= new ArrayList<CPObject>();	
		CartesianProduct cartesianProduct= new CartesianProduct();
         String oldcommitID="";
         String oldPomPath="";
         String newcommitID="";
         int oldProjectsID=0;
         int totalLibraries=listOfProjectLibraries.size();
         int currentLibraries=0;
         System.out.println("***** Start searching for CP *****");
		for (Project project : listOfProjectLibraries) {
			currentLibraries++;// current processing project
			newcommitID=project.CommitID;
			//System.out.println( "CommitID:"+project.CommitID +"LibraryName:"+project.LibraryName);
			// move to next project to search in
			//TODO: This code cannot find migration in last commit
			if (project.ProjectID!=oldProjectsID){
				   oldProjectsID=project.ProjectID;
				   listOfAddedLibraries.clear();
		    	   listOfRemovedLibraries.clear();
		    	   oldcommitID="";
		    	   oldPomPath="";
			}
			// Move  to next project in one repository
			if(oldPomPath.equals(project.PomPath)==false){
				   listOfAddedLibraries.clear();
		    	   listOfRemovedLibraries.clear();
		    	   oldcommitID="";
		    	   oldPomPath=project.PomPath;
			}
           
           //make sure to process the library for every app in git rep in every commit
           
           if ( oldcommitID.equals(project.CommitID)==false){
        	   // make sure there is added and remove library in that commit 
        	   //Here we only consider library that added and removed
	           if(listOfAddedLibraries.size()>0 && 
	        		   listOfRemovedLibraries.size()>0){
	        	   System.out.println("("+ currentLibraries +"-" +totalLibraries +")--> Find CP between:"+ project.CommitID + "<==>"+ oldcommitID);
	        	  if(isCommitsSequence(oldcommitID, newcommitID)==false){
	        		   System.err.println("==>This CP ingored because uncorrect order commits in between");
	        		   return;
	        	   }else{
	        		 //  int yearOfCommit=new AppCommitsDB().getCommitYear(oldProjectsID,oldcommitID);
	        		   
	        		   //System.err.println(yearOfCommit);
	        		   listOfProjectLibrariesCP=cartesianProduct.generateProjectLibrariesCP(listOfProjectLibrariesCP,listOfAddedLibraries, listOfRemovedLibraries);
	        	  }
	        	 
	             }
        	   
        	   //oldPomPath  = project.PomPath;
        	   oldcommitID = project.CommitID;
         	   listOfAddedLibraries.clear();
	    	   listOfRemovedLibraries.clear();
           }
           // added library in list and removed library in list
           if(project.isAdded==Operation.added){
    		   listOfAddedLibraries.add(project);
    	   }else{
    		   listOfRemovedLibraries.add(project); 
    	   }
           
          // System.out.flush();
		}
		// last case 
        if(listOfAddedLibraries.size()>0 && 
     		   listOfRemovedLibraries.size()>0){
        	
           if(isCommitsSequence(oldcommitID, newcommitID)==false){
        	   System.err.println("==>This CP ingored because uncorrect order commits in between");
              return;
       	   }else{
       		   listOfProjectLibrariesCP=cartesianProduct.generateProjectLibrariesCP(listOfProjectLibrariesCP,listOfAddedLibraries, listOfRemovedLibraries);
       	   }
          }
 
		//filter
        System.out.println("***** List of Filter Cartesian Product *****");
		FilterCartesianProduct filterCartesianProduct= new FilterCartesianProduct();
		 ArrayList<CPObject> listOfFilterLibraries= 	filterCartesianProduct.filter(listOfProjectLibrariesCP);
			
			Double threeShold=1.0;
			int frequency= 3; // number of times we see that relation //TODO: update to 3 as min
			int numberOfTureMigrationRules=0;
			int numberOfTureUpgradeRules=0;
			int numberOfFalseRules=0;
	         StringBuilder upgrades= new StringBuilder();
	         StringBuilder migrations= new StringBuilder();

	     //	DownloadLibrary downloadLibrary = new DownloadLibrary();
	     	MigrationRuleDB migrationRule= new MigrationRuleDB();
			for (CPObject cpObject : listOfFilterLibraries) {
				if(cpObject.Accuracy>=threeShold && cpObject.Frequency>=frequency){
					// Download the library
					//TODO: return download back or moved in mapping step
					//downloadLibrary.downalod(cpObject.value1);
					//downloadLibrary.downalod(cpObject.value2);
					//if(downloadLibrary.isLibraryFound(cpObject.value1)==false ||
					//		downloadLibrary.isLibraryFound(cpObject.value2)==false	){
					//	System.err.println("Cannot download either "+ cpObject.value1+ " or "+ cpObject.value2 );
					//	numberOfFalseRules++;
						//TODO: need to move process of downlaod jar to place of generate fragment
						//continue;
					//}
					String[] AppInfo=cpObject.value1.split(":");
					String[] AppInfo2=cpObject.value2.split(":");
					if(AppInfo2[1].equals(AppInfo[1])==false){
						migrations.append( cpObject.value1 +" <======> "+ cpObject.value2 + "\t| frequency:"+ cpObject.Frequency +"| Ratio:"+ cpObject.Accuracy*100 +"%|");
						migrations.append("\n");
						
					numberOfTureMigrationRules++;
					//TODO: return back
					 migrationRule.addMigrationRule(cpObject.value1, cpObject.value2, cpObject.Frequency,cpObject.Accuracy*100);
					}else{
						upgrades.append( cpObject.value1 +" <======> "+ cpObject.value2 + "\t| frequency:"+ cpObject.Frequency +"| Ratio:"+ cpObject.Accuracy*100 +"%|");
						upgrades.append("\n");
						numberOfTureUpgradeRules++;
						//TODO: return back
					 migrationRule.addMigrationRule(cpObject.value1, cpObject.value2,cpObject.Frequency,cpObject.Accuracy*100);
					}
				}else{
					numberOfFalseRules++;
				}
			}
	         System.out.println("\n**************************************");
	         System.out.println("************* Migrations **************");
	         System.out.println("**************************************\n");
	         System.out.println(migrations.toString());
	         
			 System.out.println("\n**************************************");
			System.out.println("************* upgrades **************");
			 System.out.println("**************************************\n");
			System.out.println(upgrades.toString());
			
			System.err.println("\n**************************************");
			System.err.println("************* Summary Report **************");
			 System.err.println("**************************************");
			System.err.println("With threeShold: "+threeShold +
					"\nTotal Migrations: "+ listOfFilterLibraries.size()+
					"\nTrue Rules:"+ (numberOfTureMigrationRules+ numberOfTureUpgradeRules)+" (Migration:"+ numberOfTureMigrationRules + ", Upgrade:"+ numberOfTureUpgradeRules +
					")\nFalse Rules:"+ numberOfFalseRules);
			 System.err.println("**************************************\n");
			 System.out.println("Note: In some cases we cannot find library if isnot hosted in MVN repositories\n"
			 		+ "If you want to integrate that type of library you need to add in foler 'jar' in our project before run this file");
	}
	
	// Check if the two commit are done in sequence one after another
	public boolean isCommitsSequence(String oldcommitID, String newcommitID){
		 String[] spNewCommitID=newcommitID.split("_");
  	   String[] spOldCommitID=oldcommitID.split("_");
  	   int newCommitNumber=Integer.parseInt(spNewCommitID[0].substring(1));
  	   int oldCommitNumber=Integer.parseInt(spOldCommitID[0].substring(1));
  	 //System.err.println(newCommitNumber+ "="+ oldCommitNumber);
  	   if((newCommitNumber-oldCommitNumber)>=0){
  		 return true;
  	   }else{
 		  System.err.println("Missing version="+ (newCommitNumber-oldCommitNumber));
  		 return false;
  	   }
	}
}
