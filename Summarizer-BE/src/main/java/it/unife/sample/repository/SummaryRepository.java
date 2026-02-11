package it.unife.sample.repository;

import it.unife.sample.entity.Summary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Integer> {
    
    // Recupera solo riassunti NON eliminati di un utente, ordinati per data (più recenti primi)
    @Query("SELECT s FROM Summary s WHERE s.user.id = :userId AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Summary> findActiveByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
    
    // Recupera ultimi N riassunti NON eliminati di un utente
    @Query("SELECT s FROM Summary s WHERE s.user.id = :userId AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Summary> findTopActiveByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    // Conta riassunti NON eliminati di un utente
    @Query("SELECT COUNT(s) FROM Summary s WHERE s.user.id = :userId AND s.deletedAt IS NULL")
    long countActiveByUserId(@Param("userId") Integer userId);
}
