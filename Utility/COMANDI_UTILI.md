# 🛠️ COMANDI UTILI - Cheat Sheet

## 🚀 Avvio Rapido

```powershell
# Avvia tutto (dalla root)
.\start-all.ps1

# Ferma tutto
.\stop-all.ps1
```

---

## 📦 NLP Service (Python)

```powershell
# Vai nella cartella
cd nlp-service

# Attiva ambiente virtuale
.venv\Scripts\Activate.ps1

# Installa dipendenze
pip install -r requirements.txt

# Avvia servizio
python app/main.py

# Test endpoint
curl http://localhost:8000/health

# Test riassunto
curl -X POST http://localhost:8000/summarize -H "Content-Type: application/json" -d "{\"text\":\"Il tuo testo qui...\",\"max_length\":150,\"min_length\":50}"
```

---

## ☕ Backend Spring Boot

```powershell
# Vai nella cartella
cd Summarizer-BE

# Build progetto
.\gradlew build

# Build senza test
.\gradlew build -x test

# Pulisci e rebuilda
.\gradlew clean build

# Avvia applicazione
.\gradlew bootRun

# Refresh dipendenze
.\gradlew build --refresh-dependencies

# Lista task disponibili
.\gradlew tasks

# Test
.\gradlew test

# Verifica health backend
curl http://localhost:8080/api/summarize/health
```

---

## 🅰️ Frontend Angular

```powershell
# Vai nella cartella
cd Summarizer-FE

# Installa dipendenze
npm install

# Avvia dev server
npm start
# oppure
ng serve

# Build production
npm run build
# oppure
ng build --configuration production

# Lint
ng lint

# Test
ng test

# Pulisci node_modules e reinstalla
Remove-Item -Recurse -Force node_modules
npm install
```

---

## 🗄️ Database MySQL

```sql
-- Connessione
mysql -u root -p

-- Usa database
USE summarizer;

-- Mostra tabelle
SHOW TABLES;

-- Descrivi struttura tabella
DESCRIBE summaries;

-- Conta riassunti
SELECT COUNT(*) FROM summaries;

-- Ultimi 10 riassunti
SELECT * FROM summaries ORDER BY created_at DESC LIMIT 10;

-- Drop database (ATTENZIONE!)
DROP DATABASE summarizer;

-- Crea database
CREATE DATABASE summarizer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 🐛 Debug e Troubleshooting

### Verifica porte in uso
```powershell
# Porta 8000 (NLP)
netstat -ano | findstr :8000

# Porta 8080 (Backend)
netstat -ano | findstr :8080

# Porta 4200 (Frontend)
netstat -ano | findstr :4200

# Termina processo per PID
taskkill /PID <numero_pid> /F
```

### Pulisci cache Gradle
```powershell
cd Summarizer-BE
.\gradlew clean
Remove-Item -Recurse -Force .gradle
Remove-Item -Recurse -Force build
```

### Reset completo frontend
```powershell
cd Summarizer-FE
Remove-Item -Recurse -Force node_modules
Remove-Item -Recurse -Force dist
Remove-Item -Force package-lock.json
npm install
```

---

## 📝 Git Comandi Utili

```powershell
# Status
git status

# Add tutti i file
git add .

# Commit
git commit -m "Messaggio commit"

# Push
git push origin main

# Pull
git pull origin main

# Nuovo branch
git checkout -b nome-branch

# Lista branch
git branch

# Merge branch
git checkout main
git merge nome-branch

# Annulla modifiche
git restore .

# Vedi log
git log --oneline
```

---

## 🧪 Test API con curl

### Riassunto Base
```powershell
curl -X POST http://localhost:8080/api/summarize `
  -H "Content-Type: application/json" `
  -d '{\"text\":\"Il tuo testo lungo da riassumere qui...\",\"minLength\":50,\"maxLength\":150}'
```

### Upload File
```powershell
curl -X POST http://localhost:8080/api/summarize/upload `
  -F "file=@test_examples/test_intelligenza_artificiale.txt" `
  -F "minLength=50" `
  -F "maxLength=150"
```

### Health Check
```powershell
# Backend
curl http://localhost:8080/api/summarize/health

# NLP Service
curl http://localhost:8000/health
```

---

## 📊 Monitoraggio Risorse

```powershell
# Processi Java
Get-Process java

# Processi Node
Get-Process node

# Processi Python
Get-Process python

# Uso RAM
Get-Process | Sort-Object -Property WS -Descending | Select-Object -First 10

# Uso CPU
Get-Process | Sort-Object -Property CPU -Descending | Select-Object -First 10
```

---

## 🔧 Configurazione IDE

### VS Code Extensions Consigliate
- Java Extension Pack
- Spring Boot Extension Pack
- Angular Language Service
- Python
- MySQL (cweijan.vscode-mysql-client2)
- Prettier
- ESLint

### IntelliJ IDEA Run Configurations
1. **Spring Boot App**
   - Main class: `it.unife.sample.Application`
   - VM options: `-Dspring.profiles.active=dev`

2. **Angular Dev Server**
   - Package manager: npm
   - Command: start

---

## 📦 Build e Deploy

### Build Backend (JAR)
```powershell
cd Summarizer-BE
.\gradlew bootJar
# Output: build/libs/myapp-0.0.1-SNAPSHOT.jar
```

### Run JAR
```powershell
java -jar build/libs/myapp-0.0.1-SNAPSHOT.jar
```

### Build Frontend (Production)
```powershell
cd Summarizer-FE
ng build --configuration production
# Output: dist/frontend/browser/
```

---

## 🔍 Log Viewing

### Backend Logs
```powershell
# Gradle output già mostra i log
# Per file di log personalizzati, controlla application.yaml
```

### Frontend Logs
```powershell
# Browser DevTools Console (F12)
# oppure verifica log npm/ng serve
```

### Python Logs
```powershell
# Output diretto nel terminale
# uvicorn mostra request/response logs
```

---

## 🎯 Quick Commands

```powershell
# Build tutto
cd Summarizer-BE; .\gradlew build; cd ..; cd Summarizer-FE; npm run build; cd ..

# Test tutto
cd Summarizer-BE; .\gradlew test; cd ..; cd Summarizer-FE; npm test; cd ..

# Clean tutto
cd Summarizer-BE; .\gradlew clean; cd ..; cd Summarizer-FE; Remove-Item -Recurse -Force dist; cd ..
```

---

## 📌 Variabili Ambiente Utili

```powershell
# Java Home
$env:JAVA_HOME

# Verifica versione Java
java -version

# Verifica versione Node
node -v

# Verifica versione npm
npm -v

# Verifica versione Python
python --version

# Verifica versione Angular CLI
ng version
```

---

**Salva questo file come riferimento rapido! 📖**
