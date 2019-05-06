package com.main.parse;

public class Main {

	public static void main(String[] args) {
 
		 //1 Collection
		 new CollectorClient().startOnlineSearch();
		 //2- Find migration rule
		 new MigrationRulesClient().start();
		 //3- Find code segments
		 new DetectorClient().start();
		 //4-  Collect Docs
		 new DocManagerClient().run();
		 
		 //5- Print results as HTML and save in DB
	     new FragmentDocsMapperClient().run();
		 
			
			
	}

}
