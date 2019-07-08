#!/bin/sh

PWD=`pwd`

echo "Running Codacy Checkstyle in $PWD"
echo "To run inside the container:"
echo "    docker run -it --entrypoint bash -v $PWD:/src codacy/codacy-checkstyle"
echo "and then:"
echo "    cd src"
echo "    java -jar /opt/docker/checkstyle.jar -f xml -c /src/checkstyle.xml /src > checkstyle-results.xml"

docker run -it -v $PWD:/src codacy/codacy-checkstyle
