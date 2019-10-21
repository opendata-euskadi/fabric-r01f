INSTALL A PORTABLE JAVA IN WINDOWS
============================================================================

References: 

https://techtavern.wordpress.com/2014/03/25/portable-java-8-sdk-on-windows/

http://www.brucalipto.org/java/how-to-create-a-portable-jdk-1-dot-7-on-windows/

https://javacubicle.blogspot.com.es/2016/10/portable-java-8-jdk8-setup-without.html


# [1] Download the Java 8 SDK for Windows from Oracle. You will get a file named like java-8-windows-x64.exe.

# [2] Open the downloaded file with 7-Zip

* 2.1) If the file contents are: `/tools.zip` goto to step 3
		  
* 2.1) If the file contents are:
	 
	.data
	.pdata
	.rdata
	.reloc
	.rsrc
	.text
	CERTIFICATE 
			   
- a) Extract all files into a temp folder

- b) Go inside `[temp_folder]\.rsrc\1033\JAVA_CAB10`  ...there'll be a file named `111`

- c) Open the 111 file with 7-zip, it'll contain the `tools.zip` file
 
- d) goto to step 3
			   

# [3] Open the tools.zip with 7-Zip and extract its contents to a directory where own write permissions, for example: `d:\develop\java\java-8-sdk`

# [4] From within this directory, search for all `.pack` files and extract them into `.jar` files, using `unpack2000.exe` command line tool found in the bin subdirectory.
 
The following windows prompt command does the trick when executed from within the extracted directory: 

      for /r %i in (*.pack) do .\bin\unpack200.exe %i %~pi%~ni.jar

also works with
		
		for /r %i in (*.pack) do .\bin\unpack200 -r "%i" "%~di%~pi%~ni.jar"
		
               


