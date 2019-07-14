cd %~dp0
copy /Y new_ver.exe ..\SubTitleSearcher.exe
start ..\SubTitleSearcher.exe
cd ..
ping -n 3 127.0.0.1>nul
rd /S /Q upgrade
