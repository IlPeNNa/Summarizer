# Script per fermare tutti i servizi Summarizer

Write-Host "=====================================" -ForegroundColor Red
Write-Host "  SUMMARIZER - Stop Applicazione    " -ForegroundColor Red
Write-Host "=====================================" -ForegroundColor Red
Write-Host ""

# Ferma processi Node (Angular)
Write-Host "🛑 Fermando Angular Frontend..." -ForegroundColor Yellow
Get-Process -Name "node" -ErrorAction SilentlyContinue | Stop-Process -Force
Write-Host "   ✓ Angular fermato" -ForegroundColor Green

# Ferma processi Java (Spring Boot)
Write-Host "🛑 Fermando Spring Boot Backend..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {$_.Path -like "*gradle*" -or $_.CommandLine -like "*Application*"} | Stop-Process -Force
Write-Host "   ✓ Spring Boot fermato" -ForegroundColor Green

# Ferma processi Python (NLP Service)
Write-Host "🛑 Fermando NLP Service..." -ForegroundColor Yellow
Get-Process -Name "python" -ErrorAction SilentlyContinue | Where-Object {$_.CommandLine -like "*main.py*"} | Stop-Process -Force
Write-Host "   ✓ NLP Service fermato" -ForegroundColor Green

Write-Host ""
Write-Host "✅ Tutti i servizi sono stati fermati!" -ForegroundColor Green
Write-Host ""
