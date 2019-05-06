package com.subversions.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.database.mysql.RepositoriesDB;
import com.subversions.process.*;
public class GitRepositoryManager {
	
	String LOG_FILE_NAME="app_commits.txt";
	static String pathClone=Paths.get(".").toAbsolutePath().normalize().toString() +"/Clone/";
	static String pathfiles=Paths.get(".").toAbsolutePath().normalize().toString() ;
	public static void main(String[] args) {
	 
		 
		System.out.println("process is done");
	}
  
	
	// return list of git repositories with user name and password
	public ArrayList<String> getGitLinks(String appsLinks){
		ArrayList<String> listOfGitRepositories =  new ArrayList<String>();
		try {
			FileInputStream fstream;

				fstream = new FileInputStream(appsLinks);
		
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			//Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null)   {
				  listOfGitRepositories.add(strLine.trim());
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
		
		return listOfGitRepositories;
	}

 
	
    // save this link in clean repo links
	public void saveCleanLinks(String gitLink,String fileName){
		
		 try{
			 System.out.println("Save git link ("+ gitLink +")  to file: (" + pathfiles+"/"+fileName +")");
			    String cmdStr="cd " + pathfiles + " && echo '"+ gitLink + "' >> "+ fileName;
				//System.out.println(cmdStr);
			    Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
			 
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
	}
}
