package com.subversions.process;

import java.util.ArrayList;

public class Operations {
	 ArrayList<String> months ;
	public Operations(){
		  this.months = new ArrayList<String>();
	      months.add("January");
	      months.add("February");
	      months.add("March");
	      months.add("April");
	      months.add("May");
	      months.add("June");
	      months.add("July");
	      months.add("August");
	      months.add("September");
	      months.add("October");
	      months.add("November");
	      months.add("December");
	}
 


	String correctGitDate(String gitDate){
		String datetime="";
	    String[] spDate=gitDate.split(" ");
	      for(int i=0;i<12;i++){
	 		 if(months.get(i).contains(spDate[1])){
	 			 String startDateString = spDate[4]+ "-"+ ((i+1)<10?"0":"")+ String.valueOf(i+1) +"-"+  (Integer.parseInt(spDate[2])<10?"0":"")+ spDate[2] + " "+ spDate[3];
	 			  return startDateString;
	 		 } 
	      }
	      
	      return "";
	}
}
