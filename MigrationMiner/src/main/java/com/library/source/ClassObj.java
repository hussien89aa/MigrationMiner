package com.library.source;

import java.util.ArrayList;

public class ClassObj {
	
	public String className;
	public ArrayList<MethodObj> classMethods;
	public String scope;
	public ClassObj() {
		this.className="";
		this.classMethods=new ArrayList<MethodObj>();
		this.scope="";
	}
	public  void setClassName(String lineCode){
		lineCode=lineCode.trim();
		int classNameIndex=lineCode.indexOf("class");
		int interfaceNameIndex=lineCode.indexOf("interface");
		if(classNameIndex==-1 && interfaceNameIndex==-1){
			System.err.println("line donot have class or interface: "+ lineCode);
			return;
		}
		String className="";
		// define class
		if( classNameIndex>=0){
			 className=lineCode.substring(classNameIndex+5).trim();
		    if( className.contains("implements")){
		    	className=className.substring(0, className.indexOf("implements")).trim();
		    } 
		    if( className.contains("extends")){
		    	className=className.substring(0, className.indexOf("extends")).trim();
		    } 
		    
		    if( classNameIndex>0){
		    	 this.scope=lineCode.substring(0,classNameIndex).trim();
		    }
		   
		}else if(interfaceNameIndex>=0){
			    className=lineCode.substring(interfaceNameIndex+9).trim();
			    if( interfaceNameIndex>0){
			    	 this.scope=lineCode.substring(0,interfaceNameIndex).trim();
			    }
		}
		
 
		// remove open class braket if it found
    	int indexofClose=className.lastIndexOf("{");
    	if(indexofClose>0){
    		className=className.substring(0, indexofClose).trim();
    	} 
		
    	/* remove donts it it found
	    if(className.contains(".")){
	    	int lastDotIndex=className.lastIndexOf(".");
	    	className=className.substring(lastDotIndex+1,className.length()).trim();
	    }*/
    	this.className=className;
		
	}
	public void addMethod(String lineCode){
		MethodObj methodObj= MethodObj.GenerateSignature(lineCode);
		if(methodObj!=null){
			classMethods.add(methodObj);
		}
	}
	
	public static void main(String[] args) {
		ClassObj classObj=	new ClassObj();
		classObj.setClassName("public class org.slf4j.impl.StaticMDCBinder {");
		//classObj.print();
		String lineOfCode="if (this.realLog.isErrorEnabled())";
		ArrayList<String> listOfMethodsCall= new ClassObj().findMethods(lineOfCode);
		for (String lineCall : listOfMethodsCall) {
			System.out.println(lineCall);
		}
	}

	/*
	 * This method take input like this
	 * if (this.parent != null && log.isDebugEnabled())
	 * and it will return list of method calls in the statement
	 * like this log.isDebugEnabled()
	 */
	public ArrayList<String> findMethods(String lineOfCode){
		ArrayList<String> listOfMethodsCall= new ArrayList<String>();
		//remove if
		String NotIfContain="";
		if(lineOfCode.startsWith("if"))
			NotIfContain=lineOfCode.substring(3);
		else if(lineOfCode.startsWith("while"))
			 NotIfContain=lineOfCode.substring(5);
		
		String[] andsp= NotIfContain.split("\\&\\&");
		for (String operation : andsp) {
			String[] orsp= operation.split("\\|\\|");
			for (String methodNoClean : orsp) {
				methodNoClean=methodNoClean.trim();
				//remove brakcet from start line like ((this.parent != null we keep only this.parent != null 
				int braketStartIndex=0;
				for(int i=0;i<methodNoClean.length();i++){
					if(methodNoClean.charAt(i)=='(' ){
						braketStartIndex=i+1;
					}else{
						break;
					}
				}
				methodNoClean=methodNoClean.substring(braketStartIndex).trim();
				//remove brakcet from end line like log.isDebugEnabled(a,b)) we keep only log.isDebugEnabled(a,b)
				int braketEndIndex=methodNoClean.length();
				for(int i=methodNoClean.length()-1;i>2;i--){
					if(methodNoClean.charAt(i)==')'){
						if(methodNoClean.indexOf("(")!=methodNoClean.lastIndexOf("("))
						  braketEndIndex=i+2;
						else
							braketEndIndex=i+1;
					}else{
						break;
					}
				}
				//at lest 2 chart in method like a() and should be method call
				if(braketEndIndex>=2 && methodNoClean.indexOf("(")>0 ){
					methodNoClean=methodNoClean.substring(0,braketEndIndex);
					//System.out.println();
					listOfMethodsCall.add(methodNoClean.trim());
				}
			}
		}
		return listOfMethodsCall;
	}
	public void print(){
		System.out.println("Class Name: "+this.className);
		System.out.println("Scope: "+this.scope);
	}
}
