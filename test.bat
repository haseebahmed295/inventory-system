@echo off
setlocal
echo ===============================================================================
echo   Running Automated Test Suite (CLO-5 Test Cases) - SE-314 OEL
echo ===============================================================================
set SCRIPT_DIR=%~dp0
set JAVA_CMD="%SCRIPT_DIR%..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe"

if not exist %JAVA_CMD% (
    set JAVA_CMD=java
)

%JAVA_CMD% -cp "%SCRIPT_DIR%." InventoryTest
pause
endlocal
