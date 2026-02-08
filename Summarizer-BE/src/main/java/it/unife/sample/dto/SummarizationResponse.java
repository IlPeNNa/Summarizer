package it.unife.sample.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO per la risposta. Accetta snake_case da Python e invia camelCase al frontend.
 */
public class SummarizationResponse {
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("originalLength")
    @JsonAlias("original_length")
    private Integer originalLength;
    
    @JsonProperty("summaryLength")
    @JsonAlias("summary_length")
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
