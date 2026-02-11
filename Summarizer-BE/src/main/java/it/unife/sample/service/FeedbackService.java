package it.unife.sample.service;

import it.unife.sample.dto.FeedbackRequest;
import it.unife.sample.entity.Feedback;
import it.unife.sample.entity.Summary;
import it.unife.sample.entity.User;
import it.unife.sample.repository.FeedbackRepository;
import it.unife.sample.repository.SummaryRepository;
import it.unife.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final SummaryRepository summaryRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Feedback createFeedback(String userEmail, FeedbackRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Summary summary = summaryRepository.findById(request.getSummaryId())
                .orElseThrow(() -> new RuntimeException("Riassunto non trovato"));
        
        // Controlla se l'utente ha già lasciato feedback per questo riassunto
        Optional<Feedback> existingFeedback = feedbackRepository
                .findActiveByUserIdAndSummaryId(user.getId(), request.getSummaryId());
        
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("Hai già lasciato un feedback per questo riassunto");
        }
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setSummary(summary);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        
        log.info("Nuovo feedback creato - Rating: {}, User: {}, Summary: {}", 
                request.getRating(), userEmail, request.getSummaryId());
        
        return feedbackRepository.save(feedback);
    }
    
    @Transactional
    public void deleteFeedback(Integer feedbackId, String userEmail) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback non trovato"));
        
        if (!feedback.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Non autorizzato a eliminare questo feedback");
        }
        
        feedback.setDeletedAt(java.time.LocalDateTime.now());
        feedbackRepository.save(feedback);
        
        log.info("Feedback eliminato - ID: {}, User: {}", feedbackId, userEmail);
    }
    
    public Double getAverageRating(Integer summaryId) {
        return feedbackRepository.calculateAverageRatingBySummaryId(summaryId);
    }
}
