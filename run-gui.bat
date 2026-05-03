@echo off
echo Running GUI app...
java -cp out;lib\sqlite-jdbc.jar stmarys.library.Main
exit /b %ERRORLEVEL%
