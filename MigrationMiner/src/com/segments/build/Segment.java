package com.segments.build;

import java.util.ArrayList;

import com.library.Docs.MethodDocs;
import com.library.source.MethodObj;

public class Segment implements Comparable<Segment> {
public ArrayList<String> blockCode;
public int frequency;  // home many times we did see this fragment
public double sDegree; // how similar two code are
public ArrayList<String> addedCode  ;
public ArrayList<String> removedCode ;
public int isVaildMapping; // if the mapping is valid or not (0 or 1)
public String fileName; // name of the file that has change

public int AppID;
public String CommitID;

public Segment(){
	this.blockCode=new ArrayList<String>();
	this.frequency=1;
	this.addedCode= new ArrayList<String>();
	this.removedCode= new ArrayList<String>();
}
public Segment(String removedCode,String addedCode, double sDegree){
	this.blockCode=new ArrayList<String>();
	this.addedCode= new ArrayList<String>();
	this.removedCode= new ArrayList<String>();
	this.sDegree=sDegree;
	this.addedCode.add(addedCode);
	this.removedCode.add(removedCode);
}

public static void main(String[] args) {
	
	String filePath="/Users/hussienalrubaye/Documents/workspace/FunMapping/Clone/Process/../Diffs/6/v1160_d8a665f56fadbf81074851bb97192facaf707283/diff_HttpServletResponseProducerTest.java.txt";
	Segment segment= new Segment();
	segment.addFileName(filePath);
	System.out.println(segment.fileName);
}
public void addFileName(String filePath){
	if(filePath.contains("/")){
		String[] path= filePath.split("/");
		filePath= path[path.length-1];
		filePath= filePath.replace(".txt", "").replace("diff_", "");
	}
	
	fileName=filePath;
}
// there different between number of lines in 'addedCode' and  'countAddLines' 
//becuase 'countAddLines' has only new it didnot calculate the  appending line 
//samething for 'removedCode' and 'countRemovedLines'
	public Segment(ArrayList<String> blockCode,
			ArrayList<String> addedCode,
			ArrayList<String> removedCode) {
		this.blockCode=blockCode;
		this.removedCode=removedCode;
		this.addedCode=addedCode;
		this.frequency=1;

		
	}
	public static double getSDegree(ArrayList<Segment> listOfSegment, String inRemovedCode,  String inAddedCode){
		double SDegree=-1.0;
		for (Segment segment : listOfSegment) {
			if(segment.addedCode.contains(inAddedCode) &&
					segment.removedCode.contains(inRemovedCode)){
				SDegree=segment.sDegree;
				break;
			}
		}
		return SDegree;
	}
	public static boolean isFound(ArrayList<Segment> listOfSegment,  ArrayList<String> inAddedCode, ArrayList<String> inRemovedCode){
		boolean isFound=false;
		 
		for (Segment segment : listOfSegment) {
			
			// check if all function founded in added code
			int addedFounded=0;
			for (String funcAdded : segment.addedCode) {
				for (String inFuncAdded : inAddedCode) {
					if(funcAdded.equals(inFuncAdded)){
						addedFounded++;
					}
				}	
			}
			
			// check if all function founded in removed code
			int removedFounded=0;
			for (String funcRemoved : segment.removedCode) {
				for (String inFuncRemoved : inRemovedCode) {
					if(funcRemoved.equals(inFuncRemoved)){
						removedFounded++;
						break;
					}
				}	
			}
			
			if(addedFounded==segment.addedCode.size() &&
					removedFounded==segment.removedCode.size() &&
					inAddedCode.size()==segment.addedCode.size() &&
					 inRemovedCode.size()==segment.removedCode.size() 
					){
				isFound=true;
				break;
			}
			 
		}
		
		return isFound;
	}
	
	public   boolean isFoundSegment(ArrayList<Segment> listOfSegment){
		boolean isFound=false;
		 
		for (Segment segment : listOfSegment) {
			//segment.print();
			//this.print();
			//System.out.println("======================================");
			// check if all function founded in added code
			int addedFounded=0;
			for (String funcAdded : segment.addedCode) {
				for (String inFuncAdded : this.addedCode) {
					if(funcAdded.equals(inFuncAdded)){
						addedFounded++;
					}
				}	
			}
			
			// check if all function founded in removed code
			int removedFounded=0;
			for (String funcRemoved : segment.removedCode) {
				for (String inFuncRemoved : this.removedCode) {
					if(funcRemoved.equals(inFuncRemoved)){
						removedFounded++;
						break;
					}
				}	
			}
			
			if(addedFounded==segment.addedCode.size() &&
					removedFounded==segment.removedCode.size() &&
							this.addedCode.size()==segment.addedCode.size() &&
									this.removedCode.size()==segment.removedCode.size() 
					){
				isFound=true;
				break;
			}
			 
		}
		
		return isFound;
	}
	
	public int getCountAddLines(){ return this.addedCode.size();}
	public int getCountRemovedLines(){ return this.removedCode.size();}
	public int getTotalLinesNumbers(){return this.addedCode.size()*this.removedCode.size(); }
	public void print(){
	 
	   	 System.out.println("Remove:"+  this.getCountRemovedLines());
		 System.out.println("Add:"+ this.getCountAddLines());
		 for(String lineIn: this.blockCode){
			 System.out.println(lineIn);
		 }	
		 if(this.blockCode.size()==0){
			 for(String lineIn: this.addedCode){
				 System.out.println(">  "+ lineIn);
			 }	
			 System.out.println("-------");
			 for(String lineIn: this.removedCode){
				 System.out.println("<  "+ lineIn);
			 }	
		 }
		 System.out.println("----------------------------"); 
	}
 
	@Override
	public int compareTo(Segment o) {
		 int segmentSize=this.getTotalLinesNumbers()-o.getTotalLinesNumbers();
		 // if they have same number of code then order by freqnecy 
		 // the high freqency go first
		 if(segmentSize==0){
			 segmentSize=o.frequency- this.frequency;
		 }
		return segmentSize;
	}
}
