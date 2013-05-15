PRJ_ROOT=../../../../../../

java -jar $PRJ_ROOT/etc/jflex/JFlex-1.4.3.jar -d . csselly.flex

mv CSSellyLexer.java ../../../java/jodd/csselly/

echo done!