"""
Controller principale del servizio NLP.
Gestisce le richieste HTTP e coordina le operazioni di summarization.
"""
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import uvicorn

from summarizer import Summarizer
from cleaning import clean_text
from chunking import chunk_text

app = FastAPI(title="NLP Summarization Service")

# Inizializza il summarizer con mT5
summarizer = Summarizer()


class SummarizationRequest(BaseModel):
    input: str
    maxLength: Optional[int] = 150
    minLength: Optional[int] = 50


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
        
        # 2. Chunking del testo (gestisce il limite di 1024 token)
        chunks = chunk_text(cleaned_text)
        
        # 3. Riassunto di ogni chunk
        summaries = []
        for chunk in chunks:
            summary = summarizer.summarize(
                chunk, 
                max_length=request.maxLength,
                min_length=request.minLength
            )
            summaries.append(summary)
        
        # 4. Combina i riassunti
        final_summary = " ".join(summaries)
        
        return SummarizationResponse(
            summary=final_summary,
            original_length=len(request.input),
            summary_length=len(final_summary)
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
