Node  Windows
====================================
https://docs.npmjs.com/downloading-and-installing-node-js-and-npm#using-a-node-version-manager-to-install-node-js-and-npm

# [1] - Install a node version manager: nvm

See: https://github.com/coreybutler/nvm-windows

Portable installation: https://github.com/coreybutler/nvm-windows/wiki

1. Download [nvm-noinstall.zip] from the relases page: https://github.com/coreybutler/nvm-windows/releases

2. Extract at `{develop-home}\node`

3. Create a file named `settings.txt` with the following content

	root: C:\develop\node\
	path: C:\develop\node\current
	arch: 64
	proxy: none

4. Create the following [windows] [system wariables]

	NVM_HOME={develop-home}\node
	NVM_SYMLINK={develop-home}\current		<-- this dir MUST NOT EXIST!!!
	
5. Update the system `%PATH%` [environment variable]: add `%NVM_HOME%;%NVM_SYMLINK%` at the END of the `%PATH%`

	BEWARE!!
		nvm will create a SYMLINK (windows mklink) that points to the CURRENT node.js version
		
# [2] - Install the latest `nodejs` version and activate it

	cmd>nvm install latest
	cmd>nvm list
			* 13.11.0 (Currently using 64-bit executable)
	cmd>use 13.11.0
			Now using node v13.11.0 (64-bit)
	cmd>node -v
			v13.11.0
	cmd>npm -v
			6.13.7
	

