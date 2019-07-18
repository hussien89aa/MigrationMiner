package com.library.lib;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

 
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.library.Docs.MethodDocs;
import com.library.Docs.ParseHTML;
import com.library.source.MethodObj;
import com.segments.build.TerminalCommand;
 /*
  * This Client parse  library method code body anr return then as list of objects
  */

public class LibManagerClient {
	
	public LibManagerClient(){
		terminalCommand.createFolder("librariesClasses/source");
	}

	//Library convert given JAR to source code
	public static String libJarToSourceExtractor=Paths.get(".").toAbsolutePath().normalize().toString() ;
	public static void main(String[] args) {
		
         new LibManagerClient().run();

	}
	TerminalCommand terminalCommand= new TerminalCommand();
	void run(){
		
		
		//jarToSourceCode("xxx:mysql-connector-java:5.1.23");
		ArrayList<MethodLib> listOfMethodLibs=  parseJarSource("xxx:slf4j-api:1.7.24");
		for (MethodLib methodLib : listOfMethodLibs) {
			//if(methodLib.fullName==methodLib.sourceCode){
				methodLib.print();
			//}
			
		}
		//listFileMethods("/Users/hussienalrubaye/Documents/workspace/FunMapping/librariesClasses/source/mysql-connector-java-5.1.23/com/mysql/jdbc/log/LogFactory.java");
	}
	
	public void jarToSourceCode(String LibraryInfo){
		
		String[] LibraryInfos =LibraryInfo.split(":");
		if(LibraryInfos.length<3){ 
			System.err.println(" Error in library name ("+ LibraryInfo+")");
			
			return;
	    }
		//String DgroupId=LibraryInfos[0];
		String DartifactId=LibraryInfos[1];
		String Dversion=LibraryInfos[2];
		String libraryName= DartifactId +"-"+ Dversion +".jar";
		
		String folderPathForSourceCode=libJarToSourceExtractor+"/librariesClasses/source/"+ libraryName.replace(".jar","" );
		File f = new File(folderPathForSourceCode);
		if (f.exists()) {
		   System.out.println("Good! Library source code already there :"+ folderPathForSourceCode); 
		   return;
		}
		 try{
			 System.out.println("==> generate source code for "+libraryName);
			 String cmdStr=" java -jar "+ libJarToSourceExtractor+"/lib/cfr_0_114.jar  "+ libJarToSourceExtractor+"/librariesClasses/jar/"+ libraryName+ "  --outputdir "+ folderPathForSourceCode;
				System.out.println("\nStart generate source code "+ cmdStr+ " library......" );
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				 p.waitFor();
				System.out.println("<== Process completed: ");
				//TODO: remove jar library and tfs files
			}catch (Exception e) {
				// TODO: handle exception
			}
	}

	String PackageName="";
	String ClassName="";
	//ArrayList<MethodLib> listOfMethodLibs= new ArrayList<>();
	public ArrayList<MethodLib>  parseJarSource(String LibraryInfo){
		
		ArrayList<MethodLib> listOfMethodLibs= new ArrayList<>();
		
		String[] LibraryInfos =LibraryInfo.split(":");
		if(LibraryInfos.length<3){ 
			System.err.println(" Error in library name ("+ LibraryInfo+")");
			
			return null;
	    }
		//String DgroupId=LibraryInfos[0];
		String DartifactId=LibraryInfos[1];
		String Dversion=LibraryInfos[2];
		String libraryName= DartifactId +"-"+ Dversion ;
		String sourcePath=libJarToSourceExtractor+"/librariesClasses/source/"+ libraryName;
		System.out.println(sourcePath);
		
	 	List<File> files = new ArrayList() ;
		new ParseHTML().listf(sourcePath, files,"java");
	    for (File file : files) {
		    	 PackageName=  file.getPath().substring(sourcePath.length()+1,file.getPath().length());
				if(PackageName.equals(file.getName())){
					//This isnot real documenation, it dosenot have packge
					continue;
				}
				// remove file name from package
				PackageName= PackageName.substring(0, PackageName.length()-(file.getName().length()+1));
				// convert / to .
				PackageName = PackageName.replaceAll("/", "."); // clean data
			
			    ClassName= file.getName();
				ClassName= ClassName.substring(0,ClassName.length()-5); //remove .html
//		        if(!ClassName.equals("IterateBlock")){
//		        	continue;
//		        }
				try{
					 
					 
					 CompilationUnit compilationUnit = StaticJavaParser.parse(readFileAsString(file.getPath()));
					 
					 
					 compilationUnit.findAll(MethodDeclaration.class).stream()
				        .forEach(f -> {
				            //System.out.println(file.getPath());
				    		//System.out.println(ClassName);
							//System.out.println(PackageName);
							//System.out.println(f.getDeclarationAsString());
							//System.out.println(f.toString());
				        	
				            //only consider methods with code
				            //if(f.toString().trim().endsWith("}") ){
				            	 listOfMethodLibs.add(new MethodLib(PackageName, ClassName,
				            			 f.getDeclarationAsString(), f.toString(),MethodObj.removeDot(f.getParentNode().get().getClass().toString()) ));
				           // }
				        	
			 
					         
				      } );
				}catch (Exception e) {
					//System.out.println("WARNING:"+e.getMessage());
				}
				  
				
	    }
		
	    
	    return listOfMethodLibs;
	}
	
	//Read file as String
	static public   String readFileAsString(String fileName) { 
		String text = "";
		try { 
			text = new String(Files.readAllBytes(Paths.get(fileName))); 
		} catch (IOException e) { e.printStackTrace(); } 
		return text;
   }
	
	 
}
