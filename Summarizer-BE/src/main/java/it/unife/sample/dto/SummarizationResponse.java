package it.unife.sample.dto;

/**
 * DTO per la risposta dal servizio NLP.
 */
public class SummarizationResponse {
    
    private String summary;
    private Integer originalLength;
    private Integer summaryLength;
    
    public SummarizationResponse() {
    }
    
    public SummarizationResponse(String summary, Integer originalLength, Integer summaryLength) {
        this.summary = summary;
        this.originalLength = originalLength;
        this.summaryLength = summaryLength;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public Integer getOriginalLength() {
        return originalLength;
    }
    
    public void setOriginalLength(Integer originalLength) {
        this.originalLength = originalLength;
    }
    
    public Integer getSummaryLength() {
        return summaryLength;
    }
    
    public void setSummaryLength(Integer summaryLength) {
        this.summaryLength = summaryLength;
    }
}
