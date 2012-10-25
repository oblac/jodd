@echo off

cls

echo.
echo Create Jodd staging bundle
echo.

setlocal

set JODD_VERSION=3.4.0
set GPG_ARGS=--batch --passphrase-file .pass --armor --detach-sign
set ALL_FILES=


echo -- root
echo.

cp pom.xml target/jodd-%JODD_VERSION%.pom
if exist target/*.asc rm -f target/*.asc

set JFILE=target/jodd-%JODD_VERSION%

gpg %GPG_ARGS% %JFILE%.pom

set ALL_FILES=%ALL_FILES% %JFILE%.pom
set ALL_FILES=%ALL_FILES% %JFILE%.pom.asc



echo -- modules
echo.

for /D %%i in (jodd-*) do call :loop %%i

mkdir bundle

for %%f in (%ALL_FILES%) do cp %%f bundle
cd bundle
jar -cvf ../jodd-bundle.jar *
cd ..
rm -rf bundle

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

set ALL_FILES=%ALL_FILES% %JFILE%.jar
set ALL_FILES=%ALL_FILES% %JFILE%.jar.asc

set ALL_FILES=%ALL_FILES% %JFILE%-sources.jar
set ALL_FILES=%ALL_FILES% %JFILE%-sources.jar.asc

set ALL_FILES=%ALL_FILES% %JFILE%-javadoc.jar
set ALL_FILES=%ALL_FILES% %JFILE%-javadoc.jar.asc

set ALL_FILES=%ALL_FILES% %JFILE%.pom
set ALL_FILES=%ALL_FILES% %JFILE%.pom.asc


:end