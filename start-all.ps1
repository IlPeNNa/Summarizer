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

# 1. Verifica Database MySQL
Write-Host "[1/4] Verifica Database MySQL..." -ForegroundColor Yellow
$mysqlService = Get-Service -Name MySQL80 -ErrorAction SilentlyContinue
if ($mysqlService -and $mysqlService.Status -eq 'Running') {
    Write-Host "   OK - MySQL in esecuzione su localhost:3306" -ForegroundColor Green
} else {
    Write-Host "   [!] ATTENZIONE: MySQL non in esecuzione!" -ForegroundColor Red
    Write-Host "   Avvialo manualmente o esegui 'Start-Service MySQL80' come amministratore" -ForegroundColor Yellow
    exit 1
}

# 2. Avvia NLP Service
Write-Host "[2/4] Avvio NLP Service (Python)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\nlp-service'; Write-Host 'NLP Service' -ForegroundColor Green; if (Test-Path '.venv\Scripts\Activate.ps1') { .venv\Scripts\Activate.ps1 }; python app/main.py"
Write-Host "   OK - NLP Service avviato su http://localhost:8000" -ForegroundColor Green
Start-Sleep -Seconds 3

# 3. Avvia Backend Spring Boot
Write-Host "[3/4] Avvio Backend (Spring Boot)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\Summarizer-BE'; Write-Host 'Spring Boot Backend' -ForegroundColor Green; .\gradlew bootRun"
Write-Host "   OK - Backend avviato su http://localhost:8080" -ForegroundColor Green
Start-Sleep -Seconds 5

# 4. Avvia Frontend Angular
Write-Host "[4/4] Avvio Frontend (Angular)..." -ForegroundColor Yellow
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
Write-Host "   Database:    localhost:3306 (summarizerdb)" -ForegroundColor Cyan
Write-Host ""
Write-Host "INFO: Apri il browser su http://localhost:4200" -ForegroundColor Yellow
Write-Host ""
Write-Host "ATTENZIONE: Per fermare i servizi, chiudi le finestre PowerShell" -ForegroundColor Yellow
Write-Host ""
