@echo off
set "PS_PATH=C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe"

:: test.ps1 next to test.bat
"%PS_PATH%" -NoProfile -ExecutionPolicy Bypass -File "%~dp0effect.ps1" "Reconnecting to server..."

pause
