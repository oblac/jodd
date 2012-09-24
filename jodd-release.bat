@echo off
echo.
echo JODD Release
echo.

call m clean
call m -P release
call m -P post-release

echo.
echo Done.
echo.