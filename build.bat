@echo off
setlocal
echo ===============================================================================
echo   Compiling Inventory Management System - SE-314 OEL
echo ===============================================================================
set SCRIPT_DIR=%~dp0
set JAVAC_CMD="%SCRIPT_DIR%..\jdk-27_windows-x64_bin\jdk-27\bin\javac.exe"
set MODULE_PATH="%SCRIPT_DIR%lib"

if not exist %JAVAC_CMD% (
    set JAVAC_CMD=javac
)

%JAVAC_CMD% --module-path %MODULE_PATH% --add-modules javafx.controls,javafx.fxml -d "%SCRIPT_DIR%." "%SCRIPT_DIR%InventoryItem.java" "%SCRIPT_DIR%InventoryManager.java" "%SCRIPT_DIR%Main.java" "%SCRIPT_DIR%InventoryApp.java" "%SCRIPT_DIR%InventoryTest.java"

if errorlevel 0 (
    echo.
    echo [SUCCESS] Compilation finished successfully.
) else (
    echo.
    echo [ERROR] Compilation failed.
)
pause
endlocal
