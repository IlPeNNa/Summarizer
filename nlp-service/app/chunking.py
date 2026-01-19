"""
Gestione dello chunking del testo per rispettare il limite di token di BART (~1024).
"""
from typing import List
import re


def chunk_text(
    text: str, 
    max_tokens: int = 900,
    overlap: int = 50,
    preserve_sentences: bool = True
) -> List[str]:
    """
    Divide il testo in chunk gestibili dal modello BART.
    
    Args:
        text: Testo da dividere
        max_tokens: Numero massimo di token per chunk (900 per sicurezza, BART supporta ~1024)
        overlap: Numero di token di sovrapposizione tra chunk (per mantenere contesto)
        preserve_sentences: Se True, cerca di non spezzare le frasi
        
    Returns:
        Lista di chunk di testo
    """
    if not text or not text.strip():
        return []
    
    # Stima approssimativa: 1 token ≈ 4 caratteri per l'inglese
    # Per essere sicuri usiamo 3 caratteri per token
    chars_per_token = 3
    max_chars = max_tokens * chars_per_token
    overlap_chars = overlap * chars_per_token
    
    if len(text) <= max_chars:
        # Il testo è abbastanza corto, restituisce un singolo chunk
        return [text]
    
    chunks = []
    
    if preserve_sentences:
        # Divide per frasi
        sentences = split_into_sentences(text)
        chunks = _chunk_by_sentences(sentences, max_chars, overlap_chars)
    else:
        # Divide per caratteri con overlap
        chunks = _chunk_by_chars(text, max_chars, overlap_chars)
    
    return chunks


def split_into_sentences(text: str) -> List[str]:
    """
    Divide il testo in frasi.
    Gestisce abbreviazioni comuni e casi edge.
    """
    # Pattern per split delle frasi (gestisce abbreviazioni comuni)
    sentence_endings = r'(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?|\!)\s'
    sentences = re.split(sentence_endings, text)
    
    # Rimuove frasi vuote
    sentences = [s.strip() for s in sentences if s.strip()]
    
    return sentences


def _chunk_by_sentences(
    sentences: List[str], 
    max_chars: int, 
    overlap_chars: int
) -> List[str]:
    """
    Crea chunk preservando le frasi intere.
    """
    chunks = []
    current_chunk = []
    current_length = 0
    
    for sentence in sentences:
        sentence_length = len(sentence)
        
        # Se una singola frase è troppo lunga, la divide
        if sentence_length > max_chars:
            # Salva il chunk corrente se non vuoto
            if current_chunk:
                chunks.append(" ".join(current_chunk))
                current_chunk = []
                current_length = 0
            
            # Divide la frase lunga
            sub_chunks = _chunk_by_chars(sentence, max_chars, overlap_chars)
            chunks.extend(sub_chunks)
            continue
        
        # Se aggiungere questa frase supera il limite
        if current_length + sentence_length > max_chars:
            # Salva il chunk corrente
            chunks.append(" ".join(current_chunk))
            
            # Inizia un nuovo chunk con overlap
            # Trova le ultime frasi che rientrano nell'overlap
            overlap_chunk = []
            overlap_length = 0
            for prev_sentence in reversed(current_chunk):
                if overlap_length + len(prev_sentence) <= overlap_chars:
                    overlap_chunk.insert(0, prev_sentence)
                    overlap_length += len(prev_sentence)
                else:
                    break
            
            current_chunk = overlap_chunk
            current_length = overlap_length
        
        # Aggiunge la frase al chunk corrente
        current_chunk.append(sentence)
        current_length += sentence_length
    
    # Aggiunge l'ultimo chunk
    if current_chunk:
        chunks.append(" ".join(current_chunk))
    
    return chunks


def _chunk_by_chars(text: str, max_chars: int, overlap_chars: int) -> List[str]:
    """
    Divide il testo per numero di caratteri con overlap.
    Usato come fallback quando preserve_sentences=False.
    """
    chunks = []
    start = 0
    text_length = len(text)
    
    while start < text_length:
        end = start + max_chars
        
        # Se non siamo alla fine, cerca uno spazio per non spezzare parole
        if end < text_length:
            # Cerca lo spazio più vicino prima del limite
            space_pos = text.rfind(' ', start, end)
            if space_pos > start:
                end = space_pos
        
        chunk = text[start:end].strip()
        if chunk:
            chunks.append(chunk)
        
        # Move start forward, accounting for overlap
        start = end - overlap_chars
    
    return chunks


def estimate_token_count(text: str) -> int:
    """
    Stima il numero di token nel testo.
    Approssimazione rapida senza usare il tokenizer.
    """
    # Stima: split su whitespace + punteggiatura
    words = len(text.split())
    # Aggiungi token per punteggiatura
    punctuation = len(re.findall(r'[.,!?;:\-"]', text))
    
    return words + punctuation
