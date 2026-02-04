# 📝 Summarizer - Applicazione Completa

Applicazione web per riassumere testi utilizzando AI con interfaccia moderna e user-friendly.

## 🏗️ Architettura

- **Frontend**: Angular 18 con interfaccia moderna e responsive
- **Backend**: Spring Boot 3.4.4 con Java 21
- **NLP Service**: Python FastAPI con modello IT5 da HuggingFace
- **Database**: MySQL (per future recensioni - WIP)

## ✨ Funzionalità

### Input del Testo
- ✍️ **Scrivi/Incolla**: Campo di testo per inserire manualmente il contenuto
- 📎 **Upload File**: Caricamento file TXT/JSON con drag & drop
- 🎯 **Drag & Drop**: Trascina file direttamente dal desktop

### Parametri Personalizzabili
- 📏 **Preset Lunghezza**:
  - Breve (30-80 caratteri)
  - Medio (50-150 caratteri)
  - Lungo (100-250 caratteri)
  - Personalizzato con slider
- 🎚️ **Slider Interattivi**: Regola min/max lunghezza del riassunto

### Download Multi-Formato
- 📄 **TXT**: Download diretto
- 📘 **DOCX**: Documento Microsoft Word
- 📕 **PDF**: Documento PDF formattato

### Statistiche
- Lunghezza testo originale
- Lunghezza riassunto
- Percentuale di riduzione

## 🚀 Come Avviare

### 1. NLP Service (Python)

```powershell
# Dalla root del progetto
cd nlp-service

# Attiva ambiente virtuale (se esistente)
.venv\Scripts\Activate.ps1

# Installa dipendenze (prima volta)
pip install -r requirements.txt

# Avvia il servizio
python app/main.py
```

Il servizio sarà disponibile su `http://localhost:8000`

### 2. Backend (Spring Boot)

```powershell
# Dalla root del progetto
cd Summarizer-BE

# Build e avvio
.\gradlew bootRun
```

Il backend sarà disponibile su `http://localhost:8080`

### 3. Frontend (Angular)

```powershell
# Dalla root del progetto
cd Summarizer-FE

# Installa dipendenze (prima volta)
npm install

# Avvia il frontend
npm start
```

Il frontend sarà disponibile su `http://localhost:4200`

## 📋 Ordine di Avvio

1. **NLP Service** (porta 8000) - PRIMO
2. **Backend** (porta 8080) - SECONDO
3. **Frontend** (porta 4200) - TERZO

## 🛠️ Configurazione

### application.yaml (Backend)

```yaml
server:
  port: 8080

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

### Dipendenze Aggiunte

**Backend (build.gradle)**:
```gradle
// Apache POI per DOCX
implementation 'org.apache.poi:poi-ooxml:5.2.5'

// PDFBox per PDF
implementation 'org.apache.pdfbox:pdfbox:3.0.1'
```

**Frontend (package.json)**:
```json
{
  "@angular/common": "^18.x",
  "@angular/forms": "^18.x"
}
```

## 📡 API Endpoints

### POST `/api/summarize`
Riassume un testo passato come JSON.

```json
{
  "text": "Il tuo testo da riassumere...",
  "minLength": 50,
  "maxLength": 150
}
```

### POST `/api/summarize/upload`
Upload e riassunto di un file.

**Form Data**:
- `file`: File TXT/JSON
- `minLength`: (opzionale, default 50)
- `maxLength`: (opzionale, default 150)

### POST `/api/summarize/download/txt`
Download riassunto in formato TXT.

### POST `/api/summarize/download/docx`
Download riassunto in formato DOCX.

### POST `/api/summarize/download/pdf`
Download riassunto in formato PDF.

### GET `/api/summarize/health`
Health check del servizio NLP.

## 🎨 Interfaccia Utente

L'interfaccia è moderna e responsive con:
- Gradiente viola/blu
- Animazioni fluide
- Feedback visivo immediato
- Icone emoji intuitive
- Layout a 2 colonne (input/output)
- Statistiche in tempo reale
- Pulsanti di download con icone

## 🔮 Prossimi Sviluppi

- ✅ Database MySQL per recensioni utenti
- ✅ Sistema di rating dei riassunti
- ✅ Storico riassunti
- ✅ Autenticazione utenti
- ✅ Export multipli simultanei
- ✅ Supporto più formati file (DOCX, PDF input)

## 🐛 Troubleshooting

### Errore CORS
Verifica che il backend abbia `@CrossOrigin(origins = "http://localhost:4200")`.

### Modello non scarica
Alla prima esecuzione il modello IT5 verrà scaricato da HuggingFace (~500MB). Attendi il completamento.

### Errore upload file
Verifica che il file sia TXT o JSON e non superi 10MB.

### Errore generazione PDF/DOCX
Verifica che le dipendenze Apache POI e PDFBox siano installate correttamente:
```powershell
.\gradlew build --refresh-dependencies
```

## 📝 Note Tecniche

- Il modello NLP supporta testi fino a 1024 token per chunk
- Il sistema gestisce automaticamente il chunking per testi lunghi
- La validazione richiede minimo 100 caratteri per il riassunto
- I parametri sono validati sia lato frontend che backend

## 📄 Licenza

MIT License - vedi LICENSE file per dettagli

---

**Sviluppato usando Angular, Spring Boot e Python**
