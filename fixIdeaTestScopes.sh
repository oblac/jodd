#!/usr/bin/env bash

# Every time after Gradle project is refreshed, run this script
# It changes the 'test' flag to all `testInt` and `perf` scopes
# See: https://youtrack.jetbrains.com/issue/IDEA-151925

find .idea -name \*testInt\*.iml -exec perl -p -i -e 's/isTestSource="false"/isTestSource="true"/g' '{}' \;
find .idea -name \*perf.iml -exec perl -p -i -e 's/isTestSource="false"/isTestSource="true"/g' '{}' \;

echo "All test folders are now GREEN in IntelliJ IDEA :)"