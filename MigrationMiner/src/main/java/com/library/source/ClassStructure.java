package com.library.source;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ClassStructure {
	/*
	 * to find library sgenature first unzip library generate class schema by run
	 * this commands in library folder javap * ../libraryName.txt List jar classes
	 * jar -tf picasso-2.5.2-sources.jar | grep '.java'
	 */
	public ClassStructure() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<String> listOfLibraryClasses = new ClassStructure().getLibraryClasses("XXX:testng:6.9.13.6");
		System.out.println("*********\nlist Of Library Classes");
		for (String className : listOfLibraryClasses) {
			System.out.println(className);
		}

		// packgaes
		ArrayList<String> listOfLibraryPackages = new ClassStructure().getLibraryPackages("XXX:testng:6.9.13.6");
		System.out.println("*********\nlist Of Library Packages");
		for (String packageName : listOfLibraryPackages) {
			System.out.println(packageName);
		}

		// get classes and methods as objects
		ArrayList<ClassObj> listOfLibraryClassesObj = new ClassStructure().getLibraryClassesObj("XXX:testng:6.9.13.6"); // org.slf4j:slf4j-api:1.6.6
		System.out.println("*********\nlist Of Library  Classes Info");
		System.out.println("===========================");
		for (ClassObj classObj : listOfLibraryClassesObj) {
			classObj.print();
			System.out.println("===========================");
			for (MethodObj methodObj : classObj.classMethods) {

				methodObj.print();
			}

		}

		// static methods
		ArrayList<String> listOfStaticMethods = new ClassStructure().getStaticMethods("XXX:easymock:3.4");
		System.out.println("*********\nlist Of Static methods");
		for (String packageName : listOfStaticMethods) {
			System.out.println(packageName);
		}
	}

	// get list of class name for any input library depend on library sechman that
	// we already have
	public ArrayList<String> getLibraryClasses(String libraryInfo) {
		String[] LibraryInfos = libraryInfo.split(":");
		String DgroupId = LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];
		ArrayList<String> listOfClasses = new ArrayList<String>();
		String libraryPath = "librariesClasses/" + DartifactId + "-" + Dversion + ".jar.txt";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(libraryPath)));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.contains(" class ") || line.startsWith("class ") || line.contains(" interface ")
						|| line.startsWith("interface ")) {
					String searchFor = line.contains("class") == true ? "class" : "interface";
					String className = "";
					try {
						String[] classInfo = line.split(searchFor);
						String[] packgeWithClass = classInfo[1].trim().split(" ");
						String[] packageInfo = packgeWithClass[0].split("\\.");
						className = packageInfo[packageInfo.length - 1];
					} catch (Exception e) {
						// TODO: handle exception
					}

					if (className == "") {
						continue;
					}
					;

					if (listOfClasses.contains(className) == false) {
						// System.out.println(className);
						listOfClasses.add(className);
					}

				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return listOfClasses;
	}

	// get list of packges name for any input library depend on library sechman that
	// we already have
	public ArrayList<String> getLibraryPackages(String libraryInfo) {
		ArrayList<String> listOfPackages = new ArrayList<String>();
		String[] LibraryInfos = libraryInfo.split(":");
		String DgroupId = LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];
		String libraryPath = "librariesClasses/" + DartifactId + "-" + Dversion + ".jar.txt";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(libraryPath)));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.contains(" class ") || line.startsWith("class ") || line.contains(" interface ")
						|| line.startsWith("interface ")) {
					String searchFor = line.contains("class") == true ? "class" : "interface";
					String packageName = "";
					try {
						String[] classInfo = line.split(searchFor);
						String[] packgeWithClass = classInfo[1].trim().split(" ");
						String[] packageInfo = packgeWithClass[0].split("\\.");
						for (int i = 0; i < packageInfo.length - 1; i++) {
							String folder = packageInfo[i];
							if (packageName == "") {
								packageName = folder;
							} else {
								packageName += "." + folder;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					if (packageName == "") {
						continue;
					}
					;

					if (listOfPackages.contains(packageName) == false) {
						// System.out.println(className);
						listOfPackages.add(packageName);
					}

				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return listOfPackages;
	}

	// This function return list of static methods for direct call
	public ArrayList<String> getStaticMethods(String libraryInfo) {
		ArrayList<String> listOfStaticMethods = new ArrayList<String>();
		ArrayList<ClassObj> listOfClassesObj = getLibraryClassesObj(libraryInfo);
		for (ClassObj classObj : listOfClassesObj) {
			for (MethodObj methodObj : classObj.classMethods) {
				if (methodObj.scope.trim().contains(" static")) {

					listOfStaticMethods.add(methodObj.methodName);
				}
			}

		}

		return listOfStaticMethods;
	}

	/*
	 * This function return list of objects with all classes and methods that been
	 * // in the library
	 */
	public ArrayList<ClassObj> getLibraryClassesObj(String libraryInfo) {
		String[] LibraryInfos = libraryInfo.split(":");
		String DgroupId = LibraryInfos[0];
		String DartifactId = LibraryInfos[1];
		String Dversion = LibraryInfos[2];
		ArrayList<ClassObj> listOfClassesObj = new ArrayList<ClassObj>();
		String libraryPath = "librariesClasses/" + DartifactId + "-" + Dversion + ".jar.txt";
		ClassObj classObj = null;
		boolean readingMethods = false;// when this flag is active that mean the line is method
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(libraryPath)));
			String line;
			String searchFor = "Compiled from ";
			String className;
			while ((line = br.readLine()) != null) {

				if (line.trim().equals("}") && readingMethods == true) {
					readingMethods = false; // complete reading methods for this class
					if (classObj != null) {
						listOfClassesObj.add(classObj);
						classObj = null;
					}
				}
				if (readingMethods == true) {
					if (classObj != null) {
						classObj.addMethod(line);
					}
				}
				// new class to process
				if (line.contains("class ") || line.contains("interface ")) {
					classObj = new ClassObj();
					classObj.setClassName(line);
					// listOfClasses.add(className);
					readingMethods = true;
				}

			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return listOfClassesObj;
	}

}
