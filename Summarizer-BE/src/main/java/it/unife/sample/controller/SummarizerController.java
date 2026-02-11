package it.unife.sample.controller;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationRequest;
import it.unife.sample.dto.SummarizationResponse;
import it.unife.sample.dto.SummaryResponse;
import it.unife.sample.entity.Summary;
import it.unife.sample.service.SummarizerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST Controller per gli endpoint di summarization.
 * Espone le API per il frontend Angular.
 */
@RestController
@RequestMapping("/api/summarize")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SummarizerController {
    
    private static final Logger log = LoggerFactory.getLogger(SummarizerController.class);
    
    private final SummarizerService summarizerService;
    
    /**
     * Endpoint per riassumere un testo.
     * POST /api/summarize
     * Se l'utente è autenticato, salva il riassunto nel database.
     */
    @PostMapping
    public ResponseEntity<?> summarize(@RequestBody SummarizationRequest request) {
        try {
            log.debug("Richiesta riassunto - Input length: {}, maxLength: {}, minLength: {}", 
                request.getInput().length(), request.getMaxLength(), request.getMinLength());
            
            int maxLength = request.getMaxLength() != null ? request.getMaxLength() : 150;
            int minLength = request.getMinLength() != null ? request.getMinLength() : 50;
            String format = request.getFormat() != null ? request.getFormat() : "paragraph";
            
            SummarizationResponse response = summarizerService.summarizeText(
                    request.getInput(), 
                    maxLength, 
                    minLength,
                    format
            );
            
            // Salva nel DB se l'utente è autenticato
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail != null) {
                Summary savedSummary = summarizerService.saveSummary(
                        userEmail,
                        request.getInput(),
                        response.getSummary(),
                        response.getWordCount()
                );
                response.setSummaryId(savedSummary.getId());
                log.info("Riassunto salvato per utente: {} con ID: {}", userEmail, savedSummary.getId());
            }
            
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
     * Estrai testo da un file senza riassumerlo.
     * POST /api/summarize/extract
     */
    @PostMapping("/extract")
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) {
        try {
            log.debug("Estrazione testo - Nome: {}, Dimensione: {} bytes", 
                file.getOriginalFilename(), file.getSize());
            
            String extractedText = summarizerService.extractTextFromFile(
                    file.getBytes(), 
                    file.getOriginalFilename()
            );
            
            return ResponseEntity.ok(Map.of(
                "text", extractedText,
                "length", extractedText.length(),
                "filename", file.getOriginalFilename()
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Errore nella lettura del file: " + e.getMessage()));
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
     * Se l'utente è autenticato, salva il riassunto nel database.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> summarizeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "maxLength", defaultValue = "150") int maxLength,
            @RequestParam(value = "minLength", defaultValue = "50") int minLength,
            @RequestParam(value = "format", defaultValue = "paragraph") String format) {
        try {
            log.debug("Upload file - Nome: {}, Dimensione: {} bytes, maxLength: {}, minLength: {}", 
                file.getOriginalFilename(), file.getSize(), maxLength, minLength);
            
            // Prima estrai il testo originale per salvarlo nel DB
            String originalText = summarizerService.extractTextFromFile(
                    file.getBytes(), 
                    file.getOriginalFilename()
            );
            
            // Utilizza il nuovo metodo che gestisce PDF, DOCX e TXT
            SummarizationResponse response = summarizerService.summarizeFile(
                    file.getBytes(), 
                    file.getOriginalFilename(),
                    maxLength, 
                    minLength,
                    format
            );
            
            // Salva nel DB se l'utente è autenticato
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail != null) {
                Summary savedSummary = summarizerService.saveSummary(
                        userEmail,
                        originalText,
                        response.getSummary(),
                        response.getWordCount()
                );
                response.setSummaryId(savedSummary.getId());
                log.info("Riassunto da file salvato per utente: {} con ID: {}", userEmail, savedSummary.getId());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Errore nella lettura del file: " + e.getMessage()));
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
    
    /**
     * Recupera gli ultimi riassunti dell'utente autenticato.
     * GET /api/summarize/my-summaries
     */
    @GetMapping("/my-summaries")
    public ResponseEntity<?> getMySummaries(@RequestParam(defaultValue = "10") int limit) {
        try {
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Non autenticato"));
            }
            
            List<SummaryResponse> summaries = summarizerService.getUserSummaries(userEmail, limit);
            long totalCount = summarizerService.countUserSummaries(userEmail);
            
            return ResponseEntity.ok(Map.of(
                    "summaries", summaries,
                    "totalCount", totalCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore: " + e.getMessage()));
        }
    }
    
    /**
     * Elimina (soft delete) un riassunto dell'utente.
     * DELETE /api/summarize/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSummary(@PathVariable Integer id) {
        try {
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Non autenticato"));
            }
            
            summarizerService.deleteSummary(id, userEmail);
            return ResponseEntity.ok(Map.of("message", "Riassunto eliminato"));
            
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore: " + e.getMessage()));
        }
    }
    
    /**
     * Helper per ottenere l'email dell'utente autenticato.
     * Ritorna null se l'utente non è autenticato.
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}
