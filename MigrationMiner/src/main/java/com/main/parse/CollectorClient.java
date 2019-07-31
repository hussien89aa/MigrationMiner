package com.main.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
//JDON.jar used to parse XML
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.database.mysql.ProjectLibrariesDB;
import com.database.mysql.RepositoriesDB;
import com.project.info.Operation;
import com.project.settings.AppSettings;
import com.project.settings.ProjectBuildFile;
import com.project.settings.ProjectType;
import com.segments.build.TerminalCommand;
import com.subversions.process.Commit;
import com.subversions.process.CommitFiles;
import com.subversions.process.GitRepositoryManager;
import com.subversions.process.GitHubOP;

/**
 * This file responsible for collect all added and removed libraries in all
 * commits for list of github project
 *
 */
public class CollectorClient {
	String LOG_FILE_NAME = "app_commits.txt";
	static String pathClone = Paths.get(".").toAbsolutePath().normalize().toString() + "/Clone/";
	TerminalCommand terminalCommand = new TerminalCommand();

	public static void main(String[] args) {

		new CollectorClient().startOnlineSearch();

	}

	// Search online for library migration
	void startOnlineSearch() {

		// Created needed folder
		terminalCommand.createFolder("Clone");

		// TODO: Search online for library migration
		GitRepositoryManager findGoodRepositories = new GitRepositoryManager();
		ArrayList<String> listOfGitRepositories = findGoodRepositories.getGitLinks("data/gitRepositories.csv");
		int count = 1;
		RepositoriesDB repositoriesDB = new RepositoriesDB();

		for (String appURL : listOfGitRepositories) {
			// get app libraries in every commit
			int projectID = repositoriesDB.getRepositoryID(appURL);
			if (projectID != 0) {
				continue; // TODO: return this and three more placess
			}
			// get app folder name

			String appFolder = GitHubOP.getAppName(appURL);
			if (appFolder == "") {
				continue;
			}

			String appPath = pathClone + appFolder;

			GitHubOP gitHubOP = new GitHubOP(appURL, pathClone);
			// String appLink=gitHubOP.addGitUserLogin(appURL);
			System.out.println("Process app(" + count + "-" + listOfGitRepositories.size() + ") " + appURL);
			count++;

			// if (gitHubOP.isPageExisit(appURL+"/blob/master/pom.xml")){
			gitHubOP.cloneApp();

			gitHubOP.generateLogs(LOG_FILE_NAME);
			// save commit info in database
			ArrayList<Commit> commitList = gitHubOP.readLogFile(pathClone + "app_commits.txt",
					ProjectBuildFile.getType());

			repositoriesDB.addNewProject(appURL, "Java", commitList);
			// save this link in clean repo links
			findGoodRepositories.saveCleanLinks(gitHubOP.appURL, "JavaGitLinksClean.txt");

			// get app libraries in every commit
			projectID = repositoriesDB.getRepositoryID(appURL);
			// list of paths and library name for prevous commit
			HashMap<String, String> previousVersionLibraries = new HashMap<String, String>();
			for (int i = 0; i < commitList.size(); i++) {

				// only checkout when there is change in pom.xml
				// That mean we checkout only when developer add or remove library
				Commit commitInfo = commitList.get(i);
				if (commitInfo.filePath.size() == 0) {
					continue;
				}
				System.out.println("***************");
				// commitID has (v12_commitID) we need to get only commit id
				String[] spCommitIDInfo = commitInfo.commitID.split("_");
				String commitID = spCommitIDInfo[1];
				gitHubOP.gitCheckout(gitHubOP.appFolder, commitID);

				/*
				 * we use commitInfo.filePath instead this ArrayList<String> listOfPaths=
				 * pomFilePath.findPomFilePath(pathClone, appFolder); if(listOfPaths.size()==0){
				 * System.out.println("Cannot find pom file in this commit"); continue; }
				 */

				// GET ALL LIRBARIES THAT ASSOICATE WILL EVERY PROJECT IN REPO
				for (CommitFiles pomPath : commitInfo.filePath) {
					// make sure all commits has init library
					if (previousVersionLibraries.get(pomPath.firstFile) == null) {
						previousVersionLibraries.put(pomPath.firstFile, "");
					}
					String prevCommitLibraries = generateAppLibraries(appFolder + "/" + pomPath.firstFile, projectID,
							commitInfo.commitID, previousVersionLibraries.get(pomPath.firstFile));
					previousVersionLibraries.put(pomPath.firstFile, prevCommitLibraries);
					System.out.println(pomPath.firstFile + "==>" + previousVersionLibraries);
				}

			}

			// break;//TODO: will be removed
			// }
			gitHubOP.deleteFolder(appPath);
			gitHubOP.deleteFolder(pathClone + LOG_FILE_NAME);
			System.out.println("--------------------------------------");
			System.out.println("**************************************");
			System.out.println("--------------------------------------");
			System.out.flush();
		}
	}

	String generateAppLibraries(String pomPath, int projectID, String commitID, String previousVersionLibraries) {
		try {
			String versionLibraries;
			String projectPath = pathClone + pomPath;
			if (AppSettings.projectType == ProjectType.Android) {
				versionLibraries = listOfAndroidProjectLibrary(projectPath);
			} else { // for java
				versionLibraries = listOfJavaProjectLibrary(projectPath);
			}
			// TODO to parse or languages make sure to add the language here

			// if(versionLibraries==""){ return "";}//ignore

			// System.out.println(projectPath +"\t"+ versionLibraries);
			// search for migration
			ArrayList<String> listOfRemovedLibrary = listOfChangedLibrary(previousVersionLibraries, versionLibraries);
			if (listOfRemovedLibrary.size() > 0) {
				for (String libraryName : listOfRemovedLibrary) {
					System.err.println("Removed:" + libraryName);
					new ProjectLibrariesDB().addProjectLibrary(projectID, commitID, libraryName, Operation.removed,
							pomPath);
				}
			}
			ArrayList<String> listOfAddedLibrary = listOfChangedLibrary(versionLibraries, previousVersionLibraries);
			if (listOfAddedLibrary.size() > 0) {
				for (String libraryName : listOfAddedLibrary) {
					System.err.println("Added:" + libraryName);
					new ProjectLibrariesDB().addProjectLibrary(projectID, commitID, libraryName, Operation.added,
							pomPath);
				}
			}

			previousVersionLibraries = versionLibraries;
		} catch (Exception e) {
			// do something
		}
		return previousVersionLibraries;
	}

	public ArrayList<String> listOfChangedLibrary(String previousVersionLibraries, String versionLibraries) {
		ArrayList<String> listOfLibrary = new ArrayList<String>();
		if (previousVersionLibraries.length() == 0 || versionLibraries.length() == 0) {
			// get all library that added in first commit
			if (previousVersionLibraries.length() != 0 && versionLibraries.length() == 0) {
				String[] addedLibraries = previousVersionLibraries.split(",");
				for (String addedLibraryInstance : addedLibraries) {
					listOfLibrary.add(addedLibraryInstance);
				}
			}

			return listOfLibrary;
		}

		if (previousVersionLibraries != versionLibraries) {
			String[] prev = previousVersionLibraries.split(",");
			String[] current = versionLibraries.split(",");
			for (String prevLibrary : prev) {
				if (prevLibrary.length() == 0) {
					continue;
				}
				;
				boolean isFound = false;
				for (String currentLibrary : current) {
					if (prevLibrary.equals(currentLibrary)) {
						isFound = true;
						break;
					}
				}
				if (isFound == false) {

					listOfLibrary.add(prevLibrary);
				}
			}

		}
		return listOfLibrary;
	}

	// read Android app build.gradle file
	public String listOfAndroidProjectLibrary(String projectVersionPath) {

		// String appProjectPath= projectVersionPath + "/app/build.gradle";
		String versionLibraries = "";
		System.out.println("Search for  library at :" + projectVersionPath);
		try {
			// System.out.println("Path:"+ projectPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(projectVersionPath)));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().startsWith("compile ") || line.trim().startsWith("implementation ")
						|| line.trim().startsWith("testCompile ") || line.trim().startsWith("testImplementation ")
						|| line.trim().startsWith("androidTestImplementation ")) {
					String[] libraryPackage = line.split("'");
					if (libraryPackage.length < 2) {
						continue;
					}
					if (libraryPackage[1].trim().split(":").length != 3) {
						continue;
					} // Make sure we have library in format of GorpuId:LibraryName:Version
					if (versionLibraries.length() == 0) {
						versionLibraries = libraryPackage[1];
					} else {
						if (versionLibraries.contains(libraryPackage[1]) == false) {
							versionLibraries = versionLibraries + "," + libraryPackage[1];
						}
					}
					// new Project().addProjectLibrary(pName, version, libraryPackage[1]);
				}
			}

		} catch (IOException ex) {

		}
//	    // delete folder if isnot real app folder this missing gradle folder
//	    if(versionLibraries=="" && projectVersionPath.length()>0){
// 
//			try{
//				String cmdStr=" rm -rf "+ projectVersionPath;
//				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
//				p.waitFor();
//				System.err.println("\n Clean and delete folder: "+projectVersionPath +", because isnot good App folder");
//			}catch (Exception e) {
//				// TODO: handle exception
//			}
//	    }

		System.out.println("Found libraries-> " + versionLibraries);
		return versionLibraries;
	}

	// read Android Java pom.xml file
	public String listOfJavaProjectLibrary(String projectVersionPath) {

		// String appProjectPath= projectVersionPath + "/pom.xml";
		if (projectVersionPath.length() == 0) {
			System.err.println("project doensot have pom.xml file");
			return "";
		}
		String versionLibraries = "";
		System.out.println("Search for  library at :" + projectVersionPath);

		try {
			File inputFile = new File(projectVersionPath);
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);

			Element root = document.getRootElement();

			// get public properties for library version
			HashMap<String, String> propertiesList = new HashMap<String, String>();
			try {
				Element properties = getchild(root, "properties");
				List<Element> propertiesListNode = properties.getChildren();

				for (int temp = 0; temp < propertiesListNode.size(); temp++) {
					Element property = propertiesListNode.get(temp);
					propertiesList.put("${" + property.getName() + "}", property.getValue());

				}
			} catch (Exception ex) {
			}

			/*
			 * DEBUG for (String element : propertiesList.keySet()) {
			 * System.out.println(element +":"+ propertiesList.get(element)); }
			 */

			// get library info
			Element dependencyManagement = getchild(root, "dependencyManagement");
			Element dependencies = null;
			if (dependencyManagement != null) {
				dependencies = getchild(dependencyManagement, "dependencies");
			} else {
				// dependencies may lives under root
				dependencies = getchild(root, "dependencies");
			}
			List<Element> dependencytList = dependencies.getChildren();
			// System.out.println("----------------------------");

			for (int temp = 0; temp < dependencytList.size(); temp++) {
				Element dependency = dependencytList.get(temp);
				List<Element> librariesList = dependency.getChildren();
				String groupId = "";
				String artifactId = "";
				String version = "";
				for (int temp1 = 0; temp1 < librariesList.size(); temp1++) {
					Element libraryInfo = librariesList.get(temp1);
					if (libraryInfo.getName().equals("groupId"))
						groupId = libraryInfo.getValue();
					if (libraryInfo.getName().equals("artifactId"))
						artifactId = libraryInfo.getValue();
					if (libraryInfo.getName().equals("version")) {
						version = libraryInfo.getValue();
						if (version.startsWith("${")) {
							version = propertiesList.get(version);
						}
					}
				}
				String libraryLink = groupId + ":" + artifactId + ":" + version;

				if (versionLibraries.length() == 0) {
					versionLibraries = libraryLink;
				} else {
					if (versionLibraries.contains(libraryLink) == false) {
						versionLibraries = versionLibraries + "," + libraryLink;
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Found libraries-> " + versionLibraries);
		return versionLibraries;
	}

	Element getchild(Element classElement, String name) {
		try {
			List<Element> studentList = classElement.getChildren();
			for (int temp = 0; temp < studentList.size(); temp++) {
				Element element = studentList.get(temp);
				if (element.getName().equals(name)) {
					return element;
				}
			}
		} catch (Exception ex) {
			System.out.println("No child found under:" + name);
		}
		return null;
	}

	// read properties
	public String readProperty(String propertyName, String appProjectPath) {

		String versionLibraries = "";
		System.out.println("Search for  library version :" + appProjectPath);
		try {
			// System.out.println("Path:"+ projectPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(appProjectPath)));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("<" + propertyName + ">") && line.contains("</" + propertyName + ">")) {
					String[] versionValue = line.split(">");
					String[] versionValue1 = versionValue[1].split("<");
					versionLibraries = versionValue1[0];
					break;
				}
			}

		} catch (IOException ex) {

		}

		return versionLibraries;
	}

}
