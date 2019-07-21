package com.database.mysql;

public class MigrationRule {
public int ID;
public String FromLibrary;
public String ToLibrary;
public int Frequency;
public Double Accuracy;

	public MigrationRule(int ID,String FromLibrary,String ToLibrary,int Frequency,Double Accuracy) {
	 this.ID=ID;
	 this.FromLibrary=FromLibrary;
	 this.ToLibrary=ToLibrary;
	 this.Frequency=Frequency;
	 this.Accuracy=Accuracy;
	 
	}

}
