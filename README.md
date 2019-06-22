## MigrationMiner
MigrationMiner is an automated tool that written in Java to detects code migrations performed between two Java third-party library. Given a list of open source projects, the tool detects potential library migration code changes and collects the specific code fragments where developer replaces methods from the retired library with methods from the new library. Moreover, our tool collects the library documentation that is associated with every method involved in the migration
 
 Currently, it supports the detection of  any migration happen in project history that uses maven project architecture(has pom.xml) and Android project.

## Prerequisite

* Install Eclipse IDE for Java from, https://www.eclipse.org/downloads/packages/

* Install  MYSQL  from,  https://www.mysql.com/


## How to run the Project:
### To run the project on your local machine you could try one of these two options:

### A- Using Video
* You could run the project by following this video https://youtu.be/sAlR1HNetXc

### B- following steps
* First you need to build the dataset, by running the following script Database/MigrationMinerDBSQL.sql.
Open a terminal and run the following commands
<pre>
 mysql -u root -p
 source ./MigrationMinerDBSQL.sql
</pre>
After running this code that database should be created with all tables and views.

* Create new  Java project at eclipse IDE and set the project directory to MigrationMiner folder.
* Set your MYSQL user name and password in this file "DatabaseLogin.java" that lives under MigrationMiner/src/com/project/settings/DatabaseLogin.java.

* Set your GitHub user name and password in this file "GithubLogin.java" that lives under MigrationMiner/src/com/project/settings/GithubLogin.java, So the tool can search a large number of GitHub projects without authentication issue.

* Update MigrationMiner/data/gitRepositories.csv with a list of git repositories that you want to scan and search for migration
* Run Main.java file that lives under MigrationMiner/src/com/main/parse/Main.java.

* Wait for while Then you will see database Tables filled with required fragments and migration info. Here is the schema of the table
 
   * Repositories: Has a list of projects that scanned by the tool
   * AppCommits: Has list of projects' commits information( Commit Id, developer name, Commit text, and commit date)
   * ProjectLibraries: Has a list of libraries that added or removed at every commit.
   * MigrationRules:  Has a set of migration Rules that generated from Dataset.
   * MigrationSegments: List Of migration Fragments that extract from software migration.
   * LibraryDocumenation: Has library documentation that associated with every library version that involved in migration.
   
   Also, there will be HTML file generate named "MigrationMinnerOutput.html" that has a summary of all migrations detected and code fragments with Library documentation associated with fragment.
   
![main](https://repository-images.githubusercontent.com/185124992/bcd2f000-6f9d-11e9-9040-fbc3190eb01a)


## This Tool is used by the following papers
* Alrubaye, H., & Mkaouer, M. W. (2018, October). Automating the detection of third-party Java library migration at the function level. In Proceedings of the 28th Annual International Conference on Computer Science and Software Engineering (pp. 60-71). IBM Corp.
* Alrubaye, H., Mkaouer, & M. W., Ali, O (2019). On the Use of Information Retrieval to Automate the Detection of Third-Party Java Library Migration At The Function Level, 27th IEEE/ACM International Conference on Program Comprehension 2019.

 
