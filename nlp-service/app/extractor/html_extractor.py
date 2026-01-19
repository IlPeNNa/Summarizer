"""
Estrattore di testo da file HTML e pagine web.
"""
from typing import Optional
from bs4 import BeautifulSoup


def extract_from_html(html_content: str) -> str:
    """
    Estrae il testo da contenuto HTML.
    
    Args:
        html_content: Contenuto HTML come stringa
        
    Returns:
        Testo estratto dall'HTML (senza tag)
    """
    try:
        soup = BeautifulSoup(html_content, 'html.parser')
        
        # Rimuove script, style e altri elementi non testuali
        for element in soup(['script', 'style', 'nav', 'footer', 'header', 'aside']):
            element.decompose()
        
        # Estrae il testo
        text = soup.get_text(separator='\n', strip=True)
        
        # Pulisce linee vuote multiple
        lines = [line.strip() for line in text.split('\n') if line.strip()]
        text = '\n'.join(lines)
        
        return text
    
    except Exception as e:
        raise Exception(f"Errore nell'estrazione dell'HTML: {str(e)}")


def extract_from_html_file(file_path: str, encoding: str = 'utf-8') -> str:
    """
    Estrae il testo da un file HTML dato il percorso.
    
    Args:
        file_path: Percorso del file HTML
        encoding: Encoding del file (default: utf-8)
        
    Returns:
        Testo estratto dall'HTML
    """
    try:
        with open(file_path, 'r', encoding=encoding) as file:
            return extract_from_html(file.read())
    except FileNotFoundError:
        raise Exception(f"File HTML non trovato: {file_path}")
    except Exception as e:
        raise Exception(f"Errore nella lettura del file HTML: {str(e)}")


def extract_from_url(url: str) -> str:
    """
    Estrae il testo da una URL web.
    
    Args:
        url: URL della pagina web
        
    Returns:
        Testo estratto dalla pagina
    """
    import requests
    
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        
        return extract_from_html(response.text)
    
    except requests.RequestException as e:
        raise Exception(f"Errore nel fetch della URL: {str(e)}")
