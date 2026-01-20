# Setup NLP Service

Guida per configurare il servizio NLP su un nuovo computer.

## Prerequisiti

1. **Python 3.10+** installato
2. **Git** installato

## Setup Iniziale

### 1. Clona/Pull il repository
```bash
git pull origin main
# oppure su nuovo PC
git clone <url-repository>
```

### 2. Vai nella cartella del servizio NLP
```bash
cd nlp-service
```

### 3. Crea l'ambiente virtuale
```bash
python -m venv venv
```

### 4. Attiva l'ambiente virtuale
```bash
# Windows PowerShell
.\venv\Scripts\Activate.ps1

# Windows CMD
venv\Scripts\activate.bat

# Linux/Mac
source venv/bin/activate
```

### 5. Installa le dipendenze
```bash
pip install -r requirements.txt
```

### 6. Avvia il servizio
```bash
python app/main.py
```

**NOTA**: Al primo avvio su un nuovo PC, il modello `google/mt5-base` (~2.3 GB) verrà scaricato automaticamente da Hugging Face. Questo richiederà alcuni minuti.

Il modello viene salvato in cache:
- Windows: `C:\Users\<user>\.cache\huggingface\hub\`
- Linux/Mac: `~/.cache/huggingface/hub/`

Agli avvii successivi, il modello verrà caricato dalla cache locale (molto più veloce).

## Verifica

Una volta avviato, il servizio sarà disponibile su:
- `http://localhost:8000`

Testa con:
```bash
curl http://localhost:8000/health
```

Risposta attesa:
```json
{"status": "healthy", "model": "google/mt5-base"}
```
