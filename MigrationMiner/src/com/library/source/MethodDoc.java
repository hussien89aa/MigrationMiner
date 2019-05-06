package com.library.source;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodDoc {
	public String fillName;
	public String description;
	public HashMap<String,String> parameters= new HashMap<String,String>();
	public MethodDoc(String fillName,String description) {
		this.fillName=fillName;
		this.description=description;
	}
	public void setDescription(String description){
		this.description=description;
	}
	
	public void addParameters(String paramName,String paramDescription){
		parameters.put(paramName, paramDescription);
	}

	public void print( ){
		System.out.println("fillName:"+ fillName);
		System.out.println("description:"+ description);
		for (String key : parameters.keySet()) {
			System.out.println(key +":"+ parameters.get(key));
		}
		System.out.println("-----------------------");
	}
}
