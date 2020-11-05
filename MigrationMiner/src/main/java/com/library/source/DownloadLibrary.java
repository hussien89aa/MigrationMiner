package com.library.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import com.subversions.process.GitRepositoryManager;

public class DownloadLibrary {
	// TODO need to update when machine chnage
	static String pathlib;

	public DownloadLibrary(String pathToSaveJar) {
		this.pathlib = Paths.get(".").toAbsolutePath().normalize().toString() + pathToSaveJar;
	}

	// static String
	// pathClassSignature=Paths.get(".").toAbsolutePath().normalize().toString()
	// +"/LibrariesDocs"; // +"/librariesClasses/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String libraryInfo = "org.mockito:mockito-all:1.8.4";
		DownloadLibrary downloadLibrary = new DownloadLibrary("/LibrariesDocs/jar");
		downloadLibrary.download(libraryInfo, false);
		downloadLibrary.buildTFfiles(libraryInfo);
		// System.out.println( "Is library found:"+ new
		// DownloadLibrary().isLibraryFound(libraryInfo));
		downloadLibrary.generateFunctionSignature("mockito-all-1.8.4.jar", pathlib + "/../");
	}

	public boolean isLibraryFound(String LibraryInfo) {
		boolean isFound = false;
		String[] LibraryInfos = LibraryInfo.split(":");
		if (LibraryInfos.length < 3) {
			return isFound;
		}
		// String DgroupId=LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];
		String jarFilePath = pathlib + "/" + DartifactId + "-" + Dversion + ".jar";
		// System.out.println(jarFilePath);
		File tmpDir = new File(jarFilePath);
		isFound = tmpDir.exists();
		return isFound;
	}

	// Delete files that donseot have signatures
	public void isValidLibraryToGenerateSigantures(String LibraryName) {
		boolean isFound = false;
		String tfFilePath = pathlib + "/tfs/" + LibraryName.replace(".jar", ".jar.txt");
		String jarFilePath = pathlib + "/" + LibraryName;
		File tmpDir = new File(jarFilePath);
		isFound = tmpDir.exists();
		if (isFound == true) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tfFilePath)));
				String line;
				boolean hasCode = false;
				while ((line = br.readLine()) != null) {
					hasCode = true;
					break;
				}
				br.close();
				if (hasCode == false) {
					deleteFile(jarFilePath);
					deleteFile(tfFilePath);
				} else {
					// it valid library generate the function signature
					generateFunctionSignature(LibraryName, pathlib + "/../");
				}
				br.close();
			} catch (Exception ex) {
			}
		}

	}

	public void deleteFile(String path) {
		try {
			File tmpLibDir = new File(path);
			if (tmpLibDir.exists()) {
				tmpLibDir.delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Generate function Signature from code
	public void generateFunctionSignature(String libraryName, String pathToSaveLibrarySignature) {

		// Delete file if it exisit
		deleteFile(pathToSaveLibrarySignature + libraryName + ".txt");

		try {
			System.out.println("==> generate function Signature for " + libraryName);
			String cmdStr = "cd " + pathToSaveLibrarySignature + " && javap -classpath jar/" + libraryName
					+ " $(jar -tf jar/" + libraryName + " | grep \"class$\" | sed s/\\.class$//) >>" + libraryName
					+ ".txt";
			System.out.println("\nStart generate Function Signature " + cmdStr + " library......");
			Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", cmdStr });
			if(!p.waitFor(5, TimeUnit.MINUTES)) {
			    //timeout - kill the process. 
			    p.destroy(); 
			}
			System.out.println("<== Process completed: ");
			// TODO: remove jar library and tfs files
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Download Library from URL
	public void download(String LibraryInfo, boolean isDocs) {
		// we already have the library
		if (isLibraryFound(LibraryInfo)) {
			System.out.println(" Good! library signature (" + LibraryInfo + ") already there donot need to download");
			return;
		}
		String[] LibraryInfos = LibraryInfo.split(":");
		if (LibraryInfos.length < 3) {
			System.err.println(" Error in library name (" + LibraryInfo + ")");

			return;
		}
		String DgroupId = LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];

		String cmdStr="cd "+pathlib+" && curl -L -O http://search.maven.org/remotecontent?filepath="+DgroupId.replace(".", "/") +"/"+DartifactId +"/"+ Dversion+ "/"+ DartifactId+"-"+ Dversion+(isDocs?"-javadoc":"")+".jar";
		//This Maven  repo depreacted
		//String cmdStr = "cd " + pathlib + " &&  curl -L -O http://central.maven.org/maven2/" + DgroupId.replace(".", "/") + "/" + DartifactId + "/" + Dversion + "/" + DartifactId + "-" + Dversion + (isDocs ? "-javadoc" : "") + ".jar";
		
		System.out.println(cmdStr);
		try {
			System.out.println("==> Start Download " + DartifactId + " library......");
			Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", cmdStr });
			if(!p.waitFor(5, TimeUnit.MINUTES)) {
			    //timeout - kill the process. 
			    p.destroy(); 
			}
			System.out.println("<== Download completed: ");
			// buildTFfiles(LibraryInfo,pathToSaveLibrary);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Convert java.jar to source code so we could get signatures
	public void buildTFfiles(String LibraryInfo) {
		String[] LibraryInfos = LibraryInfo.split(":");
		if (LibraryInfos.length < 3) {
			System.err.println(" Error in library name (" + LibraryInfo + ")");
			return;
		}
		// String DgroupId=LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];
		String libraryName = DartifactId + "-" + Dversion + ".jar";
		try {
			String cmdStr = "jar -tf " + pathlib + "/" + libraryName + ">>" + pathlib + "/tfs/" + libraryName + ".txt";
			System.out.println("==> generate tfs....");
			Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", cmdStr });
			if(!p.waitFor(5, TimeUnit.MINUTES)) {
			    //timeout - kill the process. 
			    p.destroy(); 
			}
			System.out.println("<== Process completed: ");
			isValidLibraryToGenerateSigantures(libraryName);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
