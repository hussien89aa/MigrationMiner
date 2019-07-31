package com.database.mysql;

public class MigrationSegments {
	public int AppID;
	public String FromCode;
	public String ToCode;
	public String CommitID;
	public String fileName;
	public String fromLibVersion;
	public String toLibVersion;

	public MigrationSegments() {
		this.AppID = 0;
		this.CommitID = null;
		this.FromCode = null;
		this.ToCode = null;
		this.fileName = null;
		this.fromLibVersion = null;
		this.toLibVersion = null;
	}

	public MigrationSegments(int AppID, String CommitID, String FromCode, String ToCode, String fileName,
			String fromLibVersion, String toLibVersion) {
		this.AppID = AppID;
		this.CommitID = CommitID;
		this.FromCode = FromCode;
		this.ToCode = ToCode;
		this.fileName = fileName;
		this.fromLibVersion = fromLibVersion;
		this.toLibVersion = toLibVersion;

	}

}
