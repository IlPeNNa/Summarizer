# Script per testare il servizio NLP
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Servizio NLP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verifica che il servizio sia attivo
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8000/health" -Method Get
    Write-Host "OK Servizio NLP attivo!" -ForegroundColor Green
    Write-Host "  Modello: $($health.model)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "X Servizio NLP non risponde!" -ForegroundColor Red
    Write-Host "  Avvia prima il servizio con: .\start-nlp.cmd" -ForegroundColor Yellow
    exit 1
}

# Invia una richiesta di test
Write-Host "Invio testo di test..." -ForegroundColor Yellow

$testFile = "$PSScriptRoot\test_request.json"
if (-not (Test-Path $testFile)) {
    # Se non esiste nella root, prova in test_examples
    $testFile = "$PSScriptRoot\test_examples\test_ai.json"
}

if (Test-Path $testFile) {
    try {
        $result = Invoke-RestMethod -Uri "http://localhost:8000/summarize" -Method Post -ContentType 'application/json' -InFile $testFile
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "RIASSUNTO GENERATO:" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host $result.summary -ForegroundColor White
        Write-Host ""
        Write-Host "Lunghezza originale: $($result.original_length) caratteri" -ForegroundColor Gray
        Write-Host "Lunghezza riassunto: $($result.summary_length) caratteri" -ForegroundColor Gray
        Write-Host ""
        
    } catch {
        Write-Host "X Errore durante il test:" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
} else {
    Write-Host "X File test_request.json non trovato!" -ForegroundColor Red
}

Write-Host ""
Write-Host "Premi un tasto per uscire..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
