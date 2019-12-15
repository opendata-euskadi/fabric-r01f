Install Google CERTs on OpenJDK
============================================================================

OpenJDK DOES NOT comes with google certs; in order to install them:

1) Goto https://pki.goog/ and download the [Root CAs]

2) Import them to the JDK using the [keytool]

If [JDK <= 8]: 

	keytool -keystore {dev_home}\java\{jdk}\jre\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR1.crt -alias Google_GGTS_Root_R1 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\jre\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR2.crt -alias Google_GGTS_Root_R2 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\jre\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR3.crt -alias Google_GGTS_Root_R3 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\jre\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GSR2.crt -alias Google_GSR_Root_R2 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\jre\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GSR4.crt -alias Google_GSR_Root_R4 -storepass changeit -noprompt


If [JDK >= 8]

	keytool -keystore {dev_home}\java\{jdk}\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR1.crt -alias Google_GGTS_Root_R1 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR2.crt -alias Google_GGTS_Root_R2 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR3.crt -alias Google_GGTS_Root_R3 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR2.crt -alias Google_GSR_Root_R2 -storepass changeit -noprompt
	keytool -keystore {dev_home}\java\{jdk}\lib\security\cacerts -import -file {dev_home}\projects\fabric\r01f\docs\pki\certs\GTSR4.crt -alias Google_GSR_Root_R4 -storepass changeit -noprompt



