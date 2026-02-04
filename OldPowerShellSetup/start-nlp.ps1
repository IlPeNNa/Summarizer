# Script PowerShell per avviare il servizio NLP
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Avvio Servizio NLP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "$PSScriptRoot\nlp-service"

Write-Host "Attivazione ambiente virtuale..." -ForegroundColor Yellow
& .\venv\Scripts\Activate.ps1

Write-Host ""
Write-Host "Avvio servizio NLP su http://localhost:8000" -ForegroundColor Green
Write-Host "Premi CTRL+C per fermare il servizio" -ForegroundColor Yellow
Write-Host ""

python app\main.py
