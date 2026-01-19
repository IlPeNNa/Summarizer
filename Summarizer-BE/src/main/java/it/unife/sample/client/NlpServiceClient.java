package it.unife.sample.client;

import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Client per comunicare con il servizio NLP Python (black box).
 * Wrapper Java che nasconde i dettagli dell'implementazione del servizio NLP.
 */
@Component
public class NlpServiceClient {
    
    private final RestClient restClient;
    private final String nlpServiceUrl;
    
    public NlpServiceClient(@Value("${nlp.service.url:http://localhost:8000}") String nlpServiceUrl) {
        this.nlpServiceUrl = nlpServiceUrl;
        this.restClient = RestClient.builder()
                .baseUrl(nlpServiceUrl)
                .build();
    }
    
    /**
     * Invia un testo al servizio NLP per la summarization.
     * 
     * @param text Testo da riassumere
     * @return Risposta con il riassunto
     * @throws NlpServiceException se il servizio NLP non è disponibile o restituisce un errore
     */
    public SummarizationResponse summarize(String text) throws NlpServiceException {
        return summarize(text, 150, 50);
    }
    
    /**
     * Invia un testo al servizio NLP per la summarization con parametri personalizzati.
     * 
     * @param text Testo da riassumere
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @return Risposta con il riassunto
     * @throws NlpServiceException se il servizio NLP non è disponibile o restituisce un errore
     */
    public SummarizationResponse summarize(String text, int maxLength, int minLength) throws NlpServiceException {
        try {
            SummarizationRequest request = new SummarizationRequest(text, maxLength, minLength);
            
            SummarizationResponse response = restClient.post()
                    .uri("/summarize")
                    .body(request)
                    .retrieve()
                    .body(SummarizationResponse.class);
            
            if (response == null) {
                throw new NlpServiceException("Risposta nulla dal servizio NLP");
            }
            
            return response;
            
        } catch (RestClientException e) {
            throw new NlpServiceException("Errore nella comunicazione con il servizio NLP: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica se il servizio NLP è disponibile.
     * 
     * @return true se il servizio è raggiungibile, false altrimenti
     */
    public boolean isHealthy() {
        try {
            restClient.get()
                    .uri("/health")
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Eccezione personalizzata per errori del servizio NLP.
     */
    public static class NlpServiceException extends Exception {
        public NlpServiceException(String message) {
            super(message);
        }
        
        public NlpServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
