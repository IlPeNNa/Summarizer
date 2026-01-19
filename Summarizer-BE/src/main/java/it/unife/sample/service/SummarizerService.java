package it.unife.sample.service;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationResponse;
import org.springframework.stereotype.Service;

/**
 * Service layer per la logica di business della summarization.
 * Utilizza il NlpServiceClient per comunicare con il servizio Python.
 */
@Service
public class SummarizerService {
    
    private final NlpServiceClient nlpServiceClient;
    
    public SummarizerService(NlpServiceClient nlpServiceClient) {
        this.nlpServiceClient = nlpServiceClient;
    }
    
    /**
     * Riassume un testo utilizzando il servizio NLP.
     * 
     * @param text Testo da riassumere
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeText(String text) throws NlpServiceClient.NlpServiceException {
        validateText(text);
        return nlpServiceClient.summarize(text);
    }
    
    /**
     * Riassume un testo con parametri personalizzati.
     * 
     * @param text Testo da riassumere
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeText(String text, int maxLength, int minLength) 
            throws NlpServiceClient.NlpServiceException {
        validateText(text);
        validateLengthParameters(maxLength, minLength);
        return nlpServiceClient.summarize(text, maxLength, minLength);
    }
    
    /**
     * Verifica se il servizio NLP è disponibile.
     * 
     * @return true se disponibile, false altrimenti
     */
    public boolean isNlpServiceAvailable() {
        return nlpServiceClient.isHealthy();
    }
    
    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo non può essere vuoto");
        }
        
        if (text.length() < 100) {
            throw new IllegalArgumentException("Il testo è troppo corto per essere riassunto (minimo 100 caratteri)");
        }
    }
    
    private void validateLengthParameters(int maxLength, int minLength) {
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength deve essere maggiore di minLength");
        }
        
        if (minLength < 10) {
            throw new IllegalArgumentException("minLength deve essere almeno 10");
        }
        
        if (maxLength > 500) {
            throw new IllegalArgumentException("maxLength non può superare 500");
        }
    }
}
