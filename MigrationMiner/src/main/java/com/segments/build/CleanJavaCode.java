package com.segments.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.library.source.ClassObj;
import com.library.source.ClassObjInfo;
import com.library.source.ClassStructure;
import com.library.source.MigratedLibraries;
import com.library.source.Translate;


public class CleanJavaCode {
	ClassStructure classStructure =new ClassStructure();
	 
	public CleanJavaCode(){
	
	}
 	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="/Users/hussienalrubaye/Documents/workspace/FunMapping/Clone/Diffs/3/v8398_78c6a1fcae718f5edd177256ab157f555b84f37a/diff_AbstractDatabaseAppenderTest.java.txt";
		ArrayList<String> diffsFilePath= new ArrayList<String>();
		diffsFilePath.add(path);
		MigratedLibraries.fromLibrary="XXX:easymock:3.4";
		MigratedLibraries.toLibrary="XXX:mockito-core:2.2.7";
		 ArrayList<Segment> segmentList =new CleanJavaCode().getListOfCleanedFiles(path,diffsFilePath);
		//System.out.println("----------------- Print segment at main method -----------------");
		// for (Segment segment : segmentList) {
		//	 printSegment(segment);
		//}
	}
	
	 // get all segment that found in one project
	public ArrayList<Segment> getListOfCleanedFiles(String path,ArrayList<String> diffsFilePath){
		 System.out.println("\n**************** Start cleanning file from Java code **************");
	 
		 ArrayList<Segment> segmentList =  new ArrayList<Segment>();
		 for (String diffPath : diffsFilePath) {
            System.out.println("=========>======"+ diffPath );
		    	  System.out.println("Start Cleanning File:"+ diffPath);
		    	  ArrayList<Segment> listOfblocks = startClearn(diffPath);
		    	  if(listOfblocks.size()>0){
		    		  segmentList.addAll( listOfblocks );
		    	  }
 
		 }
		 return segmentList;
		  
	}
 
	public ArrayList<Segment> startClearn(String diffFilePath){

		ArrayList<Segment> listOfblocks =getBlocksList(diffFilePath);
		try{
 
		    StringBuilder fileWithoutJavaCode = new StringBuilder();	    
		    // read list of blocks
			 for (Segment segment:listOfblocks) {
			   	 System.out.println("Remove:"+ segment.getCountRemovedLines());
				 System.out.println("Add:"+ segment.getCountAddLines());
				 for(String lineIn: segment.blockCode){
					 fileWithoutJavaCode.append(lineIn);
					 fileWithoutJavaCode.append("\n");
					 System.out.println(lineIn);
				 }	    
				 System.out.println("----------------------------");
			 }
		 ArrayList<String> listOfCleanedDiff= new  ArrayList<String>();	  
		     // write code segment to new clean file
	       if(fileWithoutJavaCode.length()>0){ 
	    	   String cleanDiffPath=diffFilePath.replace(".txt", "_clean.txt");
	    	   listOfCleanedDiff.add(cleanDiffPath);
			   FileWriter fr = new FileWriter(cleanDiffPath); // After '.' write
			   fr.write(fileWithoutJavaCode.toString()); // Warning: this will REPLACE your old file content!
			   fr.close();
			   System.out.println("Complete Clean Up File successfully\n");
		 
		   
			   // Apply algorithims on the functions
			   //Just show data finding  on console
               // new SegmentRule(listOfblocks);
	    	 }else{
					try{
						TerminalCommand terminalCommand= new TerminalCommand();
						
						 terminalCommand.deleteFolder(diffFilePath);
						 terminalCommand.deleteFolder(diffFilePath.replace(".txt", "_before.java"));
						 terminalCommand.deleteFolder(diffFilePath.replace(".txt", "_after.java"));
						 System.err.println("Delete File because it dosenot have migration");
						 
						 // delete DR if there is no files there
						 String commitDir= diffFilePath.substring(0,diffFilePath.lastIndexOf("/"));
						 File folderDir = new File(commitDir);
						 if(folderDir.isDirectory()){

								if(folderDir.list().length<=0){
									terminalCommand.deleteFolder(commitDir);
									System.err.println("Directory is empty! will delete it");

								}
							}
					} catch (Exception ex) {}
	    	 }
	       

		    } catch (IOException e) {
		       // do something
		    }
		
		return listOfblocks;
	}
	
	public   ArrayList<Segment>  getBlocksList(String diffFilePath){
		
		ArrayList<Segment>  listOfblocks = new ArrayList<Segment> ();
        
		ArrayList<String> listOfAddLibraryClassesName= classStructure.getLibraryClasses(MigratedLibraries.toLibrary);
		ArrayList<String> listOfRemovedLibraryClassesName= classStructure.getLibraryClasses(MigratedLibraries.fromLibrary);
		ArrayList<String> listOfAllClassesName= new ArrayList<String>();
		listOfAllClassesName.addAll(listOfAddLibraryClassesName);
		listOfAllClassesName.addAll(listOfRemovedLibraryClassesName);
		HashMap<String,String> listOfClassesInsatnces= listOfLibraryClassesInstance(listOfAllClassesName,diffFilePath);
		ArrayList<String> listOfAllRemovedStaticMethod= classStructure. getStaticMethods(MigratedLibraries.fromLibrary);
		ArrayList<String> listOfAllAddedStaticMethod= classStructure. getStaticMethods(MigratedLibraries.toLibrary);
		// This will hold previous segment to 
		//catch case when complete block removed and complete block is removed
		 //TODO: we need to add ablility to read metho that lives in multi line
		try{
		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(diffFilePath) ));         
		    String line;
		    boolean isApenndLine=false; // if developer used '.' to refer to instance from prevouse line
		    boolean isScannedLine=false;
		 
		    ArrayList<String> junkText= new  ArrayList<String>();// save junk temprory before move
		    while ((line = br.readLine()) != null) {
		    	
		    	// ignore empty line that that has only >, or <
		    	if(line.trim().length()<=1){
		    		continue;
		    	}
 
		    	//ignore commented line
		    	String lineClean=cleanLineOfCode(line);
		    	if(lineClean.startsWith("*") || lineClean.startsWith("/") ){
		    		continue;
		    	}
		 
		    	isScannedLine=false; // if the line is procsses already
		    	if(line.length()<=0){ continue;}
	 
		    	if (isStartwithNumber( line)){
		    	 
		    	 
		    		Segment cleanBlockSegment=isGoodBlock(junkText,listOfClassesInsatnces,
		    				listOfAddLibraryClassesName,listOfRemovedLibraryClassesName );
		    		if(cleanBlockSegment.addedCode.size()>0 && cleanBlockSegment.removedCode.size()>0 ){
			    		//Segment segment= new Segment(cleanBlockSegment.blockCode,cleanBlockSegment.countAddLines,cleanBlockSegment.countRemovedLines);
		    			cleanBlockSegment.addFileName(diffFilePath);
		    			listOfblocks.add(cleanBlockSegment);
			    	 }
		    	 
			    	 junkText= new  ArrayList<String>();
			    	  
		    	}
		  
		    	
		    	// if it use library class
		    	for (String className : listOfAllClassesName) {
					if(line.contains(className)){
						junkText.add(line);
						isScannedLine=true;
						isApenndLine=true;
				 
						
						break;
					}
				}
		    	if(isScannedLine){ continue ;} ;
		    	
			    	// if it use library instace
	           for (String classInstance : listOfClassesInsatnces.keySet()) {
	        	   if(line.contains(classInstance +".") ){
	        		  // line=line.replace(classInstance +".", listOfClassesInsatnces.get(classInstance) +".");
						junkText.add(line);
						isScannedLine=true;
						isApenndLine=true;
						// make sure we used added and removed library
						break;
					}
				}
	           if(isScannedLine){ continue ;} ;
		         
	        
	       	// see if he use static method
	    	   if(line.trim().startsWith("<"))
        	   {
			       	for (String methodName : listOfAllRemovedStaticMethod) {
			       		// either line stated with method with space or return data from method assgin to object
						if(line.contains(" "+ methodName +"(") || line.contains("="+ methodName +"(")){
							junkText.add(line);
							isScannedLine=true;
							isApenndLine=true;
					 
							
							break;
						}
					}
        	   }
	    	   if(line.trim().startsWith(">"))
        	   {
			       	for (String methodName : listOfAllAddedStaticMethod) {
						if(line.contains(" "+ methodName +"(") || line.contains("="+ methodName +"(")){
							junkText.add(line);
							isScannedLine=true;
							isApenndLine=true;
					 
							
							break;
						}
					}
        	   }
	    	   
	    	   
	           if(isScannedLine){ continue ;} ;
	           
	        	   /*
	        	    * In case developer used '.' instance 
	        	    * ex
	        	    * return new OkHttpClient.Builder()
					*	>                 .connectTimeout(15, TimeUnit.SECONDS)
					*	>                 .build();
					*Read this data and change it to
					* return new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build();
	        	    */
		           if(startWithDot(line)==true && isApenndLine==true){
                       String prevousLine= junkText.get(junkText.size()-1);
                       String newLineConcat= prevousLine.trim() + cleanLineOfCode( line);
		        	   junkText.set(junkText.size()-1, newLineConcat);
		           }else{
		        	   
		        	 
		        	   // another line of code added or removed
		        	   if(line.trim().startsWith(">")|| line.trim().startsWith("<"))
		        	   {
		        		   isApenndLine=false; 
		        	   
		        	   }else{
		        		   junkText.add(line);
		        	   }
		           }
	       
		           
	           
		  		
		     }// while
	
		    // check if last block is vaild
		    Segment cleanBlockSegment=isGoodBlock(junkText,listOfClassesInsatnces ,
    				listOfAddLibraryClassesName,listOfRemovedLibraryClassesName );
	    	 if(cleanBlockSegment.addedCode.size()>0 && cleanBlockSegment.removedCode.size()>0 ){
	    		 //Segment segment= new Segment(cleanBlockSegment.blockCode,cleanBlockSegment.countAddLines,cleanBlockSegment.countRemovedLines);
	    		 cleanBlockSegment.addFileName(diffFilePath);
	    		 listOfblocks.add(cleanBlockSegment);
	    	 }

		br.close();

	}catch (Exception e) {
		// TODO: handle exception
	}
		
 
   	 return listOfblocks;	

}

	// check if the line is started with "." that mean it continue for prevous line
	boolean startWithDot(String line){
		line=line.trim();
		if(line.length()<2){
			return false;
		}
        String linewithDot= line.trim().substring(1).trim();
        if(linewithDot.indexOf(".")==0  ){
        	return true;
        }else{
        	return false;
        }
	}
	// Return line without >,<
	String cleanLineOfCode(String line){
		if(line.length()<2){
			return "";
		}
        String linewithDot= line.substring(1).trim();
        return linewithDot;
	}
	
	//We know the block is good it it has added and removed lines
	Segment isGoodBlock(ArrayList<String> blockOfJunck,HashMap<String,String> listOfClassesInsatnces,
			ArrayList<String> listOfAddLibraryClassesName, ArrayList<String> listOfRemovedLibraryClassesName){
		
		
		ArrayList<String> blockOfChainingJunck = new ArrayList<String>();
		/*
		 * Same time developer function call on on multi line we will chnage it to one line
		
		String completeFunctionCall="";
		for(int i=0;i< blockOfJunck.size();i++){
			 String lineOfCode= blockOfJunck.get(i);
			 String operation=lineOfCode.substring(0, 1); //either >, or <
			String cleanLine =cleanLineOfCode(lineOfCode);
			//If the line doesnot end with ";" and it isnot "if" that is mean the method in multi line
			if(cleanLine.endsWith(";")==false 
					&& cleanLine.startsWith("if ")==false &&
					cleanLine.startsWith("if(")==false 
					&& cleanLine.startsWith("for ")==false &&
					cleanLine.startsWith("for(")==false  &&
					cleanLine.startsWith("while(")==false &&
					cleanLine.startsWith("while ")==false  ){
				if(completeFunctionCall==""){
					completeFunctionCall=cleanLine;
				}else{
					completeFunctionCall=completeFunctionCall+ cleanLine;
				}
			}if(cleanLine.endsWith(";")==true && completeFunctionCall.length()>0) {
				  String fullCall=operation +" 	"+  completeFunctionCall;
				  blockOfChainingJunck.add(fullCall);
				  completeFunctionCall="";
			}else{
				  blockOfChainingJunck.add(cleanLine);	
			}
			
		}
		
		blockOfJunck.clear();
		blockOfJunck.addAll(blockOfChainingJunck);
		blockOfChainingJunck.clear();
		 */
		
		
		/* work with 'Method chaining'
		 * convert this 
		 * person.setName("Peter").setAge(21).introduce();
		 * to this
		 * person.setName("Peter");
		 *	person.setAge(21);
		 *	person.introduce();
		 */
		
	 
		for(int i=0;i< blockOfJunck.size();i++){
			 String lineOfCode= blockOfJunck.get(i);
			String operation=lineOfCode.substring(0, 1); //either >, or <
			String cleanLine =cleanLineOfCode(lineOfCode);
			if( cleanLine.indexOf(").") >0){
				 
				// in case we have ex, return person.setName("Peter").setAge(21).introduce();
				if(cleanLine.startsWith("return")){
					cleanLine= cleanLine.substring(7,cleanLine.length()).trim(); //7 refer to next char after return
				}
				int classNameIdex=cleanLine.indexOf(".");
				String className= cleanLine.substring(0,classNameIdex ).trim();

				// case when direct greate new instance, new Person().setName("Peter").setAge(21).introduce()
				if(className.startsWith("new ")==true && className.contains("(")==true ){
					className= className.substring(4,className.indexOf("(") ); // 4 for first char after 'new'
				}else if (className.startsWith("new ")==false && className.contains("(")==true  ){
					//He call static method
					// ex: setName("Peter").setAge(21).introduce();
					className="";
					classNameIdex=-1;	
				} 
				
				String methodsName= cleanLine.substring(classNameIdex+1,cleanLine.length() );
				 
				  String[] listOfMethodsp=methodsName.split("\\)\\.");
				  for (String methodName : listOfMethodsp) {
					  if(methodName.endsWith(";")==false)
					   {methodName= methodName +")";}
					  String methodClassInstance= ( className==""?methodName:(className +"."+ methodName));
					  methodClassInstance= methodClassInstance.indexOf(".")==0?methodClassInstance.substring(1):methodClassInstance;
					  String fullCall=operation +" 	"+  methodClassInstance;
					  blockOfChainingJunck.add(fullCall);
				   }
				  
				  //If the code has 'if statment met get i
			}else if( cleanLine.startsWith("if ") || cleanLine.startsWith("if(") ||
					cleanLine.startsWith("while ") || cleanLine.startsWith("while(")){
					ArrayList<String> listOfMethodsCall= new ClassObj().findMethods(cleanLine.trim());
				 
					if(listOfMethodsCall.size()>0){
						  for (String methodCall : listOfMethodsCall) {
							  blockOfChainingJunck.add(operation +" 	"+  methodCall);
						  }
					}
						
			 }else{
				blockOfChainingJunck.add(lineOfCode);
			}
		}
		blockOfJunck.clear();
		blockOfJunck.addAll(blockOfChainingJunck);
		blockOfChainingJunck.clear();
	
		Segment segment = new Segment();
		ArrayList<String> blockOfSignatureJuncks= new ArrayList<String>();
		 if(blockOfJunck.size()==0){ return segment;}// isnot good input
 
			for (int i=0;i<blockOfJunck.size();i++) {
				String junText=blockOfJunck.get(i);
				// we donot process import statement
				if( junText.trim().contains("import ")){
					continue;
				}
				 if(junText.startsWith(">")==true ){
				 
					 // build added lines block
					 String cleanLine =cleanLineOfCode(junText);
					 if(cleanLine.length()>0){
				 
							// find class name
						 	String className=findClassName( listOfAddLibraryClassesName,listOfClassesInsatnces,cleanLine.trim());
						 	//convert function name to signature
						    Translate translate= new Translate(MigratedLibraries.toLibrary);
					        String signature="" ;
					        int equalIndex=junText.indexOf("=");
					        if(equalIndex>0){
					        	// we have return type search for same method name, return type, and number of parameters
					        		signature = translate.methodSignature(cleanLine.substring(equalIndex+1).trim(),className);
					        }
					        if(className != "" &&  signature == ""){
					        	// call instance of the class, search for same method name, class name, and number of parameters
					         	signature = translate.methodClassSignature(className,cleanLine.trim());
					        }
					        // if you cannot find use search for same method name, and number of parameters
					        if(signature == ""){
					        		signature = translate.methodSignature(cleanLine.trim());
					        }
					       
						    // Find signature
					        if(signature!=""){
					        	if (!segment.addedCode.contains(signature)){
					        		segment.addedCode.add(signature);
					        		//blockOfJunck.set(i,"> 	"+ signature);
					        		blockOfSignatureJuncks.add("> 	"+ signature);
					        	} 
					        }
					 }
				 }else if(junText.startsWith("<")==true){
				 
					 
					 // build remove lines block
					 String cleanLine =cleanLineOfCode(junText);
					 if(cleanLine.length()>0){
 
						 	//convert function name to signature
						 	// find class name
						 	String className=findClassName( listOfRemovedLibraryClassesName,listOfClassesInsatnces,cleanLine.trim());
						 	//convert function name to signature
						    Translate translate= new Translate(MigratedLibraries.fromLibrary);
					        String signature="" ;
					        int equalIndex=junText.indexOf("=");
					        if(equalIndex>0){
					        	// we have return type search for same method name, return type, and number of parameters
					        	signature = translate.methodSignature(cleanLine.substring(equalIndex+1).trim(),className);
					        }
					        if(className != "" && signature == ""){
					        	// call instance of the class, search for same method name, class name, and number of parameters
					         	signature = translate.methodClassSignature(className,cleanLine.trim());
					        }
					        // if you cannot find use search for same method name, and number of parameters
					        if(signature == ""){
					        	signature = translate.methodSignature(cleanLine.trim());
					        } 
					      if(signature != ""){
					    	  	if (!segment.removedCode.contains(signature)){
					    	  		  segment.removedCode.add(signature);
							    	  //blockOfJunck.set(i,"< 	"+ signature);
							    	  blockOfSignatureJuncks.add("< 	"+ signature);
					        	} 
					    	  
					      }	
					 }
				 }else {
					 blockOfSignatureJuncks.add(junText);  
				 }
				
			}
			
			/* for debug only
			if((segment.getCountAddLines()>0 || segment.getCountRemovedLines()>0) ){
			 System.out.println("=====> Start processed segment");
			 segment.blockCode.addAll(blockOfSignatureJuncks); 
			 segment.print();
			}*/
			 
				 
			// check if it valid junck has to have hasAddCode=true,hasRemoveCode=true
			if((segment.getCountAddLines()>0 && segment.getCountRemovedLines()>0) ){
				//segment.blockCode.addAll(blockOfJunck);
				segment.blockCode.addAll(blockOfSignatureJuncks); 
			 
			}else{
				//clean the data
				segment.removedCode.clear();
				segment.addedCode.clear();
			 
			}

		 
	 
		  
	    //Segment segment = new Segment(blockOfJunck,countAddLines,countRemovedLines);
		return segment;
	}
 
	String findClassName(ArrayList<String> listOfLibraryClassesName,HashMap<String,String> listOfClassesInsatnces,String lineCode){
		 String className="";
		    
		    // find class name from instance name
		    int equalIndex = lineCode.indexOf("=");
		    if( equalIndex>0){
	        
		        /* In case he use instace from class like 'metaData' use instance of control
		         * < 		IMocksControl control = EasyMock.createStrictControl();
					< 		DatabaseMetaData metaData = control.createMock(DatabaseMetaData.class);
		         */
	        	String instanceDefine =lineCode.substring(equalIndex+1, lineCode.length()).trim();
		        for (String classInstance : listOfClassesInsatnces.keySet()) {
		        	   if(instanceDefine.startsWith(classInstance +".") || 
		        			   instanceDefine.startsWith("this."+classInstance +".") ){
		        		   className= listOfClassesInsatnces.get(classInstance);
		        		   break;
		        	   }
			    }
 
		        /* In case he use static instance from the class
		         * < 		IMocksControl control = EasyMock.createStrictControl();
		         */
		        if(className==""){
			        for (String classInstance : listOfLibraryClassesName) {
			        	   if(instanceDefine.startsWith(classInstance +".")  ){
			        		   className= classInstance;
			        		   break;
			        	   }
				    }
		        }
		        	
			    String varaibleDefine =lineCode.substring(0, equalIndex).trim();
      
		        // line has new class instance like (final int age=12)
		        if(className==""){
		        	//ex-->  final int age
			    	int spaceIndex=varaibleDefine.lastIndexOf(" ");
			    	if(spaceIndex>0){
			    		//ex-->  final int
			    		String define =varaibleDefine.substring(0, spaceIndex).trim();
			    		spaceIndex=define.lastIndexOf(" ");
		    			//ex-->  int
			    		if(spaceIndex>0){
			    			className=define.substring(spaceIndex+1, define.length()).trim();
			    		}else{
			    			className=define;
			    		}
			    	}
		        }
		        
				if(className==""){
			    	// check if it class instance has been initialized here 
			    	// ex--> age=12
			        for (String classInstance : listOfClassesInsatnces.keySet()) {
			        	   if(varaibleDefine.equals(classInstance) ){
			        		   className= listOfClassesInsatnces.get(classInstance);
			        		   break;
			        	   }
				    }
				}
				
				
		    }else {
		    
			    // in case we just call method using class instance
			    if(className=="" ){
				  	   
					 // find class name if the line has instance from class
					    	/*
					    	 * lineCode.startsWith(classInstance +".")   for class instance
		 					lineCode.contains(classInstance +".") for use in if statement
					    	 */
					    for (String classInstance : listOfClassesInsatnces.keySet()) {
				        	   if(lineCode.startsWith(classInstance +".")  || 
				        			   lineCode.startsWith("this."+classInstance +".")  || 
				        			   lineCode.contains(" "+classInstance +".")){
				        		   className= listOfClassesInsatnces.get(classInstance);
				        		   break;
				        	   }
					    }
					  
			    }
			    
			    if(className=="" ){
				  	   
				 // find if he call direct static instance from library class
				    for (String classInstance : listOfLibraryClassesName) {
			        	   if(lineCode.trim().startsWith(classInstance +".")  || lineCode.contains(" "+ classInstance +".") ){
			        		   className= classInstance;
			        		   break;
			        	   }
				    }
					  
			    }
		    }
		    return className;
	}
	 boolean isStartwithNumber(String line){
		 boolean isNumber=false;
		 if (line.startsWith("0")||line.startsWith("1")||line.startsWith("2")||line.startsWith("3")
	    			||line.startsWith("4")||line.startsWith("5")||line.startsWith("6")||line.startsWith("7")
	    			||line.startsWith("8")||line.startsWith("9")){
			 isNumber=true;
		 }
		 return isNumber;
	 }


	// get list of of class instances
	public HashMap<String,String> listOfLibraryClassesInstance(ArrayList<String> listOfClasses, String diffFilePath){
		HashMap<String,String> listOfClassesInstance= new HashMap<String,String>();
		 try{
			    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(diffFilePath) ));         
			    String line;
			   String instanceName;
			    while ((line = br.readLine()) != null) {
			    	// we donot process import statement
					if( line.trim().contains("import ")){
						continue;
					}
					line= cleanLineOfCode(line);
			    	for (String libraryClassName : listOfClasses) {
						if (line.contains(libraryClassName)){
							if(line.length()>2){
								 
								ClassObjInfo classObjInfo=getInstanceName( line, libraryClassName);
								if(classObjInfo.instanceName!=null){
									if(listOfClassesInstance.get(classObjInfo.instanceName) == null){
										listOfClassesInstance.put(classObjInfo.instanceName.trim(),classObjInfo.className);
									}
								}
							}
						}
					}
			    }
				br.close();
			    } catch (IOException e) {
			       System.out.println(e.getMessage());
			    }
		return listOfClassesInstance;
	}
	// This method return instance name
	public ClassObjInfo getInstanceName(String line,String ClassName){
	 ClassObjInfo classObjInfo= new ClassObjInfo();
	 classObjInfo.instanceName=null;
	 classObjInfo.className=null;
	 // in case define and initiailize the instance
	 int hasEqual=line.indexOf("=");
	 int hasbraket=line.indexOf("(");
	 if(hasEqual>0 && hasEqual<hasbraket){
			 
			String instanceSide= line.substring(0, hasEqual).trim();
			int hasspace=instanceSide.lastIndexOf(" ");
			if(hasspace>0){
				classObjInfo.instanceName=instanceSide.substring(hasspace+1, instanceSide.length()).trim();
				String classNameInfo=instanceSide.substring(0, hasspace).trim();
				hasspace=classNameInfo.lastIndexOf(" ");
				if(hasspace>0){
					classObjInfo.className=classNameInfo.substring(hasspace+1, classNameInfo.length()).trim();
				}else{
					classObjInfo.className=classNameInfo;
				}
			} else{
				 classObjInfo.instanceName=instanceSide.trim();
				 classObjInfo.className=ClassName;
			}
	 }else if(line.contains(ClassName + " ")){ //  space mean some one define like  ClassName instance;
			int classIndex= line.indexOf(ClassName + " ")+ ClassName.length()+1;
			 String instanceName=line.substring(classIndex).trim();
			if (instanceName.indexOf(";")>0) {
				 classObjInfo.instanceName=instanceName.substring(0, instanceName.indexOf(";")).trim();
				 classObjInfo.className=ClassName;
			}
			
	 }
	 //In case we have this exxample List<Description>
	if(classObjInfo.className!=null){
		 int indexOfBraket=classObjInfo.className.indexOf("<");
		 int indexOfEndBraket=classObjInfo.className.lastIndexOf(">");
		 if(indexOfBraket>0 && indexOfEndBraket>0){
			 classObjInfo.className=classObjInfo.className.substring(indexOfBraket+1,  indexOfEndBraket).trim();
		 }
	}
	if(classObjInfo.instanceName!=null){
		if(classObjInfo.instanceName.startsWith("this.") && classObjInfo.instanceName.length()>5){
			classObjInfo.instanceName=classObjInfo.instanceName.substring(5);
		}
	}
     return classObjInfo; 
}
	
	//check if the file used that library
	public	boolean isUsedNewLibrary(String filePath,String addedLibraryName){

			ArrayList<String> versionLibrarieslist =classStructure.getLibraryPackages(addedLibraryName);
			boolean isUsed=false;
			   try{
			    	//System.out.println("Path:"+ projectPath);
			       BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath) ));         

			    String line;
			    while ((line = br.readLine()) != null) {
			    	if(line.contains("import")){
			    	 for (String packageName : versionLibrarieslist) {
			    		 if (line.contains(packageName +".") ) { // Why dot because when any one import package he have to add dot like (package.* or package.ClassName
			    			 isUsed=true;
			    			 break;
						 }
				    	}
			        }
			    }
				br.close();
			    } catch (IOException e) {
			       // do something
			    }
			return isUsed;
		}
		
	 

}
