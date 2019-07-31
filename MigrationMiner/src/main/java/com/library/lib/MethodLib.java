package com.library.lib;

import com.library.source.MethodObj;

public class MethodLib {
	public String fullName;
	public String sourceCode;
	public String ClassName;
	public String ClassType;
	public String PackageName;
	public MethodObj methodObj;

	public MethodLib(String PackageName, String ClassName, String fullName, String sourceCode, String ClassType) {
		this.PackageName = PackageName;
		this.ClassName = ClassName;
		this.fullName = fullName;
		this.sourceCode = sourceCode;
		this.methodObj = MethodObj.GenerateSignature(fullName.trim());
		this.ClassType = ClassType;
	}

	void print() {
		System.out.println("PackageName: " + this.PackageName);
		System.out.println("ClassName: " + this.ClassName);
		System.out.println("ClassType: " + this.ClassType);
		System.out.println("fullName: " + this.fullName);
		System.out.println("sourceCode: \n" + this.sourceCode);
		System.out.println("=====================");
	}
}
