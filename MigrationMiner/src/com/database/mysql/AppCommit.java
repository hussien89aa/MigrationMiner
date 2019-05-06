package com.database.mysql;

import java.util.Date;

public class AppCommit {
	public int AppID;
	public String CommitID;
	public String DeveloperName;
	public java.sql.Timestamp CommitDate;
	    
	    public AppCommit(){}
		public AppCommit(int AppID,String CommitID) {
			 this.AppID=AppID;
			 this.CommitID=CommitID;
		}
		
		public AppCommit(int AppID,String CommitID,java.sql.Timestamp CommitDate,String DeveloperName) {
			 this.AppID=AppID;
			 this.CommitID=CommitID;
			 this.CommitDate=CommitDate;
			 this.DeveloperName=DeveloperName;
		}
}
