-- =====================================================
-- Query utili per Summarizer DB
-- =====================================================

-- ============ QUERY PER UTENTI ============

-- Ottieni tutti gli utenti attivi
SELECT id, email, nome, cognome, created_at, last_login 
FROM users 
WHERE is_active = TRUE 
ORDER BY created_at DESC;

-- Cerca utente per email
SELECT * FROM users WHERE email = 'test@example.com';

-- Conteggio riassunti per utente
SELECT 
    u.id,
    u.email,
    u.nome,
    u.cognome,
    COUNT(s.id) as total_summaries,
    MAX(s.created_at) as last_summary_date
FROM users u
LEFT JOIN summaries s ON u.id = s.user_id
GROUP BY u.id, u.email, u.nome, u.cognome
ORDER BY total_summaries DESC;


-- ============ QUERY PER RIASSUNTI ============

-- Ultimi 10 riassunti di un utente (per sidebar)
SELECT 
    id, 
    title, 
    LEFT(summary_text, 100) as preview,
    word_count,
    created_at,
    is_favorite
FROM summaries 
WHERE user_id = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- Riassunti preferiti di un utente
SELECT 
    id,
    title,
    summary_text,
    word_count,
    created_at
FROM summaries
WHERE user_id = 1 AND is_favorite = TRUE
ORDER BY created_at DESC;

-- Statistiche riassunti per utente
SELECT 
    user_id,
    COUNT(*) as total_summaries,
    AVG(word_count) as avg_words,
    SUM(CASE WHEN format = 'paragraph' THEN 1 ELSE 0 END) as paragraph_format,
    SUM(CASE WHEN format = 'bullet' THEN 1 ELSE 0 END) as bullet_format,
    SUM(CASE WHEN is_favorite = TRUE THEN 1 ELSE 0 END) as favorites
FROM summaries
WHERE user_id = 1
GROUP BY user_id;

-- Cerca riassunti per parole chiave
SELECT 
    id,
    title,
    LEFT(summary_text, 150) as preview,
    created_at
FROM summaries
WHERE user_id = 1 
  AND (summary_text LIKE '%parola%' OR title LIKE '%parola%')
ORDER BY created_at DESC;


-- ============ QUERY PER FILE ============

-- File caricati da un utente
SELECT 
    sf.original_filename,
    sf.file_type,
    sf.file_size,
    sf.uploaded_at,
    s.title as summary_title
FROM summary_files sf
JOIN summaries s ON sf.summary_id = s.id
WHERE s.user_id = 1
ORDER BY sf.uploaded_at DESC;

-- Statistiche per tipo file
SELECT 
    file_type,
    COUNT(*) as count,
    AVG(file_size) as avg_size_bytes,
    MAX(file_size) as max_size_bytes
FROM summary_files sf
JOIN summaries s ON sf.summary_id = s.id
WHERE s.user_id = 1
GROUP BY file_type;


-- ============ QUERY PER TAG ============

-- Riassunti per tag
SELECT 
    s.id,
    s.title,
    s.created_at,
    GROUP_CONCAT(st.tag_name) as tags
FROM summaries s
LEFT JOIN summary_tags st ON s.id = st.summary_id
WHERE s.user_id = 1
GROUP BY s.id, s.title, s.created_at
ORDER BY s.created_at DESC;

-- Tag più usati
SELECT 
    tag_name,
    COUNT(*) as usage_count
FROM summary_tags st
JOIN summaries s ON st.summary_id = s.id
WHERE s.user_id = 1
GROUP BY tag_name
ORDER BY usage_count DESC
LIMIT 10;


-- ============ QUERY PER FEEDBACK ============

-- Feedback medio per riassunto
SELECT 
    s.id,
    s.title,
    AVG(f.rating) as avg_rating,
    COUNT(f.id) as feedback_count
FROM summaries s
LEFT JOIN feedback f ON s.id = f.summary_id
WHERE s.user_id = 1
GROUP BY s.id, s.title
HAVING feedback_count > 0
ORDER BY avg_rating DESC;


-- ============ UTILITY & MANUTENZIONE ============

-- Elimina riassunti vecchi (es: più di 1 anno)
-- DELETE FROM summaries 
-- WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR)
--   AND is_favorite = FALSE;

-- Pulizia account inattivi (esempio)
-- UPDATE users 
-- SET is_active = FALSE 
-- WHERE last_login < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- Dimensione database per tabella
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb
FROM information_schema.TABLES
WHERE table_schema = 'summarizerdb'
ORDER BY (data_length + index_length) DESC;
