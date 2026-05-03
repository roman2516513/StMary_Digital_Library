@echo off
echo Building StMarysDigitalLibrary...
if not exist out mkdir out
javac -d out -cp lib\sqlite-jdbc.jar src\stmarys\library\*.java src\stmarys\library\dao\*.java src\stmarys\library\model\*.java src\stmarys\library\service\*.java src\stmarys\library\ui\*.java src\stmarys\library\util\*.java
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)
echo Build successful!
exit /b 0
