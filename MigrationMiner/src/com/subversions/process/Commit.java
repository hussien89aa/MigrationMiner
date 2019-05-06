package com.subversions.process;

import java.util.ArrayList;

public class Commit implements Comparable<Commit> {
	public String commitID;
	public String developerName;
	public String commitDate;
	public String commitText;
	public ArrayList<CommitFiles> filePath= new ArrayList<CommitFiles>();
	public Commit(String commitID,String developerName,String commitDate,String commitText,ArrayList<CommitFiles> filePath){
		this.commitID=commitID;
		this.developerName=developerName;
		this.commitDate=commitDate;
		this.commitText=commitText;
		this.filePath.addAll(filePath);
	}
	@Override
	public int compareTo(Commit o) {
		 
		return o.filePath.size()-this.filePath.size();
	}
	
	//Get prev commit info by current commit ID
	public Commit prevCommit(ArrayList<Commit> commitList){
		Commit previousCommitID = null;
		for(int i=0;i< commitList.size();i++){
			if(commitList.get(i).commitID.contains(this.commitID)){
				return previousCommitID;
			}
			previousCommitID= commitList.get(i);
		}
		
		return previousCommitID;
				
	}
	//Get commit info by  ID
	public Commit commitFullInfo(ArrayList<Commit> commitList){
		Commit previousCommitID = null;
		for(int i=0;i< commitList.size();i++){
			if(commitList.get(i).commitID.contains(this.commitID)){
				return commitList.get(i);
			}  
		}
		
		return previousCommitID;
				
	} 
	public void print(){
		System.out.println("---------");
		System.out.println("commitID: "+ commitID);
		System.out.println("commitID: "+ developerName);
		System.out.println("commitID: "+ commitDate);
		System.out.println("commitID: "+ commitText);
		for (CommitFiles file : filePath) {
			System.out.println(file.firstFile);
		}
	}
}
