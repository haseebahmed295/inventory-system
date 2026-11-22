@echo off
setlocal
echo ===============================================================================
echo   Generating Javadoc HTML Documentation - SE-314 OEL
echo ===============================================================================
set SCRIPT_DIR=%~dp0
set JAVADOC_CMD="%SCRIPT_DIR%..\jdk-27_windows-x64_bin\jdk-27\bin\javadoc.exe"
set MODULE_PATH="%SCRIPT_DIR%lib"
set DOCS_DIR="%SCRIPT_DIR%docs"

if not exist %JAVADOC_CMD% (
    set JAVADOC_CMD=javadoc
)

%JAVADOC_CMD% --module-path %MODULE_PATH% --add-modules javafx.controls,javafx.fxml -d %DOCS_DIR% -author -version -private "%SCRIPT_DIR%InventoryItem.java" "%SCRIPT_DIR%InventoryManager.java" "%SCRIPT_DIR%Main.java" "%SCRIPT_DIR%InventoryApp.java" "%SCRIPT_DIR%InventoryTest.java"

if errorlevel 0 (
    echo.
    echo [SUCCESS] Javadoc successfully generated in docs\
    echo Opening index.html in browser...
    start "" "%SCRIPT_DIR%docs\index.html"
) else (
    echo.
    echo [ERROR] Javadoc generation encountered issues.
)
pause
endlocal
