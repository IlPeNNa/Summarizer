"""
Controller principale del servizio NLP.
Gestisce le richieste HTTP e coordina le operazioni di summarization.
"""
from fastapi import FastAPI, HTTPException, UploadFile, File
from pydantic import BaseModel
from typing import Optional
import uvicorn

from summarizer import Summarizer
from cleaning import clean_text
from chunking import chunk_text
from extractor import extract_from_pdf, extract_from_docx, extract_from_txt

app = FastAPI(title="NLP Summarization Service")

# Inizializza il summarizer con mT5
summarizer = Summarizer()


class SummarizationRequest(BaseModel):
    input: str
    maxLength: Optional[int] = 150
    minLength: Optional[int] = 50
    format: Optional[str] = "paragraph"


class SummarizationResponse(BaseModel):
    summary: str
    original_length: int
    summary_length: int


@app.get("/")
async def root():
    return {"message": "NLP Summarization Service", "status": "running"}


@app.get("/health")
async def health_check():
    return {"status": "healthy", "model": "ARTeLab/it5-summarization-mlsum"}


@app.post("/summarize", response_model=SummarizationResponse)
async def summarize_text(request: SummarizationRequest):
    """
    Endpoint per riassumere un testo.
    """
    try:
        # 1. Pulizia del testo
        cleaned_text = clean_text(request.input)
        
        # 2. Rileva la lingua UNA VOLTA prima del chunking
        detected_language = summarizer._detect_language(cleaned_text)
        
        # 3. Chunking del testo (gestisce il limite di 1024 token)
        chunks = chunk_text(cleaned_text)
        num_chunks = len(chunks)
        
        # 4. Distribuisci i limiti tra i chunk per rispettare max/min totali
        chunk_max_length = max(30, request.maxLength // num_chunks)
        chunk_min_length = max(10, request.minLength // num_chunks)
        
        # 5. Riassunto di ogni chunk usando la lingua già rilevata
        summaries = []
        for chunk in chunks:
            summary = summarizer.summarize(
                chunk, 
                max_length=chunk_max_length,
                min_length=chunk_min_length,
                language=detected_language
            )
            summaries.append(summary)
        
        # 6. Combina i riassunti
        final_summary = " ".join(summaries)
        
        # 7. Formatta l'output se richiesto
        if request.format == "bullet":
            final_summary = summarizer.format_as_bullets(final_summary)
        
        return SummarizationResponse(
            summary=final_summary,
            original_length=len(request.input),
            summary_length=len(final_summary)
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/extract/file")
async def extract_text_from_file(file: UploadFile = File(...)):
    """
    Endpoint per estrarre solo il testo da un file (TXT, PDF, DOCX) senza riassumerlo.
    """
    try:
        # Leggi il contenuto del file
        file_content = await file.read()
        
        # Determina il tipo di file dall'estensione
        filename = file.filename.lower() if file.filename else ""
        
        # Estrai il testo in base al tipo di file
        if filename.endswith('.pdf'):
            text = extract_from_pdf(file_content)
        elif filename.endswith('.docx'):
            text = extract_from_docx(file_content)
        elif filename.endswith('.txt'):
            text = extract_from_txt(file_content)
        else:
            # Prova a leggere come testo
            try:
                text = file_content.decode('utf-8')
            except:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Formato file non supportato. Usa .txt, .pdf o .docx"
                )
        
        if not text or len(text.strip()) == 0:
            raise HTTPException(status_code=400, detail="Il file è vuoto o non contiene testo estraibile")
        
        return {
            "text": text,
            "length": len(text),
            "filename": file.filename
        }
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Errore nell'estrazione del testo: {str(e)}")


@app.post("/summarize/file", response_model=SummarizationResponse)
async def summarize_file(
    file: UploadFile = File(...),
    maxLength: Optional[int] = 150,
    minLength: Optional[int] = 50,
    format: Optional[str] = "paragraph"
):
    """
    Endpoint per riassumere il contenuto di un file (TXT, PDF, DOCX).
    """
    try:
        # Leggi il contenuto del file
        file_content = await file.read()
        
        # Determina il tipo di file dall'estensione
        filename = file.filename.lower() if file.filename else ""
        
        # Estrai il testo in base al tipo di file
        if filename.endswith('.pdf'):
            text = extract_from_pdf(file_content)
        elif filename.endswith('.docx'):
            text = extract_from_docx(file_content)
        elif filename.endswith('.txt'):
            text = extract_from_txt(file_content)
        else:
            # Prova a leggere come testo
            try:
                text = file_content.decode('utf-8')
            except:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Formato file non supportato. Usa .txt, .pdf o .docx"
                )
        
        if not text or len(text.strip()) == 0:
            raise HTTPException(status_code=400, detail="Il file è vuoto o non contiene testo estraibile")
        
        # 1. Pulizia del testo
        cleaned_text = clean_text(text)
        
        # 2. Rileva la lingua
        detected_language = summarizer._detect_language(cleaned_text)
        
        # 3. Chunking del testo
        chunks = chunk_text(cleaned_text)
        num_chunks = len(chunks)
        
        # 4. Distribuisci i limiti tra i chunk
        chunk_max_length = max(30, maxLength // num_chunks)
        chunk_min_length = max(10, minLength // num_chunks)
        
        # 5. Riassunto di ogni chunk
        summaries = []
        for chunk in chunks:
            summary = summarizer.summarize(
                chunk, 
                max_length=chunk_max_length,
                min_length=chunk_min_length,
                language=detected_language
            )
            summaries.append(summary)
        
        # 6. Combina i riassunti
        final_summary = " ".join(summaries)
        
        # 7. Formatta l'output se richiesto
        if format == "bullet":
            final_summary = summarizer.format_as_bullets(final_summary)
        
        return SummarizationResponse(
            summary=final_summary,
            original_length=len(text),
            summary_length=len(final_summary)
        )
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Errore nell'elaborazione del file: {str(e)}")


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
