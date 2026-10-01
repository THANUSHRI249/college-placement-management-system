# PowerShell Launch Script for College Placement Management System

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Compiling College Placement Management System" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -d "bin" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Java compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "==================================================" -ForegroundColor Green
Write-Host "  Starting College Placement Management System" -ForegroundColor Green
Write-Host "  Done by THANUSHRI K, VALARMATHI R, SHALINI D" -ForegroundColor Yellow
Write-Host "==================================================" -ForegroundColor Green

java -cp bin main.PlacementManagementSystem
