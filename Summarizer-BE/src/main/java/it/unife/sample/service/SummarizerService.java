package it.unife.sample.service;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationResponse;
import it.unife.sample.dto.SummaryResponse;
import it.unife.sample.entity.Summary;
import it.unife.sample.entity.User;
import it.unife.sample.repository.SummaryRepository;
import it.unife.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer per la logica di business della summarization.
 * Utilizza il NlpServiceClient per comunicare con il servizio Python.
 */
@Service
@RequiredArgsConstructor
public class SummarizerService {
    
    private final NlpServiceClient nlpServiceClient;
    private final SummaryRepository summaryRepository;
    private final UserRepository userRepository;
    
    /**
     * Riassume un testo utilizzando il servizio NLP.
     * 
     * @param text Testo da riassumere
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeText(String text) throws NlpServiceClient.NlpServiceException {
        validateText(text);
        return nlpServiceClient.summarize(text);
    }
    
    /**
     * Riassume un testo con parametri personalizzati.
     * 
     * @param text Testo da riassumere
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeText(String text, int maxLength, int minLength) 
            throws NlpServiceClient.NlpServiceException {
        return summarizeText(text, maxLength, minLength, "paragraph");
    }
    
    /**
     * Riassume un testo con parametri personalizzati e formato.
     * 
     * @param text Testo da riassumere
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @param format Formato del riassunto (paragraph, bullet)
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeText(String text, int maxLength, int minLength, String format) 
            throws NlpServiceClient.NlpServiceException {
        validateText(text);
        validateLengthParameters(maxLength, minLength);
        return nlpServiceClient.summarize(text, maxLength, minLength, format);
    }
    
    /**
     * Genera un file DOCX contenente il testo.
     * 
     * @param text Testo da inserire nel DOCX
     * @return Array di byte del file DOCX
     * @throws IOException se c'è un errore nella generazione
     */
    public byte[] generateDocx(String text) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Titolo
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Riassunto Generato");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.addBreak();
            
            // Contenuto
            XWPFParagraph content = document.createParagraph();
            XWPFRun contentRun = content.createRun();
            contentRun.setText(text);
            contentRun.setFontSize(12);
            
            document.write(out);
            return out.toByteArray();
        }
    }
    
    /**
     * Genera un file PDF contenente il testo.
     * 
     * @param text Testo da inserire nel PDF
     * @return Array di byte del file PDF
     * @throws IOException se c'è un errore nella generazione
     */
    public byte[] generatePdf(String text) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);
                
                // Titolo
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.showText("Riassunto Generato");
                contentStream.newLine();
                contentStream.newLine();
                
                // Contenuto - gestione interruzioni di riga
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                String[] lines = wrapText(text, 80);
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
                
                contentStream.endText();
            }
            
            document.save(out);
            return out.toByteArray();
        }
    }
    
    /**
     * Divide il testo in linee di lunghezza massima specificata.
     */
    private String[] wrapText(String text, int maxLineLength) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                result.append(currentLine.toString().trim()).append("\n");
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }
        
        if (currentLine.length() > 0) {
            result.append(currentLine.toString().trim());
        }
        
        return result.toString().split("\n");
    }
    
    /**
     * Verifica se il servizio NLP è disponibile.
     * 
     * @return true se disponibile, false altrimenti
     */
    public boolean isNlpServiceAvailable() {
        return nlpServiceClient.isHealthy();
    }
    
    /**
     * Estrae il testo da un file (supporta TXT, PDF, DOCX) senza riassumerlo.
     * 
     * @param fileContent Contenuto del file come array di byte
     * @param filename Nome del file
     * @return Testo estratto dal file
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public String extractTextFromFile(byte[] fileContent, String filename) 
            throws NlpServiceClient.NlpServiceException {
        validateFileType(filename);
        return nlpServiceClient.extractTextFromFile(fileContent, filename);
    }
    
    /**
     * Riassume il contenuto di un file (supporta TXT, PDF, DOCX).
     * 
     * @param fileContent Contenuto del file come array di byte
     * @param filename Nome del file
     * @param maxLength Lunghezza massima del riassunto
     * @param minLength Lunghezza minima del riassunto
     * @param format Formato del riassunto (paragraph, bullet)
     * @return Risposta con il riassunto
     * @throws NlpServiceClient.NlpServiceException se il servizio NLP fallisce
     */
    public SummarizationResponse summarizeFile(byte[] fileContent, String filename, 
                                                int maxLength, int minLength, String format) 
            throws NlpServiceClient.NlpServiceException {
        validateLengthParameters(maxLength, minLength);
        validateFileType(filename);
        return nlpServiceClient.summarizeFile(fileContent, filename, maxLength, minLength, format);
    }
    
    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo non può essere vuoto");
        }
        
        if (text.length() < 100) {
            throw new IllegalArgumentException("Il testo è troppo corto per essere riassunto (minimo 100 caratteri)");
        }
    }
    
    private void validateLengthParameters(int maxLength, int minLength) {
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength deve essere maggiore di minLength");
        }
        
        if (minLength < 10) {
            throw new IllegalArgumentException("minLength deve essere almeno 10");
        }
        
        if (maxLength > 500) {
            throw new IllegalArgumentException("maxLength non può superare 500");
        }
    }
    
    private void validateFileType(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Nome file non valido");
        }
        
        String lowerFilename = filename.toLowerCase();
        if (!lowerFilename.endsWith(".txt") && 
            !lowerFilename.endsWith(".pdf") && 
            !lowerFilename.endsWith(".docx")) {
            throw new IllegalArgumentException("Formato file non supportato. Usa .txt, .pdf o .docx");
        }
    }
    
    /**
     * Salva un riassunto nel database per un utente autenticato.
     * 
     * @param userEmail Email dell'utente
     * @param originalText Testo originale
     * @param summaryText Testo riassunto
     * @param wordCount Numero di parole del riassunto
     * @return Summary entity salvata
     */
    @Transactional
    public Summary saveSummary(String userEmail, String originalText, String summaryText, int wordCount) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Summary summary = new Summary();
        summary.setUser(user);
        summary.setOriginalText(originalText);
        summary.setSummaryText(summaryText);
        summary.setOriginalLength(originalText.length());
        summary.setSummaryLength(summaryText.length());
        summary.setWordCount(wordCount);
        
        return summaryRepository.save(summary);
    }
    
    /**
     * Recupera gli ultimi N riassunti NON eliminati di un utente.
     * 
     * @param userEmail Email dell'utente
     * @param limit Numero massimo di riassunti da recuperare
     * @return Lista di riassunti
     */
    public List<SummaryResponse> getUserSummaries(String userEmail, int limit) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        List<Summary> summaries = summaryRepository.findTopActiveByUserId(
                user.getId(), 
                PageRequest.of(0, limit)
        );
        
        return summaries.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Conta i riassunti NON eliminati di un utente.
     * 
     * @param userEmail Email dell'utente
     * @return Numero di riassunti attivi
     */
    public long countUserSummaries(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        return summaryRepository.countActiveByUserId(user.getId());
    }
    
    /**
     * Soft delete di un riassunto.
     * 
     * @param summaryId ID del riassunto
     * @param userEmail Email dell'utente (per verifica proprietà)
     */
    @Transactional
    public void deleteSummary(Integer summaryId, String userEmail) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Riassunto non trovato"));
        
        if (!summary.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Non autorizzato a eliminare questo riassunto");
        }
        
        summary.setDeletedAt(java.time.LocalDateTime.now());
        summaryRepository.save(summary);
    }
    
    private SummaryResponse toSummaryResponse(Summary summary) {
        return new SummaryResponse(
                summary.getId(),
                summary.getSummaryText(),
                summary.getWordCount(),
                summary.getOriginalLength(),
                summary.getSummaryLength(),
                summary.getCreatedAt()
        );
    }
}
