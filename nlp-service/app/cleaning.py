"""
Pulizia e normalizzazione del testo prima della summarization.
"""
import re
from typing import Optional


def clean_text(text: str) -> str:
    """
    Pulisce e normalizza il testo prima della summarization.
    
    Args:
        text: Testo grezzo da pulire
        
    Returns:
        Testo pulito e normalizzato
    """
    if not text or not text.strip():
        return ""
    
    # Rimuove caratteri di controllo e caratteri invisibili
    text = ''.join(char for char in text if char.isprintable() or char in '\n\t ')
    
    # Normalizza gli spazi bianchi multipli
    text = re.sub(r'\s+', ' ', text)
    
    # Normalizza le newline multiple
    text = re.sub(r'\n\s*\n', '\n\n', text)
    
    # Rimuove spazi prima della punteggiatura
    text = re.sub(r'\s+([.,!?;:])', r'\1', text)
    
    # Rimuove URL (opzionale)
    text = re.sub(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', '', text)
    
    # Rimuove email (opzionale)
    text = re.sub(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b', '', text)
    
    # Rimuove caratteri speciali ripetuti
    text = re.sub(r'([!?.])\1+', r'\1', text)
    
    # Trim spazi iniziali e finali
    text = text.strip()
    
    return text


def remove_headers_footers(text: str) -> str:
    """
    Rimuove header e footer comuni nei documenti.
    Utile per PDF e documenti formattati.
    """
    # Rimuove pattern comuni di header/footer (numeri di pagina, ecc.)
    text = re.sub(r'^\s*Page\s+\d+\s*$', '', text, flags=re.MULTILINE | re.IGNORECASE)
    text = re.sub(r'^\s*\d+\s*/\s*\d+\s*$', '', text, flags=re.MULTILINE)
    
    return text


def normalize_quotes(text: str) -> str:
    """
    Normalizza i vari tipi di virgolette in virgolette standard.
    """
    # Virgolette curve -> virgolette dritte
    text = text.replace('"', '"').replace('"', '"')
    text = text.replace(''', "'").replace(''', "'")
    
    return text


def remove_excessive_punctuation(text: str) -> str:
    """
    Rimuove punteggiatura eccessiva o non necessaria.
    """
    # Rimuove punteggiatura multipla
    text = re.sub(r'([.,!?;:]){2,}', r'\1', text)
    
    return text
