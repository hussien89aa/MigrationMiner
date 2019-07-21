package com.project.info;

import java.util.ArrayList;
 
import com.segments.build.Segment;

public class CPObject {
public String value1;
public String value2;
public int Frequency;
public Double Accuracy;
public boolean isCorrectMapping;
public boolean isCleaned;// save if we applied filter or not on this relation
public CPObject(String value1,String value2){
	this.value1=value1;
	this.value2=value2;
	this.Frequency=1;
	this.isCleaned=false;
	this.Accuracy=0.0;
	this.isCorrectMapping=false;
}
// for class instance
public CPObject(){
	
}

public int isFound(ArrayList<CPObject> listOfCPObject){
	int foundIndex=-1;
	for (int i=0; i<listOfCPObject.size();i++) {
		CPObject cpObject= listOfCPObject.get(i);
		if( (cpObject.value1.equals(this.value1) && cpObject.value2.equals(this.value2) ) ||
				(cpObject.value1.equals(this.value2) && cpObject.value2.equals(this.value1) ) ){
			foundIndex=i;
			break;
		}
	}
 
	return foundIndex;
}
public int isFoundUnique(ArrayList<CPObject> listOfCPObject){
	int foundIndex=-1;
	for (int i=0; i<listOfCPObject.size();i++) {
		CPObject cpObject= listOfCPObject.get(i);
		if( (cpObject.value1.equals(this.value1) && cpObject.value2.equals(this.value2) )){
			foundIndex=i;
			break;
		}
	}
 
	return foundIndex;
}
public static ArrayList<CPObject> sort(ArrayList<CPObject> listOfFilterLibraries){
	
	for(int i=0;i< listOfFilterLibraries.size();i++){
		CPObject cPObject=listOfFilterLibraries.get(i);
		for(int j=0;j< listOfFilterLibraries.size();j++){
			if(cPObject.Frequency>listOfFilterLibraries.get(j).Frequency){
				listOfFilterLibraries.remove(i);
				listOfFilterLibraries.add(i,listOfFilterLibraries.get(j));
				listOfFilterLibraries.remove(j);
				listOfFilterLibraries.add(j,cPObject);
			}
		}
	}
	return listOfFilterLibraries;
	
}

public ArrayList<Segment> objectToSegment(ArrayList<CPObject> listOfCP, double thresholdValue){
	  ArrayList<Segment> listOfSolvedFragments= new ArrayList<Segment>();
	   //ArrayList<CPObject> listOfCPCopy=listOfCP;
	   double oldFilterValue=0.0; //group output according to filter value
	   ArrayList<String> addedCode = new ArrayList<String>() ;
	   ArrayList<String> removedCode = new ArrayList<String>();
		  
	   for (CPObject cpObject : listOfCP) {
		if(cpObject.Accuracy>= thresholdValue){
			
			// group same filter value under one fragments
		  if(oldFilterValue!= cpObject.Frequency){
			  
			  if(addedCode.size()>0 && removedCode.size()>0){
				  Segment  segment= new Segment(new ArrayList<String>(), addedCode,removedCode);
				  listOfSolvedFragments.add(segment);
			  }
			  addedCode = new ArrayList<String>() ;
			  removedCode = new ArrayList<String>();
			
			  oldFilterValue = cpObject.Frequency;
		  }
 
				  addedCode.add(cpObject.value2);
				  removedCode.add(cpObject.value1);
	 
				
		  
		} //end if
	   }// end loop
	   // add last element to group
	   if(addedCode.size()>0 && removedCode.size()>0){
			  Segment  segment= new Segment(new ArrayList<String>(), addedCode,removedCode);
			  listOfSolvedFragments.add(segment);
		}
	   
	   return listOfSolvedFragments;
}

}
