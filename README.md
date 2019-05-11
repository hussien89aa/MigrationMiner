## MigrationMiner

A tool to detect migration code between two Java third-party library. As a summary, Modern software systems rely heavily on third-party library functions as a mean to save time, reduce implementation costs, and increase their software quality when offering rich, robust and up-to-date features. However, as software systems evolve frequently, the need for better services and more secure, reliable and quality functionalities causes developers to often replace their old libraries with more recent ones. This process of replacing a library with a different one, while preserving the same functionality, is known as library migration. Learn more about evolution migration that happened over a year of development. We track library migration in around 321k open source projects 

## Prerequisite

* Install Eclipse IDE for Java from, https://www.eclipse.org/downloads/packages/

* Install  MYSQL  from,  https://www.mysql.com/


## How to run the Project
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

 
