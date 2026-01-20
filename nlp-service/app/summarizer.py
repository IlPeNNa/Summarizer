"""
Il cuore del servizio NLP.
Carica il modello it5-summarization per riassunti in italiano.
"""
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
import torch
import re


class Summarizer:
    """
    Wrapper per il modello it5-summarization ottimizzato per l'italiano.
    """
    
    def __init__(self, model_name: str = "ARTeLab/it5-summarization-mlsum"):
        """
        Inizializza il modello it5-summarization.
        
        Args:
            model_name: Nome del modello su Hugging Face
        """
        print(f"Caricamento modello {model_name}...")
        
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"Utilizzo device: {self.device}")
        
        self.model_name = model_name
        
        # Carica tokenizer e modello
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForSeq2SeqLM.from_pretrained(model_name)
        self.model.to(self.device)
        self.model.eval()  # Modalità inferenza
        
        # Limite di token per it5
        self.max_input_length = 512
        
        print(f"✓ Modello {model_name} caricato con successo!")
    
    
    def _extract_acronyms(self, text: str) -> set:
        """
        Estrae acronimi (parole tutte maiuscole di 2-5 lettere) dal testo.
        """
        # Trova parole di 2-5 lettere tutte maiuscole
        acronyms = re.findall(r'\b[A-Z]{2,5}\b', text)
        return set(acronyms)
    
    
    def _fix_capitalization(self, summary: str, original_text: str) -> str:
        """
        Corregge capitalizzazione e acronimi nel riassunto.
        
        Args:
            summary: Riassunto generato dal modello
            original_text: Testo originale da cui estrarre acronimi
            
        Returns:
            Riassunto con capitalizzazione corretta
        """
        # 1. Capitalizza la prima lettera
        if summary:
            summary = summary[0].upper() + summary[1:] if len(summary) > 1 else summary.upper()
        
        # 2. Trova acronimi nel testo originale
        acronyms = self._extract_acronyms(original_text)
        
        # 3. Sostituisci acronimi in minuscolo con versione maiuscola
        for acronym in acronyms:
            # Usa word boundary per non sostituire acronimi dentro altre parole
            pattern = r'\b' + re.escape(acronym.lower()) + r'\b'
            summary = re.sub(pattern, acronym, summary, flags=re.IGNORECASE)
        
        # 4. Aggiungi punto finale se manca
        if summary and summary[-1] not in '.!?':
            summary += '.'
        
        return summary
    
    
    def summarize(self, text: str, max_length: int = 150, min_length: int = 50) -> str:
        """
        Genera un riassunto del testo in italiano.
        
        Args:
            text: Testo da riassumere
            max_length: Lunghezza massima del riassunto (in token)
            min_length: Lunghezza minima del riassunto (in token)
            
        Returns:
            Testo riassunto
        """
        # Tokenizza l'input (it5-summarization non richiede prefisso "summarize:")
        inputs = self.tokenizer(
            text,
            max_length=self.max_input_length,
            truncation=True,
            return_tensors="pt",
            padding=True
        ).to(self.device)
        
        # Genera il riassunto
        with torch.no_grad():
            summary_ids = self.model.generate(
                inputs["input_ids"],
                max_length=max_length,
                min_length=min_length,
                num_beams=4,
                length_penalty=2.5,
                early_stopping=True,
                no_repeat_ngram_size=3
            )
        
        # Decodifica il riassunto
        summary = self.tokenizer.decode(
            summary_ids[0], 
            skip_special_tokens=True
        )
        
        # Post-processing: correggi capitalizzazione e acronimi
        summary = self._fix_capitalization(summary.strip(), text)
        
        return summary
    
    
    def get_token_count(self, text: str) -> int:
        """
        Restituisce il numero di token nel testo.
        """
        tokens = self.tokenizer.encode(text)
        return len(tokens)
