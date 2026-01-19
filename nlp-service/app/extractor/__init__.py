"""
Modulo per l'estrazione di testo da vari formati di file.
"""
from .pdf_extractor import extract_from_pdf
from .docx_extractor import extract_from_docx
from .html_extractor import extract_from_html
from .txt_extractor import extract_from_txt

__all__ = [
    'extract_from_pdf',
    'extract_from_docx', 
    'extract_from_html',
    'extract_from_txt'
]
