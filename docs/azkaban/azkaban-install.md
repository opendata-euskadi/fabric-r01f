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
