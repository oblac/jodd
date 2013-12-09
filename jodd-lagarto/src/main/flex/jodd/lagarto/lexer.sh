#!/bin/sh
set -e

PRJ_ROOT=../../../../../../
JFLEX_ROOT=$PRJ_ROOT/etc/jflex

java -jar $JFLEX_ROOT/JFlex-1.4.3-fixes.jar --skel $JFLEX_ROOT/skeleton.chararray.jodd -d . lexer.flex

mv Lexer.java ../../../java/jodd/lagarto/

echo done!