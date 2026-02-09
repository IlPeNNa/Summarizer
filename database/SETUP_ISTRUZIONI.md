# 🎯 Setup Database - Istruzioni Rapide

## ✅ Configurazione Completata!

Ho preparato tutto per collegare il database MySQL al tuo progetto Summarizer.

---

## 📋 Cosa è stato fatto:

### 1. ✅ Configurazione Backend (Spring Boot)
- File: `Summarizer-BE/src/main/resources/application.yaml`
- Configurata connessione a MySQL
- Abilitate JPA e Hibernate
- Configurato connection pool (HikariCP)

### 2. ✅ Script SQL Database
- File: `database/init-schema.sql`
  - Creazione database `summarizerdb`
  - 5 tabelle: users, summaries, summary_files, summary_tags, feedback
  - Indici per performance ottimali
  - Utente di test incluso

### 3. ✅ Query Utili
- File: `database/queries.sql`
  - Query per sidebar (ultimi riassunti)
  - Statistiche utente
  - Ricerca e filtri
  - Manutenzione database

### 4. ✅ Documentazione
- File: `database/README.md`
  - Guida completa setup
  - Troubleshooting
  - Best practices sicurezza

---

## 🚀 PROSSIMI PASSI (Fai così):

### STEP 1: Crea il Database in MySQL Workbench
```sql
1. Apri MySQL Workbench
2. Connettiti a localhost (root@localhost:3306)
3. File → Open SQL Script
4. Seleziona: database/init-schema.sql
5. Clicca sul fulmine ⚡ per eseguire
```

Oppure da terminale:
```bash
mysql -u root -p < database/init-schema.sql
```

### STEP 2: Verifica che sia tutto OK
```sql
USE summarizerdb;
SHOW TABLES;
```

Devi vedere:
- users
- summaries  
- summary_files
- summary_tags
- feedback

### STEP 3: Riavvia il Backend
```bash
cd Summarizer-BE
.\gradlew bootRun
```

**Quando vedi questa riga nei log, significa che funziona:**
```
HikariPool-1 - Start completed.
```

---

## 🔍 Come verificare che funziona:

1. **Avvia il backend** - Guarda i log, NON devono esserci errori di connessione
2. **Cerca questa riga** nei log:
   ```
   o.s.b.a.h.H2ConsoleAutoConfiguration : H2 console available at '/h2-console'
   # O per MySQL:
   HikariPool-1 - Started
   ```
3. **Controlla le tabelle** in MySQL Workbench:
   ```sql
   USE summarizerdb;
   SELECT * FROM users;  -- Dovrebbe mostrare l'utente di test
   ```

---

## ⚠️ SICUREZZA - IMPORTANTE!

### 🔐 NON committare le password su Git!

Ho aggiunto un warning nel `.gitignore`, ma ricorda:

**PRIMA DI FARE `git add .`:**
```bash
# Verifica cosa stai committando:
git status

# Se vedi application.yaml nella lista:
# 1. Opzione A: Non committarlo
git reset HEAD Summarizer-BE/src/main/resources/application.yaml

# 2. Opzione B: Usa variabili d'ambiente (RACCOMANDATO)
# Nel file application.yaml cambia:
password: ${DB_PASSWORD:default}

# Poi imposta la variabile:
# Windows PowerShell:
$env:DB_PASSWORD="Tfovygv$185956"

# Windows CMD:
set DB_PASSWORD=Tfovygv$185956
```

---

## 📊 Struttura Tabelle Creata

### 👤 `users` - Utenti del sistema
- Email, password hashata, nome, cognome
- Timestamp creazione e ultimo login

### 📝 `summaries` - Riassunti generati  
- Testo originale e riassunto
- Statistiche (lunghezza, parole)
- Formato (paragraph/bullet)
- Flag preferito

### 📎 `summary_files` - File caricati
- Nome file originale
- Tipo (txt/pdf/docx)
- Dimensione

### 🏷️ `summary_tags` - Tag/Categorie (opzionale)
- Per organizzare i riassunti

### ⭐ `feedback` - Valutazioni (opzionale)
- Rating 1-5 stelle
- Commenti

---

## 🧪 Testa subito!

**Utente di test già creato:**
- Email: `test@example.com`
- Password: `test123`

Puoi usarlo per testare login/registrazione quando implementerai l'autenticazione!

---

## 🆘 Problemi?

### Errore: "Access denied for user 'root'"
→ Controlla password in `application.yaml`

### Errore: "Unknown database 'summarizerdb'"  
→ Esegui `init-schema.sql` in MySQL Workbench

### Il backend non parte
→ Guarda i log, cerca "caused by"

### Database vuoto dopo creazione
→ Ricontrolla che lo script sia stato eseguito completamente

---

## 📚 File Utili di Riferimento

- **Setup completo**: `database/README.md`
- **Query esempi**: `database/queries.sql`
- **Schema DB**: `database/init-schema.sql`
- **Config backend**: `Summarizer-BE/src/main/resources/application.yaml`

---

**Tutto pronto! 🎉**

Il database è configurato e pronto per l'uso. Ora puoi procedere con:
1. Implementazione autenticazione (login/registrazione)
2. Salvataggio riassunti nel DB
3. Sidebar con cronologia riassunti
