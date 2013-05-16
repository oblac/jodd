PRJ_ROOT=../../../../../../

java -jar $PRJ_ROOT/etc/jflex/JFlex-1.4.3-fixes.jar -d . csselly.flex

mv CSSellyLexer.java ../../../java/jodd/csselly/

echo done!