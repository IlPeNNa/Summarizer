# Script per avviare l'intera applicazione Summarizer
# Esegui questo script dalla root del progetto

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  SUMMARIZER - Avvio Applicazione   " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Verifica che siamo nella directory corretta
if (-not (Test-Path ".\nlp-service") -or -not (Test-Path ".\Summarizer-BE") -or -not (Test-Path ".\Summarizer-FE")) {
    Write-Host "[X] Errore: Esegui questo script dalla root del progetto Summarizer!" -ForegroundColor Red
    exit 1
}

# 1. Avvia NLP Service
Write-Host "[1/3] Avvio NLP Service (Python)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\nlp-service'; Write-Host 'NLP Service' -ForegroundColor Green; if (Test-Path '.venv\Scripts\Activate.ps1') { .venv\Scripts\Activate.ps1 }; python app/main.py"
Write-Host "   OK - NLP Service avviato su http://localhost:8000" -ForegroundColor Green
Start-Sleep -Seconds 3

# 2. Avvia Backend Spring Boot
Write-Host "[2/3] Avvio Backend (Spring Boot)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\Summarizer-BE'; Write-Host 'Spring Boot Backend' -ForegroundColor Green; .\gradlew bootRun"
Write-Host "   OK - Backend avviato su http://localhost:8080" -ForegroundColor Green
Start-Sleep -Seconds 5

# 3. Avvia Frontend Angular
Write-Host "[3/3] Avvio Frontend (Angular)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\Summarizer-FE'; Write-Host 'Angular Frontend' -ForegroundColor Green; npm start"
Write-Host "   OK - Frontend avviato su http://localhost:4200" -ForegroundColor Green

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  APPLICAZIONE AVVIATA!             " -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs:" -ForegroundColor White
Write-Host "   Frontend:    http://localhost:4200" -ForegroundColor Cyan
Write-Host "   Backend:     http://localhost:8080" -ForegroundColor Cyan
Write-Host "   NLP Service: http://localhost:8000" -ForegroundColor Cyan
Write-Host ""
Write-Host "INFO: Apri il browser su http://localhost:4200" -ForegroundColor Yellow
Write-Host ""
Write-Host "ATTENZIONE: Per fermare i servizi, chiudi le finestre PowerShell" -ForegroundColor Yellow
Write-Host ""
