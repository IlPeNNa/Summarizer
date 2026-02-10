-- =====================================================
-- Query utili per Summarizer DB
-- =====================================================

-- ============ QUERY PER UTENTI ============

-- Ottieni tutti gli utenti
SELECT id, email 
FROM users 
ORDER BY id DESC;

-- Cerca utente per email
SELECT * FROM users WHERE email = 'test@example.com';

-- Conteggio riassunti per utente
SELECT 
    u.id,
    u.email,
    COUNT(s.id) as total_summaries,
    MAX(s.created_at) as last_summary_date
FROM users u
LEFT JOIN summaries s ON u.id = s.user_id
GROUP BY u.id, u.email
ORDER BY total_summaries DESC;


-- ============ QUERY PER RIASSUNTI ============

-- Ultimi 10 riassunti di un utente (per sidebar/cronologia)
SELECT 
    id, 
    LEFT(summary_text, 100) as preview,
    word_count,
    created_at
FROM summaries 
WHERE user_id = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- Riassunto completo per ID
SELECT 
    id,
    original_text,
    summary_text,
    original_length,
    summary_length,
    word_count,
    created_at
FROM summaries
WHERE id = 1;

-- Statistiche riassunti per utente
SELECT 
    user_id,
    COUNT(*) as total_summaries,
    AVG(word_count) as avg_words,
    MIN(word_count) as min_words,
    MAX(word_count) as max_words,
    SUM(original_length) as total_chars_processed
FROM summaries
WHERE user_id = 1
GROUP BY user_id;

-- Cerca riassunti per parole chiave (nel testo del riassunto)
SELECT 
    id,
    LEFT(summary_text, 150) as preview,
    word_count,
    created_at
FROM summaries
WHERE user_id = 1 
  AND summary_text LIKE '%parola_chiave%'
ORDER BY created_at DESC;

-- Riassunti creati oggi
SELECT 
    id,
    LEFT(summary_text, 100) as preview,
    word_count,
    created_at
FROM summaries
WHERE user_id = 1 
  AND DATE(created_at) = CURDATE()
ORDER BY created_at DESC;


-- ============ QUERY PER FEEDBACK ============

-- Tutti i feedback di un utente
SELECT 
    f.id,
    f.summary_id,
    f.rating,
    f.comment,
    f.created_at,
    LEFT(s.summary_text, 100) as summary_preview
FROM feedback f
JOIN summaries s ON f.summary_id = s.id
WHERE f.user_id = 1
ORDER BY f.created_at DESC;

-- Rating medio per riassunto
SELECT 
    s.id,
    LEFT(s.summary_text, 100) as preview,
    COUNT(f.id) as feedback_count,
    AVG(f.rating) as avg_rating
FROM summaries s
LEFT JOIN feedback f ON s.id = f.summary_id
WHERE s.user_id = 1
GROUP BY s.id
HAVING feedback_count > 0
ORDER BY avg_rating DESC;

-- Feedback per un riassunto specifico
SELECT 
    u.email,
    f.rating,
    f.comment,
    f.created_at
FROM feedback f
JOIN users u ON f.user_id = u.id
WHERE f.summary_id = 1
ORDER BY f.created_at DESC;


-- ============ UTILITY & MANUTENZIONE ============

-- Elimina riassunti vecchi (es: più di 1 anno)
-- ATTENZIONE: Decommentare solo quando necessario!
-- DELETE FROM summaries 
-- WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- Conta riassunti per data
SELECT 
    DATE(created_at) as date,
    COUNT(*) as summaries_count
FROM summaries
WHERE user_id = 1
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- Dimensione database per tabella
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb,
    table_rows
FROM information_schema.TABLES
WHERE table_schema = 'summarizerdb'
ORDER BY (data_length + index_length) DESC;
