Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "  Store Inventory Management System (JavaFX) - SE-314 OEL" -ForegroundColor Cyan
Write-Host "  Candidate: Haseeb Ahmad | MCS NUST" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Launching JavaFX GUI Application with JDK 27..." -ForegroundColor Green

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$JavaPath = Join-Path $ScriptDir "..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe"
$ModulePath = Join-Path $ScriptDir "lib"

if (-not (Test-Path $JavaPath)) {
    $JavaPath = "java"
}

& $JavaPath --module-path $ModulePath --add-modules javafx.controls,javafx.fxml -cp $ScriptDir Main
