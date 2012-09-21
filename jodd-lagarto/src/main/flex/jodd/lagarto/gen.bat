setlocal

call d:\java\jflex-1.4.3\bin\jflex.bat -d . lexer.flex

del *.java~

endlocal