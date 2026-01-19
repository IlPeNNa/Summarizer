package it.unife.sample.controller;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import it.unife.sample.service.SummarizerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller per gli endpoint di summarization.
 * Espone le API per il frontend Angular.
 */
@RestController
@RequestMapping("/api/summarize")
@CrossOrigin(origins = "http://localhost:4200")
public class SummarizerController {
    
    private final SummarizerService summarizerService;
    
    public SummarizerController(SummarizerService summarizerService) {
        this.summarizerService = summarizerService;
    }
    
    /**
     * Endpoint per riassumere un testo.
     * POST /api/summarize
     */
    @PostMapping
    public ResponseEntity<?> summarize(@RequestBody SummarizationRequest request) {
        try {
            // Parametri di default se non specificati
            int maxLength = request.getMaxLength() != null ? request.getMaxLength() : 150;
            int minLength = request.getMinLength() != null ? request.getMinLength() : 50;
            
            SummarizationResponse response = summarizerService.summarizeText(
                    request.getText(), 
                    maxLength, 
                    minLength
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
            
        } catch (NlpServiceClient.NlpServiceException e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Servizio NLP non disponibile: " + e.getMessage()));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno: " + e.getMessage()));
        }
    }
    
    /**
     * Health check per verificare se il servizio NLP è disponibile.
     * GET /api/summarize/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        boolean isHealthy = summarizerService.isNlpServiceAvailable();
        
        if (isHealthy) {
            return ResponseEntity.ok(Map.of(
                    "status", "healthy",
                    "nlpService", "available"
            ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "status", "unhealthy",
                            "nlpService", "unavailable"
                    ));
        }
    }
}
