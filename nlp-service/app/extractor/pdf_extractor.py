"""
Estrattore di testo da file PDF.
"""
from typing import Optional
import PyPDF2
from io import BytesIO


def extract_from_pdf(file_content: bytes) -> str:
    """
    Estrae il testo da un file PDF.
    
    Args:
        file_content: Contenuto binario del file PDF
        
    Returns:
        Testo estratto dal PDF
    """
    text_parts = []
    
    try:
        pdf_file = BytesIO(file_content)
        pdf_reader = PyPDF2.PdfReader(pdf_file)
        
        # Estrae il testo da ogni pagina
        for page_num in range(len(pdf_reader.pages)):
            page = pdf_reader.pages[page_num]
            text = page.extract_text()
            
            if text:
                text_parts.append(text)
        
        # Combina il testo di tutte le pagine
        full_text = "\n\n".join(text_parts)
        
        return full_text
    
    except Exception as e:
        raise Exception(f"Errore nell'estrazione del PDF: {str(e)}")


def extract_from_pdf_file(file_path: str) -> str:
    """
    Estrae il testo da un file PDF dato il percorso.
    
    Args:
        file_path: Percorso del file PDF
        
    Returns:
        Testo estratto dal PDF
    """
    try:
        with open(file_path, 'rb') as file:
            return extract_from_pdf(file.read())
    except FileNotFoundError:
        raise Exception(f"File PDF non trovato: {file_path}")
    except Exception as e:
        raise Exception(f"Errore nella lettura del file PDF: {str(e)}")
