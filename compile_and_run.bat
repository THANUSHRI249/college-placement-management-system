@echo off
echo ==================================================
echo   Compiling College Placement Management System
echo ==================================================

if not exist bin mkdir bin

javac -d bin src/model/*.java src/interfaces/*.java src/service/*.java src/main/*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo ==================================================
echo   Starting College Placement Management System
echo ==================================================

java -cp bin main.PlacementManagementSystem
