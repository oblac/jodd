#!/bin/sh

PWD=`pwd`

echo "Running Codacy PMD in $PWD"

docker run -it -v $PWD:/src codacy/codacy-pmdjava
