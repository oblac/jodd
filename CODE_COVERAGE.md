# Code Coverage Report generation

To generate the code coverage report, execute the following command:

Windows:

	gradlew.bat jacocoReport

Linux/Unix/OSX:

	./gradlew jacocoReport

This will generate code coverage report for all the modules.
In order to view the report for a single module, open the following file:

	<MODULE_NAME>/build/reports/jacoco/test/html/index.html

