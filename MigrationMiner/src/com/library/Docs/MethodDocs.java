package com.library.Docs;

import java.util.ArrayList;

import com.database.mysql.LibraryDocumentationDB;
import com.library.source.MethodObj;

public class MethodDocs {

	public String fullName;
	public String description;
	public String returnParams;
	public String inputParams;
	public String ClassName;
	public String PackageName;
	public MethodObj methodObj;
	public MethodDocs(String fullName,String description,String inputParams,String returnParams ) {
		this.fullName=fullName; 
		this.description=description;
		this.inputParams=inputParams;
		this.returnParams=returnParams;
		this.methodObj= MethodObj.GenerateSignature(fullName.trim());
	}
	public MethodDocs(String fullName,String description,String inputParams,String returnParams,String ClassName ,String PackageName) {
		this.fullName=fullName; 
		this.description=description;
		this.inputParams=inputParams;
		this.returnParams=returnParams;
		this.ClassName=ClassName;
		this.PackageName= PackageName;
		this.methodObj= MethodObj.GenerateSignature(fullName.trim() );
	}
	
	
	   public void print(){
		//int countMethods=1;
		//for (MethodDocs methodDocs : listOfMethodDocs) {
			 System.out.println("\n==============================");
			 System.out.println("Method: "+ this.fullName);
			 System.out.println("Description: "+ this.description);
			 if(this.inputParams.length() >0){
				 System.out.println("param : "+ this.inputParams ); 
			 } 
			 if(this.returnParams.length() >0){
				   System.out.println("Return : "+ this.returnParams );  
			 }
			 System.out.println("------------------------- methodObj -------------------------");
			 this.methodObj.print();
		//}	
	}
	   public void printWithoutObject(){
		//int countMethods=1;
		//for (MethodDocs methodDocs : listOfMethodDocs) {
			// System.out.println("\n---------------- Documenation ---------------- ");
			 System.out.println("Method: "+ this.fullName);
			 System.out.println("Description: "+ this.description);
			 if(this.inputParams.length() >0){
				 System.out.println("param : "+ this.inputParams ); 
			 } 
			 if(this.returnParams.length() >0){
				   System.out.println("Return : "+ this.returnParams );  
			 }
		//}	
	}
	   
	  public void printWithClass(){
		   
		   System.out.println("\n==============================");
			 System.out.println("PackageName: " + this.PackageName);
		  	 System.out.println("ClassName: "+ this.ClassName);
		  	 System.out.println("MethodName: "+ this.fullName);
			 System.out.println("Description: "+ this.description);
			 if(this.inputParams.length() >0){
				 System.out.println("param : "+ this.inputParams ); 
			 } 
			 if(this.returnParams.length() >0){
				   System.out.println("Return : "+ this.returnParams );  
			 }
	   }
	  
	  
	  public static  MethodDocs GetObjDocs(ArrayList<MethodDocs> libraryDocs,MethodObj methodObj ){
		  MethodDocs methodDocsRetrun = new MethodDocs("", "", "", "");
		  // We try to find best fit when this number increase that mean we have better similar
		  //For example with similarDegree=1 only method name are similar, while
		  //similarDegree=2 method name and input param are similar
		  int  similarDegree=0 ; 
		 for (MethodDocs methodDocs : libraryDocs) {
			
              // if method name same we may be good
			 //System.out.println("======Hit methodName ======" +methodDocs.methodObj.methodName  + "=="+ methodObj.methodName);
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && similarDegree < 1 ){		
				// System.out.println("======Hiting ======" );
				 methodDocsRetrun= methodDocs;
				 similarDegree=1;
			
			 }
			 
	           // if method name same we may be good
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && 
					 methodDocs.methodObj.inputParamCompare(methodObj)	&&
					 similarDegree < 2 ){		
				 methodDocsRetrun= methodDocs; // may be same search for more
				 similarDegree=2;
			 }
		
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && 
					 methodDocs.methodObj.inputParamCompare(methodObj)  &&
					 methodDocs.methodObj.inputParamDataType(methodObj) &&
					 similarDegree < 3 ){	
				 similarDegree= 3;
				 methodDocsRetrun= methodDocs;
				 //System.out.println("======Hit inputParamDataType ======" +methodDocs.methodObj.fullMethodName );

				// break; // more likely same
			 }
			 
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && 
					 methodDocs.methodObj.inputParamCompare(methodObj)  &&
					 methodDocs.methodObj.inputParamDataType(methodObj) &&
					 methodObj.fullMethodName.contains(methodDocs.methodObj.packageName)&&
					 similarDegree < 4 ){	
				 similarDegree=4;
				 methodDocsRetrun= methodDocs;

				// break; // more likely same
			 }
			 
			 
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && 
					 methodDocs.methodObj.inputParamCompare(methodObj) &&
					 methodDocs.methodObj.inputParamDataType(methodObj) &&
					 methodDocs.methodObj.returnType== methodObj.returnType &&
					 similarDegree < 5){	
				 similarDegree=5;
				 methodDocsRetrun= methodDocs;
				 break; // more likely same
			 }
			 /*
			 if(methodDocs.methodObj.methodName.equals(methodObj.methodName) && 
					 methodDocs.methodObj.inputParamCompare(methodObj) &&
					 methodDocs.methodObj.inputParamDataTypeCompare(methodObj) &&
					 methodDocs.methodObj.returnType== methodObj.returnType &&
					 similarDegree < 6){	
				 similarDegree=6;
				 methodDocsRetrun= methodDocs;
				 break; // more likely same
			 }*/
			 
		}
		 return methodDocsRetrun;
	  }
	  
		public static void main(String[] args) {
			ArrayList<MethodDocs> fromLibrary = new LibraryDocumentationDB().getDocs("easymock");
			
			MethodObj methodObj= MethodObj.GenerateSignature("IMockBuilder<T> addMockedMethod(Method method)");
			methodObj.print();
			System.out.println("======== Output ========");
			MethodDocs methodDocs =GetObjDocs(fromLibrary, methodObj);
			if(methodDocs.fullName == ""){
				System.out.println(" cannot find");
			}else{
				methodDocs.print();
			}
			
		}

}
