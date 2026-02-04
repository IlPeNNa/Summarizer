# 🧪 TEST RAPIDO - Guida Veloce

## Come testare l'applicazione

### 1️⃣ Avvia tutti i servizi

```powershell
.\start-all.ps1
```

Attendi che tutti e 3 i servizi siano avviati (circa 10-15 secondi).

### 2️⃣ Apri il browser

Vai su: **http://localhost:4200**

### 3️⃣ Testa le funzionalità

#### 📝 Test 1: Input Manuale
1. Incolla testo nel campo di testo
2. Scegli "Medio" come lunghezza
3. Clicca "🚀 Genera Riassunto"
4. Attendi il risultato (5-10 secondi)

#### 📎 Test 2: Upload File
1. Usa il file `test_examples/test_intelligenza_artificiale.txt`
2. Trascinalo nell'area "Trascina qui il tuo file"
3. Il testo verrà caricato automaticamente
4. Clicca "🚀 Genera Riassunto"

#### 🎚️ Test 3: Parametri Personalizzati
1. Seleziona "Personalizzato" nel menu lunghezza
2. Regola gli slider:
   - Min: 30
   - Max: 200
3. Genera il riassunto

#### ⬇️ Test 4: Download
1. Dopo aver generato un riassunto
2. Clicca su uno dei pulsanti download:
   - 📄 TXT (immediato)
   - 📘 DOCX (Word)
   - 📕 PDF

### 4️⃣ Verifica le statistiche

Dopo ogni riassunto, controlla:
- Testo originale (caratteri)
- Riassunto (caratteri)
- Riduzione (%)

### 🛑 Ferma i servizi

```powershell
.\stop-all.ps1
```

## ⚡ Test Velocissimo (1 minuto)

```powershell
# 1. Avvia
.\start-all.ps1

# 2. Aspetta 15 secondi

# 3. Apri browser su http://localhost:4200

# 4. Trascina test_examples/test_intelligenza_artificiale.txt

# 5. Clicca "Genera Riassunto"

# 6. Clicca "Download TXT"

# FATTO! ✅
```

## 🐛 Problemi Comuni

### "Servizio NLP non disponibile"
- Attendi che il modello finisca di caricarsi (prima volta: ~2 minuti)
- Verifica che la porta 8000 sia libera

### "Errore CORS"
- Verifica che il backend sia su porta 8080
- Verifica che il frontend sia su porta 4200

### Download non funziona
- Ribuilda il backend: `cd Summarizer-BE; .\gradlew clean build`
- Verifica le dipendenze POI e PDFBox

## 📊 Testi di esempio inclusi

- `test_intelligenza_artificiale.txt` - Testo medio (250 parole)
- `test_ai.json` - JSON con testo
- `test_clima.json` - Testo lungo
- `test_pizza.json` - Testo breve

## ✅ Checklist Funzionalità

- [ ] ✍️ Input testo manuale
- [ ] 📎 Upload file TXT
- [ ] 🎯 Drag & Drop file
- [ ] 🎚️ Slider parametri
- [ ] 📏 Preset lunghezza (Breve/Medio/Lungo)
- [ ] 🚀 Generazione riassunto
- [ ] 📄 Download TXT
- [ ] 📘 Download DOCX
- [ ] 📕 Download PDF
- [ ] 📊 Visualizzazione statistiche
- [ ] 🔄 Reset form
- [ ] ⚠️ Messaggi errore/successo

---

**Divertiti a testare! 🎉**
