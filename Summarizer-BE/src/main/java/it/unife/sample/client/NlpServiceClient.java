package it.unife.sample.client;

import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;

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
     * Estrae il testo da un file senza riassumerlo.
     * 
     * @param fileContent Contenuto del file come array di byte
     * @param filename Nome del file (utilizzato per determinare il tipo)
     * @return Testo estratto dal file
     * @throws NlpServiceException se il servizio NLP non è disponibile o restituisce un errore
     */
    public String extractTextFromFile(byte[] fileContent, String filename) throws NlpServiceException {
        try {
            // Crea una risorsa ByteArray con override del nome file
            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            
            // Prepara il multipart form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            
            log.debug("Richiesta estrazione testo - Filename: {}, Size: {} bytes", filename, fileContent.length);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<java.util.Map> response = restTemplate.exchange(
                    nlpServiceUrl + "/extract/file",
                    HttpMethod.POST,
                    entity,
                    java.util.Map.class
            );
            
            if (response.getBody() == null || !response.getBody().containsKey("text")) {
                throw new NlpServiceException("Risposta non valida dal servizio NLP");
            }
            
            String extractedText = (String) response.getBody().get("text");
            log.debug("Testo estratto con successo dal file: {} ({} caratteri)", filename, extractedText.length());
            
            return extractedText;
            
        } catch (RestClientException e) {
            log.error("Errore comunicazione con servizio NLP per estrazione file {}: {}", filename, e.getMessage());
            throw new NlpServiceException("Errore nella comunicazione con il servizio NLP: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Errore imprevisto durante estrazione file {}: {}", filename, e.getMessage(), e);
            throw new NlpServiceException("Errore imprevisto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Invia un file al servizio NLP per l'estrazione del testo e la summarization.
     * 
     * @param fileContent Contenuto del file come array di byte
     * @param filename Nome del file (utilizzato per determinare il tipo)
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @param format Formato del riassunto (paragraph, bullet)
     * @return Risposta con il riassunto
     * @throws NlpServiceException se il servizio NLP non è disponibile o restituisce un errore
     */
    public SummarizationResponse summarizeFile(byte[] fileContent, String filename, 
                                                int maxLength, int minLength, String format) 
            throws NlpServiceException {
        try {
            // Crea una risorsa ByteArray con override del nome file
            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            
            // Prepara il multipart form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("maxLength", maxLength);
            body.add("minLength", minLength);
            body.add("format", format);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            
            log.debug("Invio file al servizio NLP - Filename: {}, Size: {} bytes, maxLength: {}, minLength: {}", 
                filename, fileContent.length, maxLength, minLength);
            
            ResponseEntity<SummarizationResponse> response = restTemplate.exchange(
                    nlpServiceUrl + "/summarize/file",
                    HttpMethod.POST,
                    entity,
                    SummarizationResponse.class
            );
            
            if (response.getBody() == null) {
                throw new NlpServiceException("Risposta nulla dal servizio NLP");
            }
            
            log.debug("Risposta NLP ricevuta con successo per il file: {}", filename);
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Errore comunicazione con servizio NLP per file {}: {}", filename, e.getMessage());
            throw new NlpServiceException("Errore nella comunicazione con il servizio NLP: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Errore imprevisto durante elaborazione file {}: {}", filename, e.getMessage(), e);
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
