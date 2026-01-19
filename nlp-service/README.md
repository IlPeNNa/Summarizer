# NLP Summarization Service

Servizio Python per il riassunto di testi.

## Caratteristiche

- **Modello configurabile**: Placeholder per modelli di summarization personalizzati
- **Chunking intelligente**: Gestisce testi lunghi spezzandoli in chunk
- **Estrazione multi-formato**: Supporta PDF, DOCX, HTML e TXT
- **API REST**: Interfaccia FastAPI per facile integrazione

## TODO

- [ ] Scegliere e configurare modello multilingua per riassunti
- [ ] Opzionale: Aggiungere modello specifico per italiano
- [ ] Implementare logica di summarization in `summarizer.py`

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
- `POST /summarize` - Riassumi un testo (da implementare con il modello scelto)

## Modelli da Configurare

Quando sarai pronto a configurare i modelli:

1. **Modello multilingua**: Scegli un modello per riassunti in più lingue
2. **Modello italiano** (opzionale): Aggiungi un modello specifico per italiano
3. Aggiorna `summarizer.py` con il caricamento e l'utilizzo dei modelli
4. Aggiungi le dipendenze necessarie in `requirements.txt`
