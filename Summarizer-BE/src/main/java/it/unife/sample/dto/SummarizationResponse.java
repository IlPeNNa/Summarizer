package it.unife.sample.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la risposta. Accetta snake_case da Python e invia camelCase al frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationResponse {
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("originalLength")
    @JsonAlias("original_length")
    private Integer originalLength;
    
    @JsonProperty("summaryLength")
    @JsonAlias("summary_length")
    private Integer summaryLength;
    
    @JsonProperty("wordCount")
    @JsonAlias("word_count")
    private Integer wordCount;
    
    @JsonProperty("summaryId")
    private Integer summaryId;
}
