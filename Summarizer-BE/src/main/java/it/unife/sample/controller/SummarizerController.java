package it.unife.sample.controller;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import it.unife.sample.service.SummarizerService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                    request.getInput(), 
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
     * Upload file e riassumi contenuto.
     * POST /api/summarize/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> summarizeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "maxLength", defaultValue = "150") int maxLength,
            @RequestParam(value = "minLength", defaultValue = "50") int minLength) {
        try {
            // Leggi il contenuto del file
            String text = new String(file.getBytes());
            
            SummarizationResponse response = summarizerService.summarizeText(
                    text, maxLength, minLength
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Errore nella lettura del file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno: " + e.getMessage()));
        }
    }
    
    /**
     * Download riassunto come file TXT.
     * POST /api/summarize/download/txt
     */
    @PostMapping(value = "/download/txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> downloadTxt(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        
        ByteArrayResource resource = new ByteArrayResource(text.getBytes());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"riassunto.txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
    
    /**
     * Download riassunto come file DOCX.
     * POST /api/summarize/download/docx
     */
    @PostMapping(value = "/download/docx")
    public ResponseEntity<Resource> downloadDocx(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            byte[] docxBytes = summarizerService.generateDocx(text);
            
            ByteArrayResource resource = new ByteArrayResource(docxBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"riassunto.docx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    
    /**
     * Download riassunto come file PDF.
     * POST /api/summarize/download/pdf
     */
    @PostMapping(value = "/download/pdf")
    public ResponseEntity<Resource> downloadPdf(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            byte[] pdfBytes = summarizerService.generatePdf(text);
            
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"riassunto.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
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
