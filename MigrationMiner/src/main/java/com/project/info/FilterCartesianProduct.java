package com.project.info;

import java.util.ArrayList;

public class FilterCartesianProduct {
	public ArrayList<CPObject> filter(ArrayList<CPObject> listOfCPLibraries){
		 //ArrayList<CPObject> listOfLibraries= new  ArrayList<CPObject> ();
		 
		 for(int i=0;i<listOfCPLibraries.size();i++){
			 CPObject cpObject1=listOfCPLibraries.get(i);
			 int maxFrequency=0;
			 // find the max show times
			 for(int j=0;j<listOfCPLibraries.size();j++){
				 CPObject cpObject2=listOfCPLibraries.get(j); 
				 if(cpObject2.isCleaned==false){
					 if(cpObject1.value1.equals(cpObject2.value1)  /*|| cpObject1.value1.equals(cpObject2.value2)*/ ){
						 
						 if(cpObject2.Frequency>maxFrequency){
							 maxFrequency=cpObject2.Frequency;
						 }
					 }
				 }
			 }
			 // devide the max show times on other value
			 for(int j=0;j<listOfCPLibraries.size();j++){
				 CPObject cpObject2=listOfCPLibraries.get(j); 
				 if(cpObject2.isCleaned==false){
					 if(cpObject1.value1.equals(cpObject2.value1) /*|| cpObject1.value1.equals(cpObject2.value2)*/
							 ){
						 //We use 1.0 to convert double to int
						 listOfCPLibraries.get(j).Accuracy= listOfCPLibraries.get(j).Frequency*1.0/maxFrequency*1.0 ;
						 listOfCPLibraries.get(j).isCleaned=true;
						 }
				 }
			 }
		 }
		 
		 
		 return listOfCPLibraries;
	}
}
