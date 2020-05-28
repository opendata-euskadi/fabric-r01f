[1] - Using a [web browser], download the certificates
=======================================================================================================
A) AWS: https://www.amazontrust.com/repository/
		
B) Google: https://pki.goog/



[2] - Open a system console
===========================
a) goto to the [jdk] bin directory

		$> cd {jdk_root}/bin
		
b) run the [keytool]

If JRE <= 8

	$>keytool -keystore {jdk_root}\jre\lib\security\cacerts -import -file {develop_root}\projects\fabric\r01f\docs\pki\{certs}\{cert}.cer -alias {cert-alias} -storepass changeit

If JRE > 8

	$>keytool -keystore {jdk_root}\lib\security\cacerts -import -file {develop_root}\projects\fabric\r01f\docs\pki\{certs}\{cert}.cer -alias {cert-alias} -storepass changeit





