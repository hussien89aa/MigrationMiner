package com.project.info;

import java.io.File;
import java.util.ArrayList;
//This class responsible to find file with specific condition
// like find pom.xml with 'src dir'
public class ProjectFiles {
   String filename="pom.xml";
   String folderNeeded="src";
  
   public ProjectFiles(){
 
   }
	public static void main(String[] args) {
		ProjectFiles projectFiles=	new ProjectFiles( );
		ArrayList<String> listOfPaths=projectFiles.findPomFilePath("/Users/hussienalrubaye/Documents/workspace/FunMapping/Clone/wildfly/","connector");
	for (String path : listOfPaths) {
		System.out.println(path);
	}
	
 
	}
	
	//This function will find pom.xml file in project
	//Pom.xml should lives same direcotry with folder src
public ArrayList<String> findPomFilePath(String projectPath,String appFolder ){
		ArrayList<String> listOfPaths= new ArrayList<String>();
	     String appPath=projectPath + appFolder;
		// search for file in first level dir
		if(isPomFound(appPath)){
			 listOfPaths.add(appFolder);
			 return listOfPaths ;
		} 
		// search for file in second level dir
		//TODO: for know we support only process one project per repo in future we will process multi project per repo
		String[] rootFolders =listOfFolders(appPath);
		 if(rootFolders==null){
			 return listOfPaths;
		 }
	 
		for (String folderName : rootFolders) {
			if(isPomFound(appPath+"/"+folderName)){
				listOfPaths.add(appFolder +"/"+folderName);
			}
	  	 }
	 
		return listOfPaths;
 
}
 
// return list of folders in path
public String[] listOfFolders(String path){
	  File projects = new File(path);
	  return projects.list();
}
 
//check if pom found in that folder 
 boolean isPomFound(String path){
		String[] rootFolders =listOfFolders(path);
		 if(rootFolders==null){
			 return false;
		 }
		boolean srcFolderFound=false;
		boolean pomFileFound=false;
		 File pomFile = new File(path +"/"+filename);
		 if(pomFile.exists()){
			 pomFileFound=true;
				for (String folderName : rootFolders) {
					if(folderName.equals(folderNeeded)){
						srcFolderFound=true;
					}
				} 
		 }
	
		 return pomFileFound && srcFolderFound  ; 
 }

}
