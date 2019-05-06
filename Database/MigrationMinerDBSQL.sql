drop database MigrationMiner;
create database MigrationMiner;
use  MigrationMiner;

CREATE TABLE  Repositories (
	 AppID 	INTEGER PRIMARY KEY AUTO_INCREMENT,
	 AppLink 	varchar(200),
	 AppType 	varchar(10)
);
describe Repositories; 

 
 
CREATE TABLE  ProjectLibraries  (
	 ProjectsID 	INTEGER,
	 CommitID 	varchar(200),
	 LibraryName 	varchar(200),
	 isAdded	INTEGER,
	 PomPath    varchar(200)
);
describe ProjectLibraries; 

 
CREATE TABLE  MigrationRules  (
	 ID 	INTEGER PRIMARY KEY AUTO_INCREMENT,
	 FromLibrary 	varchar(200),
	 ToLibrary 	varchar(200),
	 Frequency INTEGER,
	 Accuracy REAL,
	 isVaild INTEGER DEFAULT 0
);
describe MigrationRules; 

 
 

 
create table MigrationSegments
(
  MigrationRuleID int          null,
  AppID           int          null,
  CommitID        varchar(200) null,
  FromCode        text         null,
  ToCode          text         null,
  fileName          text         null
);
describe MigrationSegments; 

 

 
CREATE TABLE  AppCommits  (
	 AppID 	INTEGER,
	 CommitID 	varchar(200),
	 CommitDate 	datetime,
	 DeveloperName 	varchar(100),
	 CommitText 	TEXT
);
describe AppCommits; 
 

 

CREATE TABLE  LibraryDocumentation  (
	 LibraryName 	varchar(100),
	 PackageName varchar(100),
	 ClassName 	varchar(100),
	 MethodFullName	text,
	 MethodDescription 	text,
	 MethodParams 	text,
	 MethodReturn 	text

);
describe LibraryDocumentation; 
 

 

 CREATE VIEW ProjectLibrariesView
  as
SELECT ProjectsID,CommitID,
   LibraryName, isAdded,PomPath
from (
select ProjectsID,CommitID,
   LibraryName, isAdded, PomPath,
CONVERT(substring(SUBSTRING_INDEX(CommitID, '_', 1),2),UNSIGNED INTEGER) AS orderCommit
from ProjectLibraries) as commits
ORDER BY ProjectsID,PomPath,orderCommit;

CREATE VIEW MigrationProjectLibrariesView
  as
select  ProjectsID,
        ProjectLibraries.CommitID,
        MigrationRuleID,
       SUBSTRING_INDEX(SUBSTRING_INDEX(FromLibrary, ':', -2), ':', 1)  as FromLibrary,
       SUBSTRING_INDEX(SUBSTRING_INDEX(ToLibrary, ':', -2), ':', 1)  as ToLibrary,
       LibraryName, FromCode,ToCode
from ProjectLibraries
inner join MigrationSegments
on MigrationSegments.AppID= ProjectLibraries.ProjectsID and
   MigrationSegments.CommitID= ProjectLibraries.CommitID
inner join MigrationRules
on MigrationSegments.MigrationRuleID= MigrationRules.ID;
 
