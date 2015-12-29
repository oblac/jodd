# Code Coverage Report generation

To generate the code coverage report, execute the following command:

Windows:

	gradlew jacocoReport

Linux/Unix/OSX:

	./gradlew jacocoReport

This will generate code coverage report for all the modules. In order to view the report for a module, open the following file in your browser:

	build/reports/coverage/MODULE_NAME/index.html

For example:

* `build/reports/coverage/jodd-decora/index.html`
* `build/reports/coverage/jodd-log/index.html`
