# 🎯 Backend Summarizer - Autenticazione e Database Implementati

## ✅ Implementazione Completata

### **Struttura creata:**
1. ✅ **Entity JPA** (User, Summary, Feedback) con relazioni e soft delete
2. ✅ **Repository** con query custom per recuperare dati attivi
3. ✅ **Spring Security** configurato con JWT token authentication
4. ✅ **AuthController** per login, registrazione, reset password
5. ✅ **SummarizerController** modificato per salvare riassunti automaticamente
6. ✅ **FeedbackController** per gestire recensioni utenti
7. ✅ **Dipendenze** aggiunte (Lombok, Spring Security, JWT, BCrypt)

---

## 🔑 API Endpoints

### **Autenticazione** (`/api/auth`)

#### **1. Registrazione**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "nuovo@example.com",
  "password": "password123"
}
```
**Risposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "nuovo@example.com",
  "userId": 2
}
```

#### **2. Login**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "test123"
}
```
**Risposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "test@example.com",
  "userId": 1
}
```

#### **3. Reset Password**
```http
POST http://localhost:8080/api/auth/reset-password
Content-Type: application/json

{
  "email": "test@example.com",
  "newPassword": "nuovaPassword123"
}
```

#### **4. Logout**
```http
POST http://localhost:8080/api/auth/logout
```
> **Nota:** Il logout JWT è gestito lato client eliminando il token

---

### **Riassunti** (`/api/summarize`)

#### **5. Riassumi testo** (salva automaticamente se loggato)
```http
POST http://localhost:8080/api/summarize
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "input": "Testo lungo da riassumere...",
  "maxLength": 150,
  "minLength": 50,
  "format": "paragraph"
}
```

#### **6. Riassumi file** (salva automaticamente se loggato)
```http
POST http://localhost:8080/api/summarize/upload
Content-Type: multipart/form-data
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

file: [seleziona PDF/DOCX/TXT]
maxLength: 150
minLength: 50
format: paragraph
```

#### **7. Visualizza i miei riassunti**
```http
GET http://localhost:8080/api/summarize/my-summaries?limit=10
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
**Risposta:**
```json
{
  "summaries": [
    {
      "id": 1,
      "summaryText": "Riassunto generato...",
      "wordCount": 45,
      "originalLength": 1500,
      "summaryLength": 300,
      "createdAt": "2026-02-10T15:30:00"
    }
  ],
  "totalCount": 5
}
```

#### **8. Elimina riassunto**
```http
DELETE http://localhost:8080/api/summarize/3
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### **Feedback** (`/api/feedback`)

#### **9. Lascia feedback**
```http
POST http://localhost:8080/api/feedback
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "summaryId": 1,
  "rating": 5,
  "comment": "Ottimo riassunto!"
}
```
> **Vincolo:** Un utente può lasciare UN SOLO feedback per riassunto

#### **10. Elimina feedback**
```http
DELETE http://localhost:8080/api/feedback/2
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### **11. Rating medio riassunto**
```http
GET http://localhost:8080/api/feedback/average/1
```
**Risposta:**
```json
{
  "averageRating": 4.5
}
```

---

## 🧪 Test Backend

### **1. Avvia il backend**
```powershell
cd Summarizer-BE
.\gradlew bootRun
```

### **2. Testa con utente già esistente nel DB**
```powershell
# Login
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"test@example.com","password":"test123"}'

# Copia il token dalla risposta
$token = "eyJhbGciOiJIUzI1NiJ9..."

# Genera riassunto (viene salvato automaticamente)
curl -X POST http://localhost:8080/api/summarize `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token" `
  -d '{"input":"Testo molto lungo da riassumere con almeno 100 caratteri per passare la validazione del servizio NLP.","maxLength":100,"minLength":30}'

# Visualizza i tuoi riassunti
curl -X GET http://localhost:8080/api/summarize/my-summaries `
  -H "Authorization: Bearer $token"
```

---

## 🔐 Sicurezza Implementata

### **Protezione endpoint:**
- ✅ `/api/auth/**` → **Pubblici** (login, register, reset)
- ✅ `/api/summarize/upload` e `/api/summarize/extract` → **Pubblici** (uso anonimo)
- ✅ `/api/summarize` (POST) → **Pubblico** ma salva solo se loggato
- ✅ `/api/summarize/my-summaries` → **Richiede autenticazione**
- ✅ `/api/feedback/**` → **Richiede autenticazione**

### **JWT Token:**
- Durata: **24 ore** (configurabile in `application.yaml`)
- Secret: `SummarizerSecretKeyForJWTTokenGeneration2026VerySecure12345!`
- Algoritmo: HMAC-SHA256

### **Password:**
- Hash: **BCrypt** (10 round)
- Validazione: Gestita da Spring Security

---

## 📝 Note Importanti

### **Soft Delete:**
- I riassunti e feedback eliminati hanno `deleted_at` impostato (non vengono cancellati fisicamente)
- Le query filtrano automaticamente i record eliminati con `WHERE deleted_at IS NULL`

### **Relazioni Database:**
```
users
  ↓ (user_id)
summaries ← feedback (summary_id + user_id)
```
- Se elimini un utente → tutti i suoi riassunti e feedback vengono eliminati (`ON DELETE CASCADE`)
- Un utente può lasciare UN SOLO feedback per riassunto (vincolo `UNIQUE`)

### **CORS:**
- Configurato per `http://localhost:4200` (frontend Angular)
- Permessi: GET, POST, PUT, DELETE, OPTIONS
- Header: Authorization, Content-Type

---

## 🚀 Prossimi Passi

1. **Testa tutte le API** con Postman o `curl`
2. **Integra frontend Angular** con i nuovi endpoint
3. **Crea navbar** con "I miei riassunti"
4. **Implementa modal feedback** con stelle 1-5
5. **Aggiungi login/registrazione** nel frontend

Vuoi che ti aiuti con l'integrazione del frontend Angular? 🎨
