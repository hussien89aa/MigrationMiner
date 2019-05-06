package com.segments.build;

public class TerminalCommand {
	// This command for create folder
	public void createFolder(String folderPath){
			try{
				System.out.println("==> create folder : "+folderPath);
			    String cmdStr="mkdir -p " + folderPath   ;
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== folder is created");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
	 }
	 
	 // This function will create diff from two file
	public	void createDiffs(String oldFilePath,String newUpdatedFilePath ,String outputDiffFilePath){
			String cmdStr="diff "+ oldFilePath + " "+   newUpdatedFilePath+">"+ outputDiffFilePath;
			try{
				System.out.println("==> Generate Diff File: "+ outputDiffFilePath);
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== Generate done");

			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		//delete folder
		public	void deleteFolder(String folderPath){
			try{
				System.out.println("==> Start deleting ...");
			    String cmdStr=" rm -rf "+ folderPath +"";
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				System.out.println("<== Complete delete");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		//copy file
		public	void copyFile(String fromFilePath, String toFilePath){
			try{
				//System.out.println("==> Start coping ...");
			    String cmdStr=" cp "+ fromFilePath +" " + toFilePath;
			    //System.out.println(cmdStr);
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				//System.out.println("<== Complete Copy");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		//copy file
		public	void copyFolder(String fromFilePath, String toFilePath){
			try{
				//System.out.println("==> Start coping ...");
			    String cmdStr=" cp -r "+ fromFilePath +" " + toFilePath;
			    //System.out.println(cmdStr);
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				//System.out.println("<== Complete Copy");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		//copy file
		public	void moveFile(String fromFilePath, String toFilePath){
			try{
				 //System.out.println("==> Start coping ...");
			    String cmdStr=" mv "+ fromFilePath +" " + toFilePath;
			    //System.out.println(cmdStr);
				Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmdStr});
				p.waitFor();
				//System.out.println("<== Complete Copy");
				
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
}
