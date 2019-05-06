## MigrationMiner

A tool to detect migration code between two Java third-party library

## Prerequisite

* Ecplsie IDE for Java https://www.eclipse.org/downloads/packages/

* MYSQL  https://www.mysql.com/


## How ro run the Project
* First you need to build dataset, by running the folowing script Database/MigrationMinerDBSQL.sql.
Open a terminal and run the following commands
<pre>
 mysql -u root -p
 source ./MigrationMinerDBSQL.sql
</pre>
After running this code that database should be created with all tables and views.

* Create new  Java project at eclipse IDE and set the project directory to MigrationMiner folder.
* Set your MYSQL user name and password in this file "DatabaseLogin.java" that lives under MigrationMiner/src/com/project/settings/DatabaseLogin.java.

* Set your GitHub user name and password in this file "GithubLogin.java" that lives under MigrationMiner/src/com/project/settings/GithubLogin.java, So the tool can search a large number of GitHub projects without authentication issue .

* Update data/gitRepositories.csv with a list of git repositories that you want to scan and search for migration
* Run Main.java file that lives under MigrationMiner/src/com/main/parse/Main.java.

* Wait for while Then you will see database Tables filled with required fragments and migration info. Here is the tables schema
 
   * Repositories: Has list of projects that scanned by tool
   * AppCommits: Has list of projects' commits infomration( Commit Id, developer name, Commit text, and commit date)
   * ProjectLibraries: Has list of libraries that added or removed at every commit.
   * MigrationRules:  Has a set of migration Rules that generated from Dataset.
   * MigrationSegments: List Of migration Fragments that extract from software migration.
   * LibraryDocumenation: Has library documentation that associated with every library version that involved in migration.
   
   Also, there will be HTML file generate named "MigrationMinnerOutput.html" that has a summary of all migrations  detected and code fragments with Library documentation associated with fragment.
   
![main](https://repository-images.githubusercontent.com/185124992/bcd2f000-6f9d-11e9-9040-fbc3190eb01a)


## This Tool is used by the following papers
* Alrubaye, H., & Mkaouer, M. W. (2018, October). Automating the detection of third-party Java library migration at the function level. In Proceedings of the 28th Annual International Conference on Computer Science and Software Engineering (pp. 60-71). IBM Corp.
* Alrubaye, H., Mkaouer, & M. W., Ali, O (2019). On the Use of Information Retrieval to Automate the Detection of Third-Party Java Library Migration At The Function Level, 27th IEEE/ACM International Conference on Program Comprehension 2019.

 
