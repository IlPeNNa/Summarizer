# Setup Completo Progetto Summarizer

Guida rapida per configurare l'intero progetto su un nuovo computer.

## Prerequisiti

- **Python 3.10+**
- **Java 21**
- **Node.js 18+**
- **MySQL** (per il backend)
- **Git**

## Setup Passo-Passo

### 1. Clone del Repository
```bash
git clone <url-repository>
cd Summarizer
```

### 2. Frontend Angular (Summarizer-FE)
```bash
cd Summarizer-FE
npm install
npm start
```
→ Disponibile su `http://localhost:4200`

### 3. Backend Java (Summarizer-BE)
```bash
cd Summarizer-BE

# Avvia il database MySQL
.\start-db.cmd  # Windows
./start-db.sh   # Linux/Mac

# Avvia il backend
.\gradlew bootRun  # Windows
./gradlew bootRun  # Linux/Mac
```
→ Disponibile su `http://localhost:8080`

### 4. Servizio NLP Python (nlp-service)
```bash
cd nlp-service

# Crea ambiente virtuale
python -m venv venv

# Attiva ambiente
.\venv\Scripts\Activate.ps1  # Windows PowerShell
source venv/bin/activate      # Linux/Mac

# Installa dipendenze
pip install -r requirements.txt

# Avvia servizio
python app/main.py
```
→ Disponibile su `http://localhost:8000`

**NOTA**: Al primo avvio, il modello mT5 (~2.3 GB) verrà scaricato automaticamente.

## Architettura

```
Frontend (Angular - :4200)
    ↓
Backend Java (Spring Boot - :8080)
    ↓
Servizio NLP Python (:8000)
    ↓
Modello mT5 (Google)
```

## File da NON Committare

Già configurato in `.gitignore`:
- `nlp-service/venv/` - ambiente virtuale Python
- `node_modules/` - dipendenze Node.js
- `build/` - build Java
- Cache modelli Hugging Face (nella home utente)

## Note Importanti

1. **Modelli NLP**: Scaricati automaticamente in `~/.cache/huggingface/`
2. **Primo avvio**: Richiede connessione internet per scaricare modelli
3. **Avvii successivi**: Usano modelli dalla cache locale
