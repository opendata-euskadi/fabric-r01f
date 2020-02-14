(see http://blog.madhukaraphatak.com/interactive-scheduling-using-azkaban-setting-up-solo-server/)

1.	Follow getting started guide at: https://azkaban.readthedocs.io/en/latest/getStarted.html

	NOTE: when launching gradle, DO NOT run TESTs (-x test  option)

2.	The product is compiled and a ZIP is generated at: build/distributions/azkaban-solo-server-3.0.0.zip

3.	Unzip the generated file at: /develop/azkaban_server

4.	To use MySql instead of the H2 default db change `conf/azkaban.properties`

Comment:

	#database.type=h2
	#h2.path=./h2
	#h2.create.tables=true

Add:

	database.type=mysql
	mysql.port=3306
	mysql.host=127.0.0.1
	mysql.database=pci
	mysql.user=pci
	mysql.password=pci
	mysql.numconnections=100

5.	Start the server

	cd /develop/azkaban_server
	start-solo.sh

*BEWARE!!!* Launch from `/develop/azkaban_server` *NEVER* from `/develop/azkaban_server/bin`

6.	Upload a ZIP file with the DAG definition as the one attached


WINDOWS INSTALL
===================
INSTALL Azkaban
================================
https://azkaban.github.io/azkaban/docs/latest/
https://azkaban.github.io/azkaban/docs/latest/#solo-setup

Follow these steps to get started:

	[1] - Install java (jre 8):
		$ java
			Command 'java' not found, but can be installed with:

			sudo apt install default-jre
			sudo apt install openjdk-11-jre-headless
			sudo apt install openjdk-8-jre-headless

		$ sudo apt-get update
		$ sudo apt install openjdk-8-jre-headless
		$ java -version


	[2] - Clone the repo:
		$ git clone https://github.com/azkaban/azkaban.git

	[3] - Build Azkaban and create an installation:
		$ cd /home/ubuntu/azkaban/
		$ ./gradlew build installDist -x test

	[4] - Start the server:
		$ cd azkaban-solo-server/build/install/azkaban-solo-server
		$ sh ./bin/start-solo.sh

	[5] Test server
		Open Navigator and access to http://localhost:8081/ user:azkaban pwd:azkaban

		OR
		& ps -feaH | grep azkaban

		OR see log file at C:\store\ubuntu\ubuntu1804\Ubuntu\rootfs\home\ubuntu\azkaban\azkaban-solo-server\build\install\azkaban-solo-server\soloServerLog__2020-02-13+17#003A26#003A32.out


	[6] - Stop server:
		$ sh ./bin/shutdown-solo.sh

