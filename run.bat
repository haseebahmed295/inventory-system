@echo off
setlocal
echo ===============================================================================
echo   Store Inventory Management System (JavaFX) - SE-314 OEL
echo   Candidate: Haseeb Ahmad ^| MCS NUST
echo ===============================================================================
echo Launching Application with JDK 27 and JavaFX Runtime...

set SCRIPT_DIR=%~dp0
set JAVA_CMD="%SCRIPT_DIR%..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe"
set MODULE_PATH="%SCRIPT_DIR%lib"

if not exist %JAVA_CMD% (
    set JAVA_CMD=java
)

%JAVA_CMD% --module-path %MODULE_PATH% --add-modules javafx.controls,javafx.fxml -cp "%SCRIPT_DIR%." Main

if errorlevel 1 (
    echo.
    echo Application exited with code %errorlevel%.
    pause
)
endlocal
