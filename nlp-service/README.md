# NLP Summarization Service

Servizio Python per il riassunto di testi utilizzando il modello BART di Hugging Face.

## Caratteristiche

- **Modello BART**: Utilizza `facebook/bart-large-cnn` per riassunti di alta qualità
- **Chunking intelligente**: Gestisce testi lunghi spezzandoli in chunk gestibili (~1024 token)
- **Estrazione multi-formato**: Supporta PDF, DOCX, HTML e TXT
- **API REST**: Interfaccia FastAPI per facile integrazione

## Struttura

```
nlp-service/
├── app/
│   ├── main.py           # Controller FastAPI
│   ├── summarizer.py     # Modello BART
│   ├── cleaning.py       # Pulizia testo
│   ├── chunking.py       # Gestione chunk
│   └── extractor/        # Estrattori per vari formati
│       ├── pdf_extractor.py
│       ├── docx_extractor.py
│       ├── html_extractor.py
│       └── txt_extractor.py
├── requirements.txt
└── README.md
```

## Installazione

1. Crea un ambiente virtuale:
```bash
python -m venv venv
```

2. Attiva l'ambiente:
```bash
# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

3. Installa le dipendenze:
```bash
pip install -r requirements.txt
```

## Utilizzo

1. Avvia il server:
```bash
python app/main.py
```

Il server sarà disponibile su `http://localhost:8000`

2. Endpoints disponibili:

- `GET /` - Info sul servizio
- `GET /health` - Health check
- `POST /summarize` - Riassumi un testo

### Esempio di richiesta:

```bash
curl -X POST "http://localhost:8000/summarize" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Il tuo testo lungo da riassumere...",
    "max_length": 150,
    "min_length": 50
  }'
```

## Note

- Il modello BART viene caricato **una sola volta** all'avvio del servizio
- Il limite di token di BART è ~1024, gestito automaticamente dal chunking
- Per testi molto lunghi, i chunk vengono riassunti separatamente e poi combinati
