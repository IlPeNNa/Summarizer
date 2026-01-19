"""
Estrattore di testo da file di testo semplice.
"""
from typing import Optional


def extract_from_txt(file_content: bytes, encoding: str = 'utf-8') -> str:
    """
    Estrae il testo da un file di testo.
    
    Args:
        file_content: Contenuto binario del file
        encoding: Encoding del testo (default: utf-8)
        
    Returns:
        Testo estratto
    """
    try:
        text = file_content.decode(encoding)
        return text
    except UnicodeDecodeError:
        # Prova con encoding alternativi
        for alt_encoding in ['latin-1', 'cp1252', 'iso-8859-1']:
            try:
                text = file_content.decode(alt_encoding)
                return text
            except UnicodeDecodeError:
                continue
        
        raise Exception("Impossibile decodificare il file di testo")


def extract_from_txt_file(file_path: str, encoding: str = 'utf-8') -> str:
    """
    Estrae il testo da un file TXT dato il percorso.
    
    Args:
        file_path: Percorso del file
        encoding: Encoding del file (default: utf-8)
        
    Returns:
        Testo estratto dal file
    """
    try:
        with open(file_path, 'rb') as file:
            return extract_from_txt(file.read(), encoding)
    except FileNotFoundError:
        raise Exception(f"File non trovato: {file_path}")
    except Exception as e:
        raise Exception(f"Errore nella lettura del file: {str(e)}")
