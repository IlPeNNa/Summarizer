package it.unife.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private Integer id;
    private String summaryText;
    private Integer wordCount;
    private Integer originalLength;
    private Integer summaryLength;
    private LocalDateTime createdAt;
}
