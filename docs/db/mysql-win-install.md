# MySQL install
=======================================

The following instructions are for installing MySQL Database and MySQL Workbenchinstance

The idea is:

1 - Install MySQL as a service

2- Install MySQL Workbench

## [1]: Install MySQL Community Server

a) Download the [MySQL Community Server] from https://dev.mysql.com/downloads/mysql/

> BEWARE!! Download the [Windows (x86, 64-bit), ZIP Archive]

b) Extract the contents of the [MySQL Community Server] archive to `/{dev_home}/db-server`

c) Create a directory for MySQL's database's data files at `/{dev_home}/db-server/mydb`

d) Create a directory for MySQL's database logging at `/{dev_home}/db-server/logs`

e) Create a MySQL options file at `/{dev_home}/db-server/config.ini` (see config.ini file)

## [2]: Initialize MySQL database

> This will create a database files in the location specified in the configuration file.
> It will have root user with no password
> Error messages will be printed on current console window.

a) Open CMD window:

	c:\develop\db-server\mysql-8.0.19-winx64\bin\mysqld.exe --defaults-file="c:\\develop\\db-server\\mysql-8.0.19-winx64\\config.ini" --initialize-insecure --console

b) Create a batch file to start the MySQL database server:

	c:\develop\db-server\mysql-8.0.19-winx64\bin\mysqld.exe --defaults-file="c:\\develop\\db-server\\mysql-8.0.19-winx64\\config.ini"
	
c) Create a batch file to shutdown the MySQL database server

	c:\develop\db-server\mysql-8.0.19-winx64\bin\mysqladmin.exe --defaults-file="c:\\develop\\db-server\\mysql-8.0.19-winx64\\config.ini" shutdown

## [3]: Install MySQL as a service
	
a) Open CMD window:

	c:\develop\db-server\mysql-8.0.19-winx64\bin\mysqld --install pci-mysql --defaults-file=c:\develop\db-server\mysql-8.0.19-winx64\config.ini

## [4]: Install MySQL Workbench

a) Download the [MySQL Workbench] from https://dev.mysql.com/downloads/workbench/

> BEWARE!! Download the [Windows (x86, 64-bit), MSI Installer]

b) Open CMD window and execute the [MySQL Workbench] file as Admin

c) Open the [MySQL Workbench] and create configuration using the 3306 Port