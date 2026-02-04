# 🗄️ TODO: Integrazione Database MySQL

## Quando sarai pronto per aggiungere il database...

### 📋 Cosa serve il database

Il database MySQL servirà per:
- ✅ Salvare le recensioni degli utenti sui riassunti
- ✅ Storico dei riassunti generati
- ✅ Rating (stelle) per ogni riassunto
- ✅ Commenti e feedback
- ✅ Statistiche d'uso

---

## 🏗️ Struttura Database Proposta

### Tabella: `summaries`
```sql
CREATE TABLE summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_text TEXT NOT NULL,
    summary_text TEXT NOT NULL,
    original_length INT NOT NULL,
    summary_length INT NOT NULL,
    min_length INT NOT NULL,
    max_length INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at)
);
```

### Tabella: `reviews`
```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (summary_id) REFERENCES summaries(id) ON DELETE CASCADE,
    INDEX idx_summary_id (summary_id),
    INDEX idx_rating (rating)
);
```

### Tabella: `users` (opzionale - per future autenticazioni)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 📝 Entità JPA da Creare

### `Summary.java`
```java
package it.unife.sample.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "summaries")
public class Summary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalText;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String summaryText;
    
    private int originalLength;
    private int summaryLength;
    private int minLength;
    private int maxLength;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "summary", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters, Setters, Constructors...
}
```

### `Review.java`
```java
package it.unife.sample.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id", nullable = false)
    private Summary summary;
    
    @Column(nullable = false)
    private int rating; // 1-5 stelle
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters, Setters, Constructors...
}
```

---

## 🔧 Repository da Creare

### `SummaryRepository.java`
```java
package it.unife.sample.repository;

import it.unife.sample.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    
    List<Summary> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT s FROM Summary s LEFT JOIN FETCH s.reviews ORDER BY s.createdAt DESC")
    List<Summary> findAllWithReviews();
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.summary.id = :summaryId")
    Double getAverageRating(Long summaryId);
}
```

### `ReviewRepository.java`
```java
package it.unife.sample.repository;

import it.unife.sample.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findBySummaryIdOrderByCreatedAtDesc(Long summaryId);
    
    Long countBySummaryId(Long summaryId);
}
```

---

## 📡 API Endpoints da Aggiungere

### Controller: `ReviewController.java`
```java
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {
    
    // POST /api/reviews - Crea nuova recensione
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest request) {
        // Logica per salvare recensione
    }
    
    // GET /api/reviews/summary/{summaryId} - Ottieni recensioni per riassunto
    @GetMapping("/summary/{summaryId}")
    public ResponseEntity<List<Review>> getReviewsBySummary(@PathVariable Long summaryId) {
        // Logica per recuperare recensioni
    }
    
    // GET /api/reviews/summary/{summaryId}/average - Media rating
    @GetMapping("/summary/{summaryId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long summaryId) {
        // Logica per calcolare media
    }
}
```

---

## 🎨 Modifiche Frontend

### Nel componente Angular:

1. **Aggiungi form recensione**:
```html
<div class="review-section" *ngIf="summary">
  <h3>⭐ Valuta questo riassunto</h3>
  <div class="star-rating">
    <span *ngFor="let star of [1,2,3,4,5]" 
          (click)="setRating(star)"
          [class.active]="star <= currentRating">
      ★
    </span>
  </div>
  <textarea [(ngModel)]="reviewComment" 
            placeholder="Commenta questo riassunto (opzionale)">
  </textarea>
  <button (click)="submitReview()">Invia Recensione</button>
</div>
```

2. **Visualizza recensioni esistenti**:
```html
<div class="reviews-list">
  <h4>Recensioni precedenti</h4>
  <div *ngFor="let review of reviews" class="review-item">
    <div class="review-rating">{{ '★'.repeat(review.rating) }}</div>
    <p>{{ review.comment }}</p>
    <small>{{ review.createdAt | date:'short' }}</small>
  </div>
</div>
```

---

## 🔄 Modifica Service Backend

Nel `SummarizerService.java`, salva automaticamente ogni riassunto:

```java
public SummarizationResponse summarizeText(String text, int maxLength, int minLength) 
        throws NlpServiceClient.NlpServiceException {
    validateText(text);
    validateLengthParameters(maxLength, minLength);
    
    // Chiama servizio NLP
    SummarizationResponse response = nlpServiceClient.summarize(text, maxLength, minLength);
    
    // NUOVO: Salva nel database
    Summary summary = new Summary();
    summary.setOriginalText(text);
    summary.setSummaryText(response.getSummary());
    summary.setOriginalLength(response.getOriginalLength());
    summary.setSummaryLength(response.getSummaryLength());
    summary.setMinLength(minLength);
    summary.setMaxLength(maxLength);
    
    summaryRepository.save(summary);
    
    // Aggiungi ID alla response
    response.setSummaryId(summary.getId());
    
    return response;
}
```

---

## 📊 Query Utili per MySQL Workbench

### Statistiche globali
```sql
-- Totale riassunti generati
SELECT COUNT(*) as total_summaries FROM summaries;

-- Media lunghezza riassunti
SELECT AVG(summary_length) as avg_summary_length FROM summaries;

-- Riassunti più apprezzati
SELECT s.id, s.summary_text, AVG(r.rating) as avg_rating, COUNT(r.id) as review_count
FROM summaries s
LEFT JOIN reviews r ON s.id = r.summary_id
GROUP BY s.id
ORDER BY avg_rating DESC, review_count DESC
LIMIT 10;

-- Distribuzione rating
SELECT rating, COUNT(*) as count
FROM reviews
GROUP BY rating
ORDER BY rating DESC;
```

---

## ⚙️ Configurazione MySQL Workbench

1. **Crea database**:
```sql
CREATE DATABASE summarizer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE summarizer;
```

2. **Verifica connessione**:
   - Host: `localhost`
   - Port: `3306`
   - Username: `root`
   - Password: `Tfovygv$185956`

3. **Le tabelle verranno create automaticamente** da Hibernate (vedi `application.yaml: ddl-auto: update`)

---

## ✅ Checklist Implementazione Database

- [ ] Creare entità JPA (`Summary`, `Review`)
- [ ] Creare repository
- [ ] Modificare `SummarizerService` per salvare riassunti
- [ ] Creare `ReviewController`
- [ ] Aggiungere endpoint API recensioni
- [ ] Modificare frontend per mostrare form recensioni
- [ ] Aggiungere visualizzazione recensioni esistenti
- [ ] Implementare logica rating stelle
- [ ] Testare salvataggio e recupero dati
- [ ] Creare query statistiche in MySQL Workbench

---

## 🚀 Quando Implementare

Per ora **NON implementare** - concentrati sul funzionamento base dell'applicazione.

Quando sarai pronto:
1. Leggi questo file
2. Segui i passaggi nell'ordine
3. Testa ogni componente separatamente
4. Integra gradualmente le funzionalità

---

**📌 Nota**: Questo è solo un piano! Puoi modificarlo come preferisci quando sarai pronto per aggiungere il database.
