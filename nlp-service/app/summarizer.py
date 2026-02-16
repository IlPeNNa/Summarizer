""" 
Il cuore del servizio NLP.
Carica modelli specializzati per riassunti: it5 per italiano, BART-CNN per inglese.
"""
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
import torch
import re
from langdetect import detect, LangDetectException


class Summarizer:
    """
    Wrapper per modelli di summarization multilingua.
    - it5 per testi in italiano
    - BART-CNN per testi in inglese
    """
    
    def __init__(self, 
                 italian_model: str = "ARTeLab/it5-summarization-mlsum",
                 english_model: str = "facebook/bart-large-cnn"):
        """
        Inizializza i modelli di summarization.
        Utilizza lazy loading: carica i modelli solo quando necessari.
        
        Args:
            italian_model: Modello per testi in italiano
            english_model: Modello per testi in inglese
        """
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"Utilizzo device: {self.device}")
        
        # Nomi dei modelli
        self.italian_model_name = italian_model
        self.english_model_name = english_model
        
        # Modelli non ancora caricati (lazy loading)
        self.italian_model = None
        self.italian_tokenizer = None
        self.english_model = None
        self.english_tokenizer = None
        
        # Limiti di token per modello
        self.italian_max_input_length = 512   # IT5 max input
        self.english_max_input_length = 1024  # BART max input
        
        print("✓ Summarizer inizializzato (modelli caricabili al bisogno)")
    
    
    def _extract_acronyms(self, text: str) -> set:
        """
        Estrae acronimi (parole tutte maiuscole di 2+ lettere) dal testo.
        """
        # Trova parole di 2 o più lettere tutte maiuscole
        acronyms = re.findall(r'\b[A-Z]{2,}\b', text)
        return set(acronyms)
    
    
    def _extract_proper_nouns(self, text: str) -> set:
        """
        Estrae nomi propri (parole con iniziale maiuscola a metà frase) dal testo.
        """
        proper_nouns = set()
        
        # Trova parole che iniziano con maiuscola in mezzo a una frase
        # Pattern: non all'inizio della stringa e non dopo .!?
        # Usa lookbehind negativo per escludere inizio stringa e dopo punteggiatura+spazio
        words = re.findall(r'(?<![.!?]\s)(?<!^)\b([A-Z][a-z]+)\b', text)
        
        # Filtra parole troppo comuni che potrebbero essere false positive
        # (es. parole comuni che appaiono dopo virgola)
        common_words = {'Il', 'La', 'Le', 'Lo', 'Gli', 'Un', 'Una', 'I', 'E', 'O', 'Ma', 'Poi', 'Ora', 'Già', 'Che'}
        proper_nouns = {word for word in words if word not in common_words}
        
        return proper_nouns
    
    
    def _fix_capitalization(self, summary: str, original_text: str) -> str:
        """
        Corregge capitalizzazione e acronimi nel riassunto.
        
        Args:
            summary: Riassunto generato dal modello
            original_text: Testo originale da cui estrarre acronimi e nomi propri
            
        Returns:
            Riassunto con capitalizzazione corretta
        """
        # 1. Capitalizza la prima lettera
        if summary:
            summary = summary[0].upper() + summary[1:] if len(summary) > 1 else summary.upper()
        
        # 2. Capitalizza dopo .!?
        # Pattern: trova .!? seguiti da spazio e lettera minuscola
        summary = re.sub(r'([.!?])\s+([a-z])', lambda m: m.group(1) + ' ' + m.group(2).upper(), summary)
        
        # 3. Trova acronimi nel testo originale
        acronyms = self._extract_acronyms(original_text)
        
        # 4. Sostituisci acronimi in minuscolo con versione maiuscola
        for acronym in acronyms:
            # Usa word boundary per non sostituire acronimi dentro altre parole
            pattern = r'\b' + re.escape(acronym.lower()) + r'\b'
            summary = re.sub(pattern, acronym, summary, flags=re.IGNORECASE)
        
        # 5. Trova e preserva nomi propri dal testo originale
        proper_nouns = self._extract_proper_nouns(original_text)
        
        # 6. Sostituisci nomi propri in minuscolo con versione capitalizzata
        for noun in proper_nouns:
            # Usa word boundary per non sostituire nomi dentro altre parole
            pattern = r'\b' + re.escape(noun.lower()) + r'\b'
            summary = re.sub(pattern, noun, summary, flags=re.IGNORECASE)
        
        # 7. Aggiungi punto finale se manca
        if summary and summary[-1] not in '.!?':
            summary += '.'
        
        return summary
    
    
    def _detect_language(self, text: str) -> str:
        """
        Rileva la lingua del testo.
        
        Args:
            text: Testo da analizzare
            
        Returns:
            Codice lingua ISO 639-1 (es: 'it', 'en', 'fr')
        """
        try:
            # Usa un campione più grande per migliorare l'accuratezza
            sample = text[:1000] if len(text) > 1000 else text
            
            # langdetect restituisce codice ISO 639-1
            lang = detect(sample)
            print(f"Lingua rilevata: {lang}")
            
            # Se il testo è molto corto e rileva inglese, prova a rilevare di nuovo
            if lang == 'en' and len(text) < 100:
                print("Testo breve rilevato come inglese, uso italiano come default")
                return "it"
            
            return lang
        except LangDetectException:
            # Se non riesce a rilevare, default italiano (contesto dell'app)
            print("Impossibile rilevare lingua, uso italiano come default")
            return "it"
    
    
    def _load_italian_model(self):
        """
        Carica il modello italiano se non ancora caricato.
        """
        if self.italian_model is None:
            print(f"Caricamento modello italiano {self.italian_model_name}...")
            self.italian_tokenizer = AutoTokenizer.from_pretrained(self.italian_model_name)
            self.italian_model = AutoModelForSeq2SeqLM.from_pretrained(
                self.italian_model_name,
                dtype=torch.float32
            )
            self.italian_model.to(self.device)
            self.italian_model.eval()
            print(f"✓ Modello italiano caricato con successo!")
    
    
    def _load_english_model(self):
        """
        Carica il modello inglese (BART-CNN) se non ancora caricato.
        """
        if self.english_model is None:
            try:
                print(f"Caricamento modello inglese {self.english_model_name}...")
                self.english_tokenizer = AutoTokenizer.from_pretrained(self.english_model_name)
                self.english_model = AutoModelForSeq2SeqLM.from_pretrained(
                    self.english_model_name,
                    dtype=torch.float32
                )
                self.english_model.to(self.device)
                self.english_model.eval()
                print(f"✓ Modello inglese caricato con successo!")
            except Exception as e:
                print(f"❌ Errore nel caricamento del modello inglese: {e}")
                raise RuntimeError(f"Impossibile caricare il modello inglese: {e}")
    
    
    def format_as_bullets(self, text: str) -> str:
        """
        Formatta il testo come elenco puntato, dividendo in frasi.
        """
        # Dividi in frasi usando punti, punti interrogativi, ecc.
        sentences = re.split(r'(?<=[.!?])\s+', text.strip())
        
        # Rimuovi frasi vuote e troppo corte
        sentences = [s.strip() for s in sentences if len(s.strip()) > 10]
        
        if not sentences:
            return text
        
        # Formatta come bullet points
        bullet_points = [f"• {sentence}" for sentence in sentences]
        return "\n".join(bullet_points)
    
    
    def summarize(self, text: str, max_length: int = 150, min_length: int = 50, language: str = None) -> str:
        """
        Genera un riassunto del testo.
        Rileva automaticamente la lingua se non fornita.
        
        Args:
            text: Testo da riassumere
            max_length: Lunghezza massima del riassunto (in token)
            min_length: Lunghezza minima del riassunto (in token)
            language: Lingua del testo (opzionale, auto-rilevata se non fornita)
            
        Returns:
            Testo riassunto
        """
        # 1. Rileva la lingua del testo (solo se non già fornita)
        if language is None:
            language = self._detect_language(text)
        
        # 2. Seleziona il modello appropriato e il suo limite di token
        if language == 'it':
            # Usa modello italiano
            self._load_italian_model()
            model = self.italian_model
            tokenizer = self.italian_tokenizer
            max_input_length = self.italian_max_input_length
            print("Uso modello italiano (it5) - max 512 token")
        elif language == 'en':
            # Usa modello inglese
            self._load_english_model()
            model = self.english_model
            tokenizer = self.english_tokenizer
            max_input_length = self.english_max_input_length
            print("Uso modello inglese (BART-CNN) - max 1024 token")
        else:
            # Per altre lingue, usa il modello italiano come fallback
            print(f"Lingua {language} non supportata, uso modello italiano come fallback")
            self._load_italian_model()
            model = self.italian_model
            tokenizer = self.italian_tokenizer
            max_input_length = self.italian_max_input_length
        
        # 3. Tokenizza l'input con il limite corretto per il modello
        inputs = tokenizer(
            text,
            max_length=max_input_length,
            truncation=True,
            return_tensors="pt",
            padding=True
        ).to(self.device)
        
        # 4. Genera il riassunto
        with torch.no_grad():
            summary_ids = model.generate(
                inputs["input_ids"],
                max_length=max_length,
                min_length=min_length,
                num_beams=4,
                length_penalty=2.5,
                early_stopping=True,
                no_repeat_ngram_size=3
            )
        
        # 5. Decodifica il riassunto
        summary = tokenizer.decode(
            summary_ids[0], 
            skip_special_tokens=True
        )
        
        # 6. Post-processing: correggi capitalizzazione e acronimi
        summary = self._fix_capitalization(summary.strip(), text)
        
        return summary
    
    
    def get_token_count(self, text: str, language: str = 'it') -> int:
        """
        Restituisce il numero di token nel testo.
        
        Args:
            text: Testo da contare
            language: Lingua del testo ('it' o 'en')
        """
        if language == 'en':
            self._load_english_model()
            tokens = self.english_tokenizer.encode(text)
        else:
            self._load_italian_model()
            tokens = self.italian_tokenizer.encode(text)
        return len(tokens)
    
    
    def get_max_input_length(self, language: str = 'it') -> int:
        """
        Restituisce il limite massimo di token per il modello della lingua specificata.
        
        Args:
            language: Lingua del modello ('it' o 'en')
            
        Returns:
            Numero massimo di token (512 per IT5, 1024 per BART)
        """
        return self.english_max_input_length if language == 'en' else self.italian_max_input_length
