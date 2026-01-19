"""
Il cuore del servizio NLP.
Carica il modello BART una sola volta e genera i riassunti.
"""
from transformers import MBartForConditionalGeneration, MBart50TokenizerFast
import torch


class BARTSummarizer:
    """
    Wrapper per il modello BART di Facebook per la summarization.
    Carica il modello una sola volta all'inizializzazione.
    """
    
    def __init__(self, model_name: str = "facebook/mbart-large-50-many-to-many-mmt"):
        """
        Inizializza il modello mBART multilingua.
        
        Args:
            model_name: Nome del modello su Hugging Face
                       - facebook/mbart-large-50-many-to-many-mmt (multilingua, supporta italiano)
                       - facebook/bart-large-cnn (solo inglese)
        """
        print(f"Caricamento modello {model_name}...")
        
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"Utilizzo device: {self.device}")
        
        self.model_name = model_name
        
        # Carica tokenizer e modello per mBART
        self.tokenizer = MBart50TokenizerFast.from_pretrained(model_name)
        self.model = MBartForConditionalGeneration.from_pretrained(model_name)
        self.model.to(self.device)
        self.model.eval()  # Modalità inferenza
        
        # Lingua di default: italiano
        self.tokenizer.src_lang = "it_IT"
        self.tokenizer.tgt_lang = "it_IT"
        
        # Limite di token per mBART (circa 1024)
        self.max_input_length = 1024
        
        print(f"Modello {model_name} caricato con successo!")
        print(f"Lingua configurata: italiano (it_IT)")
    
    
    def summarize(
        self, 
        text: str, 
        max_length: int = 150, 
        min_length: int = 50,
        num_beams: int = 4,
        length_penalty: float = 2.0,
        early_stopping: bool = True
    ) -> str:
        """
        Genera un riassunto del testo fornito.
        
        Args:
            text: Testo da riassumere
            max_length: Lunghezza massima del riassunto (in token)
            min_length: Lunghezza minima del riassunto (in token)
            num_beams: Numero di beam per beam search
            length_penalty: Penalità per la lunghezza (>1 preferisce riassunti più lunghi)
            early_stopping: Termina quando tutti i beam hanno generato EOS
            
        Returns:
            Testo riassunto
        """
        # Tokenizza l'input
        inputs = self.tokenizer(
            text,
            max_length=self.max_input_length,
            truncation=True,
            return_tensors="pt"
        ).to(self.device)
        
        # Genera il riassunto
        with torch.no_grad():
            summary_ids = self.model.generate(
                inputs["input_ids"],
                max_length=max_length,
                min_length=min_length,
                num_beams=num_beams,
                length_penalty=length_penalty,
                early_stopping=early_stopping
            )
        
        # Decodifica il riassunto
        summary = self.tokenizer.decode(
            summary_ids[0], 
            skip_special_tokens=True
        )
        
        return summary
    
    
    def get_token_count(self, text: str) -> int:
        """
        Restituisce il numero di token nel testo.
        Utile per verificare se il testo supera il limite.
        """
        tokens = self.tokenizer.encode(text)
        return len(tokens)
