call mvn deploy:deploy-file -Durl=file:artifacts -Dfile=dist/jodd-3.0.4.jar     -DpomFile=mvn/jodd.pom
call mvn deploy:deploy-file -Durl=file:artifacts -Dfile=dist/jodd-wot-3.0.4.jar -DpomFile=mvn/jodd-wot.pom
