set JAVA_HOME=D:\Java\jdk-10\
set CLASSPATH=%JAVA_HOME%\jre\lib\rt.jar;%JAVA_HOME%\lib\tools.jar
set PATH=%JAVA_HOME%\bin
set PUBLISH_PATH=_publish
set PUBLISH_JRE_PATH=%PUBLISH_PATH%\jre
rd /s /q %PUBLISH_JRE_PATH%\
jlink -p %JAVA_HOME%/jmods --add-modules java.sql,javafx.base,javafx.swing,javafx.web --output %PUBLISH_JRE_PATH%
rd /s /q %PUBLISH_JRE_PATH%\legal
rd /s /q %PUBLISH_JRE_PATH%\include

rd /s /q %PUBLISH_JRE_PATH%\lib\server
del /q %PUBLISH_JRE_PATH%\bin\api-ms-win*
del /q %PUBLISH_JRE_PATH%\bin\appletviewer.exe
del /q %PUBLISH_JRE_PATH%\bin\keytool.exe
del /q %PUBLISH_JRE_PATH%\bin\ucrtbase.dll
del /q %PUBLISH_JRE_PATH%\bin\gstreamer-lite.dll
del /q %PUBLISH_JRE_PATH%\bin\concrt140.dll
del /q %PUBLISH_JRE_PATH%\release

