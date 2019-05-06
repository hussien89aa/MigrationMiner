package com.project.info;

import java.nio.file.Paths;
import java.util.ArrayList;

import com.database.mysql.RepositoriesDB;
import com.database.mysql.Repository;
import com.subversions.process.GitHubOP;
 

public class CollectAppCommits {
 
	static String pathClone=Paths.get(".").toAbsolutePath().normalize().toString() +"/Clone/";
	public static void main(String[] args){
		ArrayList<Repository> listOfRepositores= new RepositoriesDB().getRepositories();
		for (Repository repository : listOfRepositores) {
			System.err.println("Start process:AppID:"+repository.AppID+ ",AppLink:"+repository.AppLink);
			GitHubOP gitHubOP= new GitHubOP(repository.AppLink,pathClone);
			gitHubOP.cloneApp( );
			String LOG_FILE_NAME= repository.AppID +"_commits.txt";
			gitHubOP.generateLogs(LOG_FILE_NAME);
			// save commit info in database
			//ArrayList<Commit> commitList= gitHubOP.readLogFile(pathClone + LOG_FILE_NAME);
			
			String appFolder=GitHubOP.getAppName(repository.AppLink);
			if(appFolder==""){	continue;}
			String appPath=pathClone+appFolder;
			gitHubOP.deleteFolder(appPath);
		}
		System.out.println("Process is done");
	}
}
