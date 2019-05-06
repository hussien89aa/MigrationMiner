package com.library.source;

import java.util.ArrayList;

	public class MethodObj implements Comparable<MethodObj> {
	public String returnType;
	public String methodName;
	public ArrayList<String> inputParam;
	public String scope;
	public String fullMethodName;
	public String packageName;
	public Double frequency;
	public MethodObj(String returnType,String methodName,String scope) {
		inputParam= new ArrayList<String>();
		this.returnType=returnType;
		this.methodName=methodName;
		this.scope=scope;
		
	}
	public MethodObj() {
		inputParam= new ArrayList<String>();	
		this.returnType="";
		this.methodName="";
		this.scope="";
		this.fullMethodName="";
	}
	public void addInputParam(String inputType){
		inputParam.add(inputType.trim());	
	}
	public String getInputParamAsString(){
		String inputParams="";
		for (String param : inputParam) {
				if(inputParams==""){
					inputParams= param;
				}else{
					inputParams= inputParams+","+ param;
				}
		}
		return inputParams;
	}
	
	public int countOfInputParam(){
		return inputParam.size();
	}

	//TODO: need to be able to process method like sum({1,2,3})
	// This function recive line of code and return method signture 
	public static MethodObj GenerateSignature(String lineCode) {
		lineCode=lineCode.trim();
		MethodObj methodObj= new MethodObj();
		// find index of input param
		int startParamIndex=lineCode.indexOf("("); 
		int endParamIndex=lineCode.lastIndexOf(")");
		if(startParamIndex==-1 || endParamIndex==-1){
			//System.err.println("line donot have method: "+ lineCode);
			return null;
		}
		String inputTypes=lineCode.substring(startParamIndex+1, endParamIndex).trim();
		inputTypes = inputTypes.replaceAll("<([^<]*)>", "<T>"); // replace Answer1<T,A> to Answer1<T> avoid bad split
		// save all input param in list
		if(inputTypes.indexOf(",")>0){
			String[] paramsSP= inputTypes.split(",");
			for (String inputType : paramsSP) {
				methodObj.addInputParam(inputType.trim());
			}
		}else{
			// has only one param
			if(inputTypes.length()>0){
				methodObj.addInputParam(inputTypes.trim());
			}
		}
		// find function name
	    String nameWithDefine=lineCode.substring(0, startParamIndex).trim();
	    if(nameWithDefine.indexOf(" ")>0){
	    	// find function name
	    	int startNameIndex=nameWithDefine.lastIndexOf(" ");
	    	methodObj.methodName=lineCode.substring(startNameIndex+1, nameWithDefine.length()).trim();
	    	//find return type
	    	String returnTypeWithDefine=lineCode.substring(0, startNameIndex).trim();
	    	if(returnTypeWithDefine.indexOf(" ")>0){
	    		int startReturnTypeIndex=returnTypeWithDefine.lastIndexOf(" ");
	    		methodObj.returnType=returnTypeWithDefine.substring(startReturnTypeIndex+1, returnTypeWithDefine.length()).trim();
	    		// find scope
	    		methodObj.scope=lineCode.substring(0, startReturnTypeIndex).trim();
	    	}else{
	    		methodObj.returnType=returnTypeWithDefine;
	    	}
	    }else{
	    	methodObj.methodName=nameWithDefine;
	    }
	    // check if their is static instance
	    methodObj.packageName= getPackageName(methodObj.methodName);
	    methodObj.methodName= removeDot(methodObj.methodName);
 
	    methodObj.fullMethodName=lineCode;
		return methodObj;
	}
	
	// Get name without packages
	public static String removeDot(String name){
		 
		// remove donts it it found
	    if(name.contains(".")){
	    	int lastDotIndex=name.lastIndexOf(".");
	    	name=name.substring(lastDotIndex+1,name.length()).trim();
	    }
	    return name;
	}
	// Get name without packages
	public static String getPackageName(String name){
	 
		 
		// remove donts it it found
	    if(name.contains(".")){
	    	int lastDotIndex=name.lastIndexOf(".");
	    	name=name.substring(0,lastDotIndex).trim();
	    }else{
	    	name="";
	    }
	    return name;
	}
	// Get name without packages
	public String getMethodNameWithoutPackage( ){
		String name=this.methodName;
		// remove donts it it found
	    if(name.contains(".")){
	    	int lastDotIndex=name.lastIndexOf(".");
	    	name=name.substring(lastDotIndex+1,name.length()).trim();
	    }
	    return name;
	}
	
	public static void main(String[] args) {
		 
		MethodObj methodObj= GenerateSignature("public static LogicalOperator valueOf(long.n name)");
		MethodObj methodObj1= GenerateSignature("public static LogicalOperator valueOf(log.java.string name)");
		System.out.println(methodObj.inputParamDataType(methodObj1));
	}
	public boolean inputParamCompare(MethodObj otherMethodObj){
		 boolean isEqual=false;
		 // in case they have same number of param
	     if(this.countOfInputParam()== otherMethodObj.countOfInputParam()){
	    	 	isEqual=true;
	     }
	     
	     /*in case we compare with '...' that could have any number of param
	      *   public static void verify(java.lang.Object...);
	      */
	     if(isEqual==false && countOfInputParam()==1){
		    	  if(this.inputParam.get(0).endsWith("...")){
		    		  isEqual=true;
		    	  }
	     }
	     
	     if(isEqual==false && otherMethodObj.countOfInputParam()==1){
	    	  if(otherMethodObj.inputParam.get(0).endsWith("...")){
	    		  isEqual=true;
	    	  }
        }
	     
	     return isEqual;
	}
	
	public boolean inputParamDataType(MethodObj otherMethodObj){
		
		 if(this.countOfInputParam() == otherMethodObj.countOfInputParam() && this.countOfInputParam()==0){
			 return true;
		 }
		 if(this.countOfInputParam() != otherMethodObj.countOfInputParam()  ){
			 return false;
		 }
		 
		int countOfParams =0;
		//System.out.println(this.fullMethodName +"<===>"+ otherMethodObj.fullMethodName);
		//System.out.println(this.inputParam);
		ArrayList<String> listOfMyParams = new ArrayList<>();
		listOfMyParams.addAll(this.inputParam);
		
		ArrayList<String> listOfOtherParams = new ArrayList<>();
		listOfOtherParams.addAll(otherMethodObj.inputParam);
		
		//System.out.println(otherMethodObj.inputParam);
		for (int i=0;i<listOfMyParams.size();i++) {
            String meDataType = listOfMyParams.get(i).trim();
            String[] inputPramaSp = meDataType.split(" ");
            meDataType = inputPramaSp[0];
        	//System.out.println("==>"+ inputPramaSp.length);
			if(meDataType.contains(".")){
				String[] inputPramaOtherSp = meDataType.split("\\.");
				meDataType= inputPramaOtherSp[inputPramaOtherSp.length-1];
				
			}
			//System.out.println("=="+ meDataType);
             //System.out.println(meDataType);
            if(inputPramaSp.length<1){ continue;}
           // System.out.println(inputPramaSp[0]);
			for (int j=0;j<listOfOtherParams.size();j++)  {
				
				String[] inputPramaOtherSp =listOfOtherParams.get(j).trim().split("\\s+");
				String otherDataType = inputPramaOtherSp[0];
				if(otherDataType.contains(".")){
					
					inputPramaOtherSp = otherDataType.split("\\.");
					otherDataType= inputPramaOtherSp[inputPramaOtherSp.length-1];
				}
				 //System.out.println("=="+ meDataType);
				//System.out.println("=="+ otherDataType);
		             //System.out.println(otherDataType);
		           // if(inputPramaOtherSp.length<1){ continue;}
				//System.out.println(inputPramaOtherSp[inputPramaOtherSp.length-1] +"="+ inputPramaSp[0]);
				if(otherDataType.toLowerCase().startsWith(meDataType.toLowerCase())){
					listOfOtherParams.remove(j);
					countOfParams++;
					break;
				}
			 }
		 }
		// System.out.println(countOfParams);
		// System.out.println((countOfParams == otherMethodObj.inputParam.size()) &&(countOfParams == this.inputParam.size()));
		
		return (countOfParams == otherMethodObj.inputParam.size()) &&(countOfParams == this.inputParam.size());
	}
	public double inputParamComparePercent(MethodObj otherMethodObj){
		double isEqual=0;
		 // in case they have same number of param
	     if(this.countOfInputParam()== otherMethodObj.countOfInputParam()){
	    	 	isEqual=1.0;
	     }
	     
	     /*in case we compare with '...' that could have any number of param
	      *   public static void verify(java.lang.Object...);
	      */
	     if(isEqual==0 && countOfInputParam()==1){
		    	  if(this.inputParam.get(0).endsWith("...")){
		    		  isEqual=1.0;
		    	  }
	     }
	     
	     if(isEqual==0 && otherMethodObj.countOfInputParam()==1){
	    	  if(otherMethodObj.inputParam.get(0).endsWith("...")){
	    		  isEqual=1.0;
	    	  }
       }
	      
	     return Math.abs((this.countOfInputParam()- otherMethodObj.countOfInputParam()) );
	}
	
	
	//Show different in Ratio
	public double inputParamCompareRatio(MethodObj otherMethodObj){
		double diffRation= inputParamComparePercent(otherMethodObj);
		if(diffRation>1){
			diffRation = 1.0-(diffRation/((this.countOfInputParam()+ otherMethodObj.countOfInputParam())*1.0));
		}
	      
	      
	     return diffRation;
	}
	
	public boolean inputParamDataTypeCompare(MethodObj otherMethodObj){
		 boolean isEqual=true;
		 // in case they have same number of param
	     if(this.countOfInputParam()== otherMethodObj.countOfInputParam()){
	    	 	for(int i=0;i<this.countOfInputParam();i++){
	    	 		if(this.inputParam.get(i)!= otherMethodObj.inputParam.get(i)){
	    	 			isEqual=false;
	    	 			break;
	    	 		}
	    	 	}
	     }else{
	    	   isEqual=false;
	     }
	     
	     
	     return isEqual;
	}
	
	/* see if two methods has good match 
	 * To have excellent match they should be similar in number of params and not abstruct
	 */
	 boolean hasGoodMatch(String functionSignature ){
		   boolean hasGoodMatch=true;
		   //We prefer method name over abstruct name,if we cannot find method we will use abstruct
			if(functionSignature.contains("abstract ")==false){
				hasGoodMatch=false;
			}
          
			if(hasGoodMatch==true && this.countOfInputParam()==1){
				 if(this.inputParam.get(0).endsWith("...")){
					 hasGoodMatch=false;
				 }
			}
			
			return hasGoodMatch;
	 }
	public void print(){
		System.out.println(this.fullMethodName);
		System.out.println("Method Name: "+this.methodName);
		System.out.println("Return Type: "+this.returnType);
		if(this.scope.length()>0){
			//TODO: return this System.out.println("Scope: "+this.scope);
		}

		if(this.packageName.length()>0){
			System.out.println("Package Name: "+this.packageName);
		}
		
		int index=1;
		for (String param : this.inputParam) {
			System.out.println("Input Params("+ (index++) +")==> "+ param);
		}
		if(this.inputParam.size()>0){
			System.out.println("All input Params: "+ getInputParamAsString());
		}
		
		System.out.println("-------------------------");
	}
	@Override
	public int compareTo(MethodObj o) {
		// TODO Auto-generated method stub
		return  (int) (   o.frequency- this.frequency);
	}
}
