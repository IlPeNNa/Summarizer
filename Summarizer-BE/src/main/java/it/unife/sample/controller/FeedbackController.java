package it.unife.sample.controller;

import it.unife.sample.dto.FeedbackRequest;
import it.unife.sample.entity.Feedback;
import it.unife.sample.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    /**
     * Crea un nuovo feedback per un riassunto.
     * POST /api/feedback
     */
    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody FeedbackRequest request) {
        try {
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Non autenticato"));
            }
            
            Feedback feedback = feedbackService.createFeedback(userEmail, request);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Feedback inviato con successo",
                    "feedbackId", feedback.getId()
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore: " + e.getMessage()));
        }
    }
    
    /**
     * Elimina un feedback (soft delete).
     * DELETE /api/feedback/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Integer id) {
        try {
            String userEmail = getAuthenticatedUserEmail();
            if (userEmail == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Non autenticato"));
            }
            
            feedbackService.deleteFeedback(id, userEmail);
            return ResponseEntity.ok(Map.of("message", "Feedback eliminato"));
            
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
     * Ottieni rating medio per un riassunto.
     * GET /api/feedback/average/{summaryId}
     */
    @GetMapping("/average/{summaryId}")
    public ResponseEntity<?> getAverageRating(@PathVariable Integer summaryId) {
        try {
            Double average = feedbackService.getAverageRating(summaryId);
            
            return ResponseEntity.ok(Map.of(
                    "averageRating", average != null ? average : 0.0
            ));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore: " + e.getMessage()));
        }
    }
    
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}
