@echo off
echo Running console app...
java -cp out;lib\sqlite-jdbc.jar stmarys.library.Main console
exit /b %ERRORLEVEL%
