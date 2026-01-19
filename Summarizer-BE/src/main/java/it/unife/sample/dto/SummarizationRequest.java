package it.unife.sample.dto;

/**
 * DTO per la richiesta di summarization al servizio NLP.
 */
public class SummarizationRequest {
    
    private String text;
    private Integer maxLength;
    private Integer minLength;
    
    public SummarizationRequest() {
    }
    
    public SummarizationRequest(String text) {
        this.text = text;
        this.maxLength = 150;
        this.minLength = 50;
    }
    
    public SummarizationRequest(String text, Integer maxLength, Integer minLength) {
        this.text = text;
        this.maxLength = maxLength;
        this.minLength = minLength;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Integer getMaxLength() {
        return maxLength;
    }
    
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    
    public Integer getMinLength() {
        return minLength;
    }
    
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }
}
