#!/bin/sh
set -e

PRJ_ROOT=../../../../../../
JFLEX_ROOT=$PRJ_ROOT/etc/jflex

java -jar $JFLEX_ROOT/JFlex-1.6.1.jar --skel $JFLEX_ROOT/skeleton.chararray-1.6.1.jodd -d . csselly.flex

mv CSSellyLexer.java ../../../java/jodd/csselly/

echo done!