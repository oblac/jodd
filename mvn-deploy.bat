@echo off

setlocal

set v=3.2.6
set repo=-Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2 -DrepositoryId=sonatype-nexus-staging -DgeneratePom=false
set repo_jodd=%repo% -DpomFile=mvn\jodd.pom.xml
set repo_jodd_wot=%repo% -DpomFile=mvn\jodd-wot.pom.xml



echo.
echo --==[ Signing ]==--
echo.

rm -f dist/*.asc
rm -f mvn/*.asc

gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-%v%.jar
gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-%v%-sources.jar
gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-%v%-javadoc.jar
gpg --batch --passphrase-file .pass --armor --detach-sign mvn/jodd.pom.xml

gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-wot-%v%.jar
gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-wot-%v%-sources.jar
gpg --batch --passphrase-file .pass --armor --detach-sign dist/jodd-wot-%v%-javadoc.jar
gpg --batch --passphrase-file .pass --armor --detach-sign mvn/jodd-wot.pom.xml

pause.



echo.
echo --==[ Deploy Jodd %v% ]==--
echo.

call mvn deploy:deploy-file %repo_jodd%											 -Dfile=dist\jodd-%v%.jar
call mvn deploy:deploy-file %repo_jodd% 	 				 -Dpackaging=jar.asc -Dfile=dist\jodd-%v%.jar.asc

call mvn deploy:deploy-file %repo_jodd% -Dclassifier=sources -Dpackaging=jar     -Dfile=dist\jodd-%v%-sources.jar
call mvn deploy:deploy-file %repo_jodd% -Dclassifier=sources -Dpackaging=jar.asc -Dfile=dist\jodd-%v%-sources.jar.asc

call mvn deploy:deploy-file %repo_jodd% -Dclassifier=javadoc -Dpackaging=jar     -Dfile=dist\jodd-%v%-javadoc.jar
call mvn deploy:deploy-file %repo_jodd% -Dclassifier=javadoc -Dpackaging=jar.asc -Dfile=dist\jodd-%v%-javadoc.jar.asc

call mvn deploy:deploy-file %repo_jodd%						 -Dpackaging=pom	 -Dfile=mvn\jodd.pom.xml
call mvn deploy:deploy-file %repo_jodd% 	 				 -Dpackaging=pom.asc -Dfile=mvn\jodd.pom.xml.asc


echo.
echo --==[ Deploy Jodd WOT %v% ]==--
echo.

call mvn deploy:deploy-file %repo_jodd_wot%      			                         -Dfile=dist\jodd-wot-%v%.jar
call mvn deploy:deploy-file %repo_jodd_wot%                      -Dpackaging=jar.asc -Dfile=dist\jodd-wot-%v%.jar.asc

call mvn deploy:deploy-file %repo_jodd_wot% -Dclassifier=sources -Dpackaging=jar     -Dfile=dist\jodd-wot-%v%-sources.jar
call mvn deploy:deploy-file %repo_jodd_wot% -Dclassifier=sources -Dpackaging=jar.asc -Dfile=dist\jodd-wot-%v%-sources.jar.asc

call mvn deploy:deploy-file %repo_jodd_wot% -Dclassifier=javadoc -Dpackaging=jar     -Dfile=dist\jodd-wot-%v%-javadoc.jar
call mvn deploy:deploy-file %repo_jodd_wot% -Dclassifier=javadoc -Dpackaging=jar.asc -Dfile=dist\jodd-wot-%v%-javadoc.jar.asc

call mvn deploy:deploy-file %repo_jodd_wot%					 	 -Dpackaging=pom	 -Dfile=mvn\jodd-wot.pom.xml
call mvn deploy:deploy-file %repo_jodd_wot%  				 	 -Dpackaging=pom.asc -Dfile=mvn\jodd-wot.pom.xml.asc



endlocal

echo.
echo Done!
echo.
