package it.unife.sample.service;

import it.unife.sample.client.NlpServiceClient;
import it.unife.sample.dto.SummarizationResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service layer per la logica di business della summarization.
 * Utilizza il NlpServiceClient per comunicare con il servizio Python.
 */
@Service
public class SummarizerService {
    
    private final NlpServiceClient nlpServiceClient;
    
    public SummarizerService(NlpServiceClient nlpServiceClient) {
        this.nlpServiceClient = nlpServiceClient;
    }
    
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
        validateText(text);
        validateLengthParameters(maxLength, minLength);
        return nlpServiceClient.summarize(text, maxLength, minLength);
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
}
