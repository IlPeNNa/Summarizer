package it.unife.sample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO per la richiesta di summarization al servizio NLP.
 */
public class SummarizationRequest {
    
    @JsonProperty("input")
    private String input;
    
    @JsonProperty("maxLength")
    private Integer maxLength;
    
    @JsonProperty("minLength")
    private Integer minLength;
    
    @JsonProperty("format")
    private String format;
    
    public SummarizationRequest() {
    }
    
    public SummarizationRequest(String input) {
        this.input = input;
        this.maxLength = 150;
        this.minLength = 50;
    }
    
    public SummarizationRequest(String input, Integer maxLength, Integer minLength) {
        this.input = input;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.format = "paragraph";
    }
    
    public SummarizationRequest(String input, Integer maxLength, Integer minLength, String format) {
        this.input = input;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.format = format != null ? format : "paragraph";
    }
    
    public String getInput() {
        return input;
    }
    
    public void setInput(String input) {
        this.input = input;
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
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
}
