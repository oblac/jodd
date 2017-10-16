# Code Coverage Report generation

To generate the code coverage report, execute the following command:

Windows:

	gradlew.bat codeCoverageReport

Linux/Unix/OSX:

	./gradlew codeCoverageReport

This will generate the code coverage report for **ALL** the modules.

_NOTE_: since the code coverage task runs the integration tests, be sure that testing docker containers are up and running:

	cd docker
	docker-compose -f docker-compose-test.yml up  

### Reports location

Code Coverage report is located here:

	build/reports/jacoco/codeCoverageReport/html/index.html
	
## Code Coverage for a single module :star:

Run:

	./gradlew :<MODULE>:codeCoverage

The result is located in:

	MODULE/build/reports/jacoco/index.html
	
For example:

	./gradlew :jodd-core:codeCoverage
	open jodd-core/build/reports/jacoco/index.html
	
_NOTE_: Package `jodd.asm5` is _excluded_ from code coverage, but still reported locally.