@echo off

cls

echo.
echo Publish Jodd to stage !
echo.

echo Publishing each artifact is slow and unreliable!
echo.
pause

setlocal

set JAVA_HOME=d:\java\jdk6
set JDK_HOME=d:\java\jdk6

set JODD_VERSION=3.4.1
set GPG_ARGS=--batch --passphrase-file .pass --armor --detach-sign
set DEPLOY_ARGS=-Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2 -DrepositoryId=sonatype-nexus-staging -DgeneratePom=false
set MVN=d:\java\apache-maven-3.0.4\bin\mvn.bat 




echo -- root
echo.

cp pom.xml target/jodd-%JODD_VERSION%.pom
if exist target/*.asc rm -f target/*.asc

set JFILE=target/jodd-%JODD_VERSION%

gpg %GPG_ARGS% %JFILE%.pom

call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dpackaging=pom -Dfile=%JFILE%.pom -Dfiles=
call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dpackaging=pom.asc -Dfile=%JFILE%.pom.asc




echo -- modules
echo.

for /D %%i in (jodd-*) do call :loop %%i

echo done.
endlocal

goto end




:loop

echo %1
echo.

cp %1/pom.xml %1/target/%1-%JODD_VERSION%.pom
if exist %1/target/*.asc rm -f %1/target/*.asc

set JFILE=%1/target/%1-%JODD_VERSION%

gpg %GPG_ARGS% %JFILE%.jar
gpg %GPG_ARGS% %JFILE%-sources.jar
gpg %GPG_ARGS% %JFILE%-javadoc.jar
gpg %GPG_ARGS% %JFILE%.pom

call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom                     -Dfile=%JFILE%.jar
call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dpackaging=jar.asc -Dfile=%JFILE%.jar.asc

call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dclassifier=sources -Dpackaging=jar     -Dfile=%JFILE%-sources.jar
call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dclassifier=sources -Dpackaging=jar.asc -Dfile=%JFILE%-sources.jar.asc

call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dclassifier=javadoc -Dpackaging=jar     -Dfile=%JFILE%-javadoc.jar
call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dclassifier=javadoc -Dpackaging=jar.asc -Dfile=%JFILE%-javadoc.jar.asc

call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dpackaging=pom     -Dfile=%JFILE%.pom
call %MVN% deploy:deploy-file %DEPLOY_ARGS% -DpomFile=%JFILE%.pom -Dpackaging=pom.asc -Dfile=%JFILE%.pom.asc


:end