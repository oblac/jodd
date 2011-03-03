setlocal

call d:\java\ideaX\tools\lexer\jflex-1.4\bin\jflex.bat --charat -d . --skel d:\java\ideaX\tools\lexer\idea-flex.skeleton props.flex

del *.java~

endlocal