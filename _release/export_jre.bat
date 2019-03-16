set JAVA_HOME=D:\Java\jdk-11\
set CLASSPATH=%JAVA_HOME%\jre\lib\rt.jar;%JAVA_HOME%\lib\tools.jar
set PATH=%JAVA_HOME%\bin
jlink -p %JAVA_HOMER%/jmods --add-modules java.base,java.desktop,java.sql --output jre-11
rd /s /q jre-11\legal
rd /s /q jre-11\include
del /q jre-11\release

