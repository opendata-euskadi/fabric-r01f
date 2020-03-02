Windows Linux SubSystem
======================================
PRE: INSTALL POWERSHELL with .net CORE
======================================
Install .net CORE SDK (not the runtime): https://dotnet.microsoft.com/download
	Check the installation: https://docs.microsoft.com/en-us/dotnet/core/
		cmd> dotnet new console

Install PowerShell CORE:
	https://aka.ms/pscore6
	https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-core-on-windows?view=powershell-7

	cmd> dotnet tool install --global PowerShell

	Update powershell to use [powershell core]: https://github.com/PowerShell/PowerShell/releases


INSTALL WINDOWS LINUX SUBSYSTEM
================================
https://adamtheautomator.com/windows-subsystem-for-linux/#updating-to-windows-subsystem-for-linux-2
[1] - Install Windows SubSystem for Linux
			PowerShell > Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux


====> NOT FOUND and not needed [2] - Update to Windows Subsystem for Linux 2:
										PowerShell > Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform


[3]
-   1) Option 1:

			In [windows store] app
				Search for wsl or wsl ubuntu
				Intall
				Launch

======>	2) Option 2:

			Important!!!   See this !!  " Install WSL Manually on NON System Drive"  https://damsteen.nl/blog/2018/08/29/installing-wsl-manually-on-non-system-drive
			                                                                         https://docs.microsoft.com/es-es/windows/wsl/install-manual


			------------------------------------------------------------------------------------------
			Install https://damsteen.nl/blog/2018/08/29/installing-wsl-manually-on-non-system-drive
			------------------------------------------------------------------------------------------
			Windows > Create directory C:\store\ubuntu\ubuntu1804

			PowerShell > Set-Location C:\store\ubuntu\ubuntu1804
			PowerShell > Invoke-WebRequest -Uri https://aka.ms/wsl-ubuntu-1804 -OutFile Ubuntu.appx -UseBasicParsing
			PowerShell > Rename-Item .\Ubuntu.appx Ubuntu.zip
			PowerShell > Expand-Archive .\Ubuntu.zip -Verbose

			Windows > Run C:\store\ubuntu\ubuntu1804\Ubuntu\ubuntu1804.exe as an Administrator
				Installing, this may take a few minutes...
				Please create a default UNIX user account. The username does not need to match your Windows username.
				For more information visit: https://aka.ms/wslusers
				Enter new UNIX username: ubuntu
				Enter new UNIX password: ubuntu
				Retype new UNIX password: ubuntu
				passwd: password updated successfully
				Installation successful!
				To run a command as administrator (user "root"), use "sudo <command>".
				See "man sudo_root" for details.

			Windows > Run C:\store\ubuntu\ubuntu1804\Ubuntu\ubuntu1804.exe


			----------------------------------------------------------
			Uninstall https://github.com/Microsoft/WSL/issues/3817
			----------------------------------------------------------
			PowerShell >  wslconfig /list
				Distribuciones del subsistema de Windows para Linux:
				Ubuntu-18.04 (predet.)
				Ubuntu-16.04

			PowerShell > wslconfig /unregister Ubuntu-18.04
				Eliminando del registro...

			Windows > Delete dir C:\store\ubuntu\ubuntu1804\Ubuntu\

-  3) Option 3:

				https://chocolatey.org/packages/wsl-ubuntu-1804

				PowerShell> Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
				PowerShell> choco install wsl-ubuntu-1804

				...Linux will be installed to..

				C:\ProgramData\chocolatey\lib


				New-Item c:\store\ubuntu -ItemType Directory
				Set-Location c:\store\ubuntu\ubuntu1804


				Invoke-WebRequest -Uri https://aka.ms/wsl-ubuntu-1804 -OutFile Ubuntu.appx -UseBasicParsing


Ubuntu root FileSystem : C:\store\ubuntu\ubuntu1804\Ubuntu\rootfs\
User ubuntu home dir : C:\store\ubuntu\ubuntu1804\Ubuntu\rootfs\home\ubuntu

[2] - Install multiple instances of ubuntu in the same WSL:
	https://stackoverflow.com/questions/51584765/how-do-you-install-multiple-separate-instances-of-ubuntu-in-wsl

