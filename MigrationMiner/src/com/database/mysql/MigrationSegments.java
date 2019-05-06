package com.database.mysql;

public class MigrationSegments {
	public int AppID;
	public String FromCode;
	public String ToCode;
	public String CommitID;
	public MigrationSegments(int AppID,String CommitID,String FromCode,String ToCode) {
		this.AppID=AppID;
		this.CommitID=CommitID;
		this.FromCode=FromCode;
		this.ToCode=ToCode;
		
	}

}
