-- =====================================================
-- Script di inizializzazione Database Summarizer
-- =====================================================

-- Crea il database se non esiste
CREATE DATABASE IF NOT EXISTS summarizerdb 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE summarizerdb;

-- =====================================================
-- Tabella USERS (Utenti)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nome VARCHAR(100),
    cognome VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_user_email (email),
    INDEX idx_user_active (is_active, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella SUMMARIES (Riassunti)
-- =====================================================
CREATE TABLE IF NOT EXISTS summaries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    original_text TEXT NOT NULL,
    summary_text TEXT NOT NULL,
    original_length INT NOT NULL,
    summary_length INT NOT NULL,
    word_count INT NOT NULL,
    format ENUM('paragraph', 'bullet') DEFAULT 'paragraph',
    min_length INT,
    max_length INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_favorite BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT fk_summary_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_user_summaries (user_id, created_at DESC),
    INDEX idx_summary_created (created_at DESC),
    INDEX idx_summary_favorite (user_id, is_favorite, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella SUMMARY_FILES (File Originali)
-- =====================================================
CREATE TABLE IF NOT EXISTS summary_files (
    id INT PRIMARY KEY AUTO_INCREMENT,
    summary_id INT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_type ENUM('txt', 'pdf', 'docx', 'html') NOT NULL,
    file_size INT NOT NULL COMMENT 'dimensione in bytes',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_file_summary FOREIGN KEY (summary_id) 
        REFERENCES summaries(id) ON DELETE CASCADE,
    
    INDEX idx_file_summary (summary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella SUMMARY_TAGS (Tag/Categorie) - OPZIONALE
-- =====================================================
CREATE TABLE IF NOT EXISTS summary_tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    summary_id INT NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_tag_summary FOREIGN KEY (summary_id) 
        REFERENCES summaries(id) ON DELETE CASCADE,
    
    INDEX idx_tag_summary (summary_id),
    INDEX idx_tag_name (tag_name),
    UNIQUE KEY unique_summary_tag (summary_id, tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella FEEDBACK (Feedback Utenti) - OPZIONALE
-- =====================================================
CREATE TABLE IF NOT EXISTS feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    summary_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_feedback_summary FOREIGN KEY (summary_id) 
        REFERENCES summaries(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_feedback_summary (summary_id),
    INDEX idx_feedback_user (user_id),
    UNIQUE KEY unique_user_summary_feedback (user_id, summary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Dati di test (OPZIONALE - rimuovi in produzione)
-- =====================================================
-- Inserisci un utente di test (password: "test123" hashata con BCrypt)
-- NOTA: In produzione le password devono essere hashate dal backend!
INSERT INTO users (email, password_hash, nome, cognome) 
VALUES ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User')
ON DUPLICATE KEY UPDATE email=email;

-- =====================================================
-- Query utili per verificare la configurazione
-- =====================================================
-- Controlla le tabelle create:
-- SHOW TABLES;

-- Controlla la struttura di una tabella:
-- DESCRIBE users;
-- DESCRIBE summaries;

-- Verifica utente di test:
-- SELECT * FROM users;
