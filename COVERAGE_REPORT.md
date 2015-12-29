To generate the code coverage report, execute the following command:

gradlew build -x test
gradlew testAll
gradlew testReport
gradlew jacocoReport

This will generate code coverage report in each of the modules. In order to view the same, open the following file in your browser.

build/reports/coverage/index.html

Please note that the above folder is created under each of the modules. For example:

build/reports/coverage/jodd-log/index.html
