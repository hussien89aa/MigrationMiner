package com.project.info;

import java.util.ArrayList;
 
import com.database.mysql.LibraryDocumentationDB;
import com.library.Docs.MethodDocs;
import com.library.source.MethodDoc;
import com.library.source.MethodObj;
import com.library.source.MigratedLibraries;
 
 

// Write here list Cartesian Product for any list of objects
public class CartesianProduct {

	public ArrayList<CPObject> generateProjectLibrariesCP(ArrayList<CPObject> listOfProjectLibrariesCP,ArrayList<Project> l1, ArrayList<Project> l2){
		// ArrayList<CPObject> listOfProjectLibrariesCP= new  ArrayList<CPObject> ();
		
		// IF the CP is large then ingore this process Set max size of CP
		if(l1.size()>100|| l2.size()>100){
			return listOfProjectLibrariesCP;
		}
		
		//-------------------------------------------
		// Do not include in the search Upgrade Process
		ArrayList<Project> l1Temp = new ArrayList<Project> ();
		l1Temp.addAll(l1);
        ArrayList<Project> l2Temp = new ArrayList<Project> ();
        l2Temp.addAll(l2);
       
		 for (Project project2 : l2) {
			 
			 for (Project project1 : l1) {
				 if(isUpgradeProcess(project1.LibraryName,project2.LibraryName)){
					 l1Temp.remove(project1) ;
					 l2Temp.remove(project2) ;
				 }
			 }
		 }
		//-------------------------------------------
		 
		 for (Project project2 : l2Temp) {
 
			 for (Project project1 : l1Temp) {
				 //Remove Version index
				 project2.LibraryName= libraryWithoutVersion(project2.LibraryName);
				 project1.LibraryName= libraryWithoutVersion(project1.LibraryName);
				 CPObject cPObject= new CPObject(project2.LibraryName, project1.LibraryName);
				 int index=cPObject.isFoundUnique( listOfProjectLibrariesCP);
				 if(index!=-1){
					 // is found
					 listOfProjectLibrariesCP.get(index).Frequency=listOfProjectLibrariesCP.get(index).Frequency+1;
				 }else{
					 listOfProjectLibrariesCP.add(cPObject);
				 }
				 
			  }
		  }
		 
		 return listOfProjectLibrariesCP;
	}
	
	public static String libraryWithoutVersion(String librarName){
		 
		String[] AppInfo=librarName.split(":");

		return  AppInfo[0] + ":"+ AppInfo[1] + ":xxx";
	}
	public boolean isUpgradeProcess(String libraryName1,String libraryName2){
		 
		String[] librarName1sp=libraryName1.split(":");
		String[] librarName2sp=libraryName2.split(":");
		if(librarName1sp[0].trim().startsWith(librarName2sp[0].trim()) ||
				librarName2sp[0].trim().startsWith(librarName1sp[0].trim()) ||
				librarName1sp[1].trim().startsWith(librarName2sp[1].trim()) ||
				librarName2sp[1].trim().startsWith(librarName1sp[1].trim()) 
				){
			return true;
		}
		
		return false;
	}
	// generate CP between two list
	public ArrayList<CPObject> generateListCP(ArrayList<CPObject> listOfCP,ArrayList<String> l1, ArrayList<String> l2){
 
		 for (String funcName2 : l2) {
 
			 for (String funName1 : l1) {
				 CPObject cPObject= new CPObject(funcName2, funName1);
				 int index=cPObject.isFound( listOfCP);
				 if(index!=-1){
					 // is found
					 listOfCP.get(index).Frequency=listOfCP.get(index).Frequency+1;
				 }else{
					 listOfCP.add(cPObject);
				 }
				 
			  }
		  }
		 
		 return listOfCP;
	}
	
 

	
}
