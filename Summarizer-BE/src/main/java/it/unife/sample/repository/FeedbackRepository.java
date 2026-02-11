package it.unife.sample.repository;

import it.unife.sample.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
    // Recupera feedback NON eliminati per un riassunto
    @Query("SELECT f FROM Feedback f WHERE f.summary.id = :summaryId AND f.deletedAt IS NULL")
    List<Feedback> findActiveBySummaryId(@Param("summaryId") Integer summaryId);
    
    // Controlla se un utente ha già lasciato feedback per un riassunto
    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId AND f.summary.id = :summaryId AND f.deletedAt IS NULL")
    Optional<Feedback> findActiveByUserIdAndSummaryId(@Param("userId") Integer userId, @Param("summaryId") Integer summaryId);
    
    // Calcola rating medio per un riassunto
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.summary.id = :summaryId AND f.deletedAt IS NULL")
    Double calculateAverageRatingBySummaryId(@Param("summaryId") Integer summaryId);
}
