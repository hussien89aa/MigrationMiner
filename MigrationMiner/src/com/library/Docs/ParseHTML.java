package com.library.Docs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.util.Elements;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.database.mysql.LibraryDocumentationDB;
/*
 * This class parse html files and collect methods name and descriptions
 */
public class ParseHTML {
	static String path="/Users/hussienalrubaye/Downloads/lib/data/org/json/JSONML.html";
	public static String pathDocs=Paths.get(".").toAbsolutePath().normalize().toString() +"/Docs/";
	
	// get list of all files from a path
	public void listf(String directoryName, List<File> files) {
	    File directory = new File(directoryName);

	    // Get all the files from a directory.
	    File[] fList = directory.listFiles();
	    if(fList != null){
	        for (File file : fList) {
	            if (file.isFile() && file.getName().endsWith("html")) {
	                files.add(file);
	            } else if (file.isDirectory()) {
	                listf(file.getAbsolutePath(), files);
	            }
	        }
	    }
	}

	//Parse method names and description form html file method 1
	public ArrayList<MethodDocs> parse1(String content) {
		ArrayList<MethodDocs> listOfMethodDocs= new ArrayList<MethodDocs>();
		
		String html="";
		try {
			org.jsoup.nodes.Document doc;
			//System.out.println(content);
			doc = Jsoup.parse(content)	; 
			
			   //read methods details
			   // int countMethods=1;
			   for (Element element : doc.select("div.details>ul.blockList>li.blockList>ul.blockList>li.blockList>ul.blockList>li.blockList")){

				  // System.out.println("Name:"+element.getElementsByTag("h4").text());
				  String fullName="";
				  String codeExample="";
				  for (Element preTag : element.getElementsByTag("pre") ){
					  if(fullName==""){
						  fullName= preTag.text();
						  break;
					  }
				  }
				  fullName = fullName.replaceAll("[\r\n  ]+", " "); // remove muti line from name and double space to one space we added two sapce after \n
				   String description= element.select("div.block").text();
				   
				   for (Element preTag : element.select("div.block") ){
						  if(codeExample=="" && preTag.tagName()=="pre"){
							  codeExample= preTag.text();
							  System.err.println(codeExample);
							  break;
						  }
					  }
				 
				    
	 
				   String returnParamsAll="";
				   String paramsAll="";
				   for (Element paramsDescription : element.select("dl>dt") ){
					   
					   String hasReturn = paramsDescription.select("span.returnLabel").text();
					   if(hasReturn.length()>0){ 
						   try{
							   Element params = paramsDescription.nextElementSibling();
							   do{
								   //System.out.println("returnLabel: "+ params.text() ); 
								   returnParamsAll = returnParamsAll + params.text() +" ||";
								   params = params.nextElementSibling(); 
							   }while(params.nodeName()=="dd");
							   
						   }catch (Exception e) {
							// TODO: handle exception
						   }  
						   // remove last || from string
						   returnParamsAll= returnParamsAll.substring(0, returnParamsAll.length()-2);
						  
					   }
					   String hasprams = paramsDescription.select("span.paramLabel").text();
					   if(hasprams.length()>0){ 
						   try{
							   Element params = paramsDescription.nextElementSibling();
							   do{
								   //System.out.println("paramLabel: "+ params.text() ); 
								   paramsAll = paramsAll + params.text() +" ||";
								   params = params.nextElementSibling(); 
							   }while(params.nodeName()=="dd");
							   
						   }catch (Exception e) {
							// TODO: handle exception
						   }
						   paramsAll= paramsAll.substring(0, paramsAll.length()-2);
							  
					   }
				   }
				  
				 listOfMethodDocs.add(new MethodDocs(fullName, description,  paramsAll,returnParamsAll));
				
				  
				  
			   }
			   
		
				   
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return listOfMethodDocs;
	}
	
	
	//Parse method names and description form html file method 2
	public ArrayList<MethodDocs> parse2(String content) {
		ArrayList<MethodDocs> listOfMethodDocs= new ArrayList<MethodDocs>();
		
		String html="";
		try {
			org.jsoup.nodes.Document doc = Jsoup.parse(content)	; 
			
			   //read methods details
			   // int countMethods=1;
			   for (Element element : doc.select("html>body>h3")){

				 String fullName="";
				 String description= "";
				 String returnParamsAll="";
				 String paramsAll="";
				 
				 Element fullNameElement = element.nextElementSibling();
 
				 if(fullNameElement.nodeName().equals("pre")){
					fullName = fullNameElement.text(); 
				    fullName = fullName.replaceAll("[\r\n  ]+", " "); // remove muti line from name and double space to one space we added two sapce after \n
				   
				    Element dlElements = fullNameElement.nextElementSibling();
				    if(dlElements.nodeName().equals("dl")){
				     
				    	
				    	  Element descriptionElement =null;
				    	  org.jsoup.nodes.Document dlDoc = Jsoup.parse(dlElements.html()) ;
				    	 
				    	  // Get  method description 
			    		  try{
			    			  descriptionElement = dlDoc.select("dd").first();
			    			  description= descriptionElement.text();
					    	  // if line 0 wasnot have description read next line
					    	  if(description.startsWith("Description copied from interface:") || 
					    			  description.startsWith("Description copied from class:")){
						    		  descriptionElement = descriptionElement.nextElementSibling();
							    	  if(descriptionElement!=null){
							    		  description= descriptionElement.text() ; 
							    	  }   
					    	  }
				    	  }catch (Exception e) {}
			    		
				    	  // get retrun and params
				     
				    	 do{
				    		 try{
				    			 //System.out.println("nextChild:"+ nextChild);
				    			 descriptionElement = descriptionElement.nextElementSibling();
				    			 if( descriptionElement.text().startsWith("Parameters: ")){
				    				try{
				    					 org.jsoup.nodes.Document paramDoc = Jsoup.parse(descriptionElement.html()) ;
					    				 Element ddElement= paramDoc.select("dd").first();
					    				 do{
					    					 paramsAll= paramsAll + ddElement.text()  +" ||";
					    					 ddElement = ddElement.nextElementSibling();
					    				 }while( ddElement.nodeName() == "dd");
					    				 
				    				}catch (Exception e) {}
				    				paramsAll= paramsAll.substring(0, paramsAll.length()-2);
				    			 }
				    			 if( descriptionElement.text().startsWith("Returns: ")){
				    				  
				    					try{
					    					 org.jsoup.nodes.Document returnDoc = Jsoup.parse(descriptionElement.html()) ;
						    				 Element ddElement= returnDoc.select("dd").first();
						    				 do{
						    					 returnParamsAll= returnParamsAll + ddElement.text()  +" ||";
						    					 ddElement = ddElement.nextElementSibling();
						    				 }while( ddElement.nodeName() == "dd");
						    				 
					    				}catch (Exception e) {}
				    					returnParamsAll= returnParamsAll.substring(0, returnParamsAll.length()-2);
				    			 }
					    		  //System.out.println("dlElement: "+ descriptionElement.text());
					    		  
				    		 }catch (Exception e) {
								//System.err.println(e.getMessage());
							}
				    		  
				    	  }while( descriptionElement !=null);
				    	  
				    	 
				    	  
				    }
				    
				  }
	
	 
				 listOfMethodDocs.add(new MethodDocs(fullName, description,  paramsAll,returnParamsAll));
				
				  
				  
			   }
			   
		
				   
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return listOfMethodDocs;
	}
	
	//Read file as string
    String readFile(String path, Charset encoding)  throws IOException {
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
	 }
	
    public void start(String directoryName,String LibraryName, int methodType){
    	System.out.println("\n============>  Start collect library documentation from("+ directoryName +") <================");
    	int numberOfParseClasses=0;
    	int numberOfClassesNoDocs=0;
    	int  totalMethodsParsed=0;
    	// get all files in folder
    	List<File> files = new ArrayList() ;
		new ParseHTML().listf(directoryName, files);
		for (File file : files) {
			//System.out.println(file.getName());
			//System.out.println(file.getPath());
			try {
				String packageName=  file.getPath().substring(directoryName.length()+1,file.getPath().length());
				if(packageName.equals(file.getName())){
					//This isnot real documenation, it dosenot have packge
					continue;
				}
				// remove file name from package
				packageName= packageName.substring(0, packageName.length()-(file.getName().length()+1));
				// convert / to .
				packageName = packageName.replaceAll("/", "."); // clean data
				
				//System.out.println("packageName:"+ packageName);
				//System.out.println("\n************************************************************************************************************************");
				 //System.out.println("*************** Parse Class("+ file.getName() +")("+ file.getPath() +")***********");
				//System.out.println("*************** Parse Package("+ packageName +") ***********");
				//System.out.println("************************************************************************************************************************\n");
				
				String content = readFile(file.getPath(), StandardCharsets.UTF_8);
				ArrayList<MethodDocs> listOfMethodDocs  = new ArrayList<>() ;
				 switch (methodType) {
					case 1:
						 listOfMethodDocs = parse1(content);
						break;
					case 2:
						 listOfMethodDocs = parse2(content);
						break;
					default:
						System.err.println("Error can not method type");
						break;
				  }
			
				//Do we have documenation ?
				if(listOfMethodDocs.size() >0){
					
					numberOfParseClasses++;
					 
					//Add in database
					LibraryDocumentationDB libraryDocumentationDB =new LibraryDocumentationDB();
					for (MethodDocs methodDocs : listOfMethodDocs) {
						// methodDocs.print();
						//Save inn database
						String ClassName= file.getName();
						ClassName= ClassName.substring(0,ClassName.length()-5); //remove .html
						//Save only methods that has description
						//if(methodDocs.description.length()>0){
							libraryDocumentationDB.add(LibraryName,packageName, ClassName, methodDocs);
						//}
						
					}
				}else{
					numberOfClassesNoDocs++;
					//System.out.println("Class dosenot have documentation");
				}
				
			    totalMethodsParsed+= listOfMethodDocs.size();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		System.out.println("\n============>  Summary Report <================");
		System.out.println("Number of parsed Classes is ("+ (numberOfParseClasses +numberOfClassesNoDocs) +")");
		System.out.println("Number of parsed Classes have methods description is ("+ numberOfParseClasses +")");
		System.out.println("Number of parsed Classes Donot have methods description is ("+ numberOfClassesNoDocs +")");
		System.out.println("Number of parsed methods is ("+ totalMethodsParsed +")\n");
		 
		
    }
	
    void test(){
    	
    	try{
	    	String file="/Users/hussienalrubaye/Documents/workspace/FunMapping/Docs/junit-4.12-javadoc.jarDocs/org/junit/Assert.html";
	    	String content = readFile(file, StandardCharsets.UTF_8);
			ArrayList<MethodDocs> listOfMethodDocs  =    parse2(content);
			 for (MethodDocs methodDocs : listOfMethodDocs) {
				methodDocs.print();
			} 
		
	    } catch (Exception e) {
			// TODO: handle exception
		}
    }
	public static void main(String[] args) {
			 new ParseHTML().start(pathDocs + "mockito-core-2.23.0-javadoc.jarDocs","mockito",1);
			//new ParseHTML().test();
		
	}

}
