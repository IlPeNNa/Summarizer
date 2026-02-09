# 📊 Database Configuration - Summarizer

Questa cartella contiene gli script SQL e la configurazione per il database MySQL del progetto Summarizer.

## 🗄️ Configurazione Database

### Credenziali
- **Database**: `summarizerdb`
- **Host**: `localhost`
- **Porta**: `3306`
- **Username**: `root`
- **Password**: `Tfovygv$185956`

---

## 🚀 Setup Iniziale

### 1. Avvia MySQL Server
Assicurati che MySQL sia in esecuzione:
```bash
# Su Windows, verifica che il servizio MySQL sia attivo
# Oppure avvia MySQL Workbench
```

### 2. Esegui lo script di inizializzazione

**Opzione A - Da MySQL Workbench:**
1. Apri MySQL Workbench
2. Connettiti al server (root@localhost:3306)
3. File → Open SQL Script → Seleziona `init-schema.sql`
4. Clicca sul fulmine ⚡ per eseguire lo script

**Opzione B - Da terminale:**
```bash
mysql -u root -p < database/init-schema.sql
# Inserisci la password quando richiesto
```

### 3. Verifica la configurazione
```sql
USE summarizerdb;
SHOW TABLES;
```

Dovresti vedere queste tabelle:
- `users`
- `summaries`
- `summary_files`
- `summary_tags`
- `feedback`

---

## 📋 Struttura Database

### **Tabella `users`**
Gestisce gli utenti registrati
- `id`: Chiave primaria
- `email`: Email univoca (username)
- `password_hash`: Password hashata (BCrypt)
- `nome`, `cognome`: Dati utente
- `created_at`, `last_login`: Timestamp
- `is_active`: Account attivo/disattivato

### **Tabella `summaries`**
Memorizza tutti i riassunti generati
- `id`: Chiave primaria
- `user_id`: Riferimento all'utente
- `title`: Titolo del riassunto
- `original_text`, `summary_text`: Testi originale e riassunto
- `word_count`: Conteggio parole
- `format`: Formato (paragraph/bullet)
- `is_favorite`: Flag preferito

### **Tabella `summary_files`**
Traccia i file originali caricati
- `summary_id`: Riferimento al riassunto
- `original_filename`: Nome file originale
- `file_type`: Tipo (txt/pdf/docx)
- `file_size`: Dimensione in bytes

### **Tabella `summary_tags`** (Opzionale)
Tag/categorie per organizzare i riassunti
- `summary_id`: Riferimento al riassunto
- `tag_name`: Nome del tag

### **Tabella `feedback`** (Opzionale)
Feedback e valutazioni utenti
- `summary_id`, `user_id`: Riferimenti
- `rating`: Valutazione 1-5 stelle
- `comment`: Commento testuale

---

## 🔧 Backend Configuration

La configurazione è già stata aggiornata in `Summarizer-BE/src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/summarizerdb
    username: root
    password: Tfovygv$185956
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update  # Crea/aggiorna automaticamente le tabelle
```

---

## 📝 Query Utili

Vedi il file `queries.sql` per query di esempio:
- Ottenere ultimi riassunti di un utente
- Statistiche per utente
- Ricerca per parole chiave
- Gestione preferiti
- E molto altro...

---

## 🔐 Sicurezza

### ⚠️ IMPORTANTE PER PRODUZIONE:
1. **NON committare** password in chiaro su Git
2. Usa **variabili d'ambiente** per le credenziali:
   ```yaml
   password: ${DB_PASSWORD:default_password}
   ```
3. Cambia `ddl-auto: update` in `ddl-auto: validate`
4. Abilita SSL per connessione database
5. Crea un utente MySQL dedicato (non root)

### Password Hashing
Le password utente vengono hashate usando **BCrypt** (implementato nel backend).
Non salvare MAI password in chiaro!

---

## 🧪 Utente di Test

Lo script crea automaticamente un utente di test:
- **Email**: `test@example.com`
- **Password**: `test123`

⚠️ **Rimuovi questo utente in produzione!**

```sql
DELETE FROM users WHERE email = 'test@example.com';
```

---

## 🛠️ Troubleshooting

### Errore: "Access denied for user 'root'@'localhost'"
- Verifica la password MySQL
- Controlla che MySQL sia in esecuzione

### Errore: "Unknown database 'summarizerdb'"
- Esegui lo script `init-schema.sql`
- O crea manualmente: `CREATE DATABASE summarizerdb;`

### Il backend non si connette al DB
1. Verifica che MySQL sia in esecuzione
2. Controlla le credenziali in `application.yaml`
3. Controlla i log Spring Boot per errori specifici

---

## 📞 Supporto

Per problemi o domande sulla configurazione del database, controlla:
1. Log del backend: `Summarizer-BE/logs/`
2. Console Spring Boot durante l'avvio
3. MySQL log files
