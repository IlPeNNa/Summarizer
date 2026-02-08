package it.unife.sample.client;

import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client per comunicare con il servizio NLP Python (black box).
 * Wrapper Java che nasconde i dettagli dell'implementazione del servizio NLP.
 */
@Component
public class NlpServiceClient {
    
    private static final Logger log = LoggerFactory.getLogger(NlpServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String nlpServiceUrl;
    
    public NlpServiceClient(@Value("${nlp.service.url:http://localhost:8000}") String nlpServiceUrl) {
        this.nlpServiceUrl = nlpServiceUrl;
        this.restTemplate = new RestTemplate();
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
        return summarize(text, maxLength, minLength, "paragraph");
    }
    
    /**
     * Invia un testo al servizio NLP per la summarization con parametri personalizzati e formato.
     * 
     * @param text Testo da riassumere
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @param format Formato del riassunto (paragraph, bullet)
     * @return Risposta con il riassunto
     * @throws NlpServiceException se il servizio NLP non è disponibile o restituisce un errore
     */
    public SummarizationResponse summarize(String text, int maxLength, int minLength, String format) throws NlpServiceException {
        try {
            SummarizationRequest request = new SummarizationRequest(text, maxLength, minLength, format);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SummarizationRequest> entity = new HttpEntity<>(request, headers);
            
            log.debug("Invio richiesta NLP - Input length: {}, maxLength: {}, minLength: {}, format: {}", 
                text.length(), maxLength, minLength, format);
            
            ResponseEntity<SummarizationResponse> response = restTemplate.exchange(
                    nlpServiceUrl + "/summarize",
                    HttpMethod.POST,
                    entity,
                    SummarizationResponse.class
            );
            
            if (response.getBody() == null) {
                throw new NlpServiceException("Risposta nulla dal servizio NLP");
            }
            
            log.debug("Risposta NLP ricevuta con successo");
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Errore comunicazione con servizio NLP: {}", e.getMessage());
            throw new NlpServiceException("Errore nella comunicazione con il servizio NLP: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Errore imprevisto: {}", e.getMessage(), e);
            throw new NlpServiceException("Errore imprevisto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica se il servizio NLP è disponibile.
     * 
     * @return true se il servizio è raggiungibile, false altrimenti
     */
    public boolean isHealthy() {
        try {
            restTemplate.getForEntity(nlpServiceUrl + "/health", String.class);
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
