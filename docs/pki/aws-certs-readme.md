[1] - Using a [web browser], download the AWS certificates from: https://www.amazontrust.com/repository/
=======================================================================================================

		CN=Amazon Root CA 1,O=Amazon,C=US
		CN=Amazon Root CA 2,O=Amazon,C=US
		CN=Amazon Root CA 3,O=Amazon,C=US
		CN=Amazon Root CA 4,O=Amazon,C=US
		CN=Starfield Services Root Certificate Authority – G2,O=Starfield Technologies, Inc.,L=Scottsdale,ST=Arizona,C=US
		Starfield Class 2 Certification Authority

[2] - Open a system console
===========================
a) goto to the [jdk] bin directory

		$> cd {jdk_root}/bin
		
b) run the [keytool]

If JRE <= 8

	$>keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file {develop_root}\projects\fabric\r01f\docs\pki\aws\{cert}.cer -alias {cert-alias} -storepass changeit

If JRE > 8

	$>keytool -keystore {jdk_root}\lib\security\cacerts -import -file {develop_root}\projects\fabric\r01f\docs\pki\aws\{cert}.cer -alias {cert-alias} -storepass changeit



IE:

	keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file c:\develop\projects\fabric\r01f\docs\pki\aws\AmazonRootCA1.cer -alias AmazonRootCA1 -storepass changeit
	keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file c:\develop\projects\fabric\r01f\docs\pki\aws\AmazonRootCA2.cer -alias AmazonRootCA2 -storepass changeit
	keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file c:\develop\projects\fabric\r01f\docs\pki\aws\AmazonRootCA3.cer -alias AmazonRootCA3 -storepass changeit
	keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file c:\develop\projects\fabric\r01f\docs\pki\aws\AmazonRootCA4.cer -alias AmazonRootCA4 -storepass changeit
	
	keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file c:\develop\projects\fabric\r01f\docs\pki\aws\SFSRootCAG2.cer -alias SFSRootCAG2 -storepass changeit

