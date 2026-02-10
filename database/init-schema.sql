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
-- Solo: id, email, password
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella SUMMARIES (Riassunti)
-- Testo originale, riassunto, lunghezze, word_count, data creazione
-- MEDIUMTEXT supporta fino a ~2-3 milioni di parole (documenti molto lunghi)
-- =====================================================
CREATE TABLE IF NOT EXISTS summaries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    original_text MEDIUMTEXT NOT NULL,
    summary_text MEDIUMTEXT NOT NULL,
    original_length INT NOT NULL,
    summary_length INT NOT NULL,
    word_count INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    
    CONSTRAINT fk_summary_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_user_summaries (user_id, deleted_at, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Tabella FEEDBACK (Feedback Utenti)
-- Rating 1-5 stelle + commento opzionale
-- =====================================================
CREATE TABLE IF NOT EXISTS feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    summary_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    
    CONSTRAINT fk_feedback_summary FOREIGN KEY (summary_id) 
        REFERENCES summaries(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_feedback_summary (summary_id, deleted_at),
    UNIQUE KEY unique_user_summary_feedback (user_id, summary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Dati di test (OPZIONALE - rimuovi in produzione)
-- =====================================================
-- Inserisci un utente di test (password: "test123" hashata con BCrypt)
-- NOTA: In produzione le password devono essere hashate dal backend!
INSERT INTO users (email, password) 
VALUES ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON DUPLICATE KEY UPDATE email=email;

-- =====================================================
-- Query utili per verificare la configurazione
-- =====================================================
-- Controlla le tabelle create (dovrebbero essere 3: users, summaries, feedback):
SHOW TABLES;

-- Controlla la struttura delle tabelle:
DESCRIBE users;
DESCRIBE summaries;
DESCRIBE feedback;

-- Verifica utente di test:
SELECT * FROM users;

-- =====================================================
-- NOTE TECNICHE
-- =====================================================
-- MEDIUMTEXT può contenere:
-- - Max 16,777,215 caratteri (~16MB)
-- - Circa 2-3 milioni di parole
-- - Perfetto per PDF lunghi di 100+ pagine
--
-- TEXT standard (se preferisci più leggero):
-- - Max 65,535 caratteri (~65KB)  
-- - Circa 10,000-15,000 parole
-- - Sufficiente per la maggior parte dei documenti
