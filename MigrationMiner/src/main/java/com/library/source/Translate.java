package com.library.source;

import java.util.ArrayList;
//This class responsible for convert function  from code to library function signature using
//library schema that we have
public class Translate {

	ArrayList<ClassObj> listOfLibraryClassesObj;
	public Translate(String libraryName) {
	 listOfLibraryClassesObj= new ClassStructure().getLibraryClassesObj(libraryName);
		
	}

	public static void main(String[] args) {
		Translate translate= new Translate("org.slf4j:slf4j-log4j12:1.7.5");
        String methodName="logger.warn('homename', locationIndex);";
        System.out.println(methodName);
        String signature= translate.methodSignature(methodName);
		System.out.println(signature);
		//
	}
	
	public String cleanMethod(String methodInfo){
		int equalIndex=methodInfo.indexOf("=");
		if(equalIndex>0){
			methodInfo=methodInfo.substring(equalIndex+1,methodInfo.length()).trim();
		}
		return methodInfo;
		
	}
	// compare if we know the class that method belongs to
	public String methodClassSignature(String className,String fullFunctionName){
		fullFunctionName=cleanMethod(fullFunctionName);
		MethodObj methodSignatureObj=  MethodObj.GenerateSignature(fullFunctionName);
         if(methodSignatureObj==null){
        	 	return "";
         }
		//methodSignatureObj.print();
		//TODO: it is better to return origin function if cannot find signature
		String functionSignature="";
		
		for (ClassObj classObj : listOfLibraryClassesObj) {
			 if(classObj.className.endsWith("."+className)){
					for (MethodObj methodObj : classObj.classMethods) {
						// either same name or same instance name like logger.warn("A")
						if(  (methodObj.methodName.endsWith("."+methodSignatureObj.methodName)||
								methodSignatureObj.methodName.equals(methodObj.methodName) )&&
								methodSignatureObj.inputParamCompare(methodObj)	){
							functionSignature= methodObj.fullMethodName;
							if(methodSignatureObj.hasGoodMatch(functionSignature)){
								break;
							}
						}
							
					}
					break;
			 }
		
			 
		}
		
		return functionSignature;
	}
	// you know the class name and return type
	public String methodSignature(String fullFunctionName,String className){
		fullFunctionName=cleanMethod(fullFunctionName);
		MethodObj methodSignatureObj=  MethodObj.GenerateSignature(fullFunctionName);
		 if(methodSignatureObj==null){
     	 	return "";
		 }
 
		//TODO: it is better to return origin function if cannot find signature
		 //This code has issue with polimorphisim, needs fix
		String functionSignature="";
		boolean Isfound=false;
		for (ClassObj classObj : listOfLibraryClassesObj) {
					for (MethodObj methodObj : classObj.classMethods) {
						if( (methodObj.methodName.endsWith("."+methodSignatureObj.methodName)||
								methodSignatureObj.methodName.equals(methodObj.methodName) )&&
								methodObj.returnType.endsWith("."+ className )  &&
								methodSignatureObj.inputParamCompare(methodObj)	){
							//methodSignatureObj.print();
							//methodObj.print();
							functionSignature= methodObj.fullMethodName;
							if(methodSignatureObj.hasGoodMatch(functionSignature)){
								Isfound=true;
								break;
							}
						}
							
					}
		 
		if(Isfound){
			break;
		}
			 
		}
		
		return functionSignature;
	}
	
	public String methodSignature(String fullFunctionName){
		fullFunctionName=cleanMethod(fullFunctionName);
		MethodObj methodSignatureObj=  MethodObj.GenerateSignature(fullFunctionName);
		 if(methodSignatureObj==null){
     	 	return "";
		 }
 
		//TODO: it is better to return origin function if cannot find signature
		String functionSignature="";
		boolean Isfound=false;
		for (ClassObj classObj : listOfLibraryClassesObj) {
					for (MethodObj methodObj : classObj.classMethods) {
						if( (methodObj.methodName.endsWith("."+methodSignatureObj.methodName)||
								methodSignatureObj.methodName.equals(methodObj.methodName) )&&
								methodSignatureObj.inputParamCompare(methodObj)	){
							//methodSignatureObj.print();
							//methodObj.print();
							functionSignature= methodObj.fullMethodName;
							 
								// we find match methods in number of param
							if(methodSignatureObj.hasGoodMatch(functionSignature)){
								Isfound=true;
								break;
							}
							 
						}
							
					}
		 
		if(Isfound){
			break;
		}
			 
		}
		
		return functionSignature;
	}
	
 
	
	public void nameToSignature(String fullFunctionName){
		System.out.println("*********\nlist Of Library  Classes Info");
		System.out.println("===========================");
		for (ClassObj classObj : listOfLibraryClassesObj) {
			classObj.print();
			System.out.println("===========================");
			for (MethodObj methodObj : classObj.classMethods) {
				methodObj.print();
			}
			 
		}
	}
}
