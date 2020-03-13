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

	Download and install [powershell 7]: https://github.com/PowerShell/PowerShell/releases/tag/v7.0.0

INSTALL WINDOWS LINUX SUBSYSTEM
================================
https://adamtheautomator.com/windows-subsystem-for-linux/#updating-to-windows-subsystem-for-linux-2

[1] - Install Windows SubSystem for Linux

	PowerShell > Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux


[2] - Update to Windows Subsystem for Linux 2:

	PowerShell > Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform


[3] Install ubuntu:

1) Option A:

	[windows store] app
		Search for wsl or wsl ubuntu
		Intall
			Launch

2) Option B: Install WSL Manually on NON System Drive

https://damsteen.nl/blog/2018/08/29/installing-wsl-manually-on-non-system-drive

https://docs.microsoft.com/es-es/windows/wsl/install-manual

**Installation:**

	Create the directory where ubuntu will reside > C:\develop\wsl\ubuntu1804

	PowerShell> Set-Location C:\develop\wsl\ubuntu1804
	PowerShell> Invoke-WebRequest -Uri https://aka.ms/wsl-ubuntu-1804 -OutFile Ubuntu.appx -UseBasicParsing
	PowerShell> Rename-Item .\Ubuntu.appx Ubuntu.zip
	PowerShell> Expand-Archive .\Ubuntu.zip -Verbose

	cmd > C:\develop\wsl\ubuntu1804\ubuntu1804.exe as an Administrator

	(the ubuntu filesystem will be at: C:\develop\wsl\ubuntu1804\Ubuntu\rootfs)

**run the installed ubuntu** 

	cmd > C:\develop\wsl\ubuntu1804\ubuntu1804.exe

... or just [start] > [ubuntu]


**Uninstall** https://github.com/Microsoft/WSL/issues/3817

	PowerShell>wslconfig /list
		Distribuciones del subsistema de Windows para Linux:
		Ubuntu-18.04 (predet.)
		Ubuntu-16.04

	PowerShell> wslconfig /unregister Ubuntu-18.04
		
		Eliminando del registro...

	Delete dir C:\develop\wsl\ubuntu1804

3) Option 3: Chocolatey > https://chocolatey.org/packages/wsl-ubuntu-1804

Install chocolatey:

	PowerShell> Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
	PowerShell> choco install wsl-ubuntu-1804
				...Linux will be installed to..
				C:\ProgramData\chocolatey\lib

				New-Item C:\develop\wsl\ubuntu1804 -ItemType Directory
				Set-Location C:\develop\wsl\ubuntu1804

				Invoke-WebRequest -Uri https://aka.ms/wsl-ubuntu-1804 -OutFile Ubuntu.appx -UseBasicParsing

Ubuntu root FileSystem : C:\develop\wsl\ubuntu1804\Ubuntu\rootfs\
User ubuntu home dir : C:\develop\wsl\ubuntu1804\Ubuntu\rootfs\home\ubuntu

[2] - Install multiple instances of ubuntu in the same WSL:
	https://stackoverflow.com/questions/51584765/how-do-you-install-multiple-separate-instances-of-ubuntu-in-wsl


