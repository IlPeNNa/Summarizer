package it.unife.sample.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "summaries", indexes = {
    @Index(name = "idx_user_summaries", columnList = "user_id,deleted_at,created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_summary_user"))
    private User user;
    
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String originalText;
    
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String summaryText;
    
    @Column(nullable = false)
    private Integer originalLength;
    
    @Column(nullable = false)
    private Integer summaryLength;
    
    @Column(nullable = false)
    private Integer wordCount;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "summary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
