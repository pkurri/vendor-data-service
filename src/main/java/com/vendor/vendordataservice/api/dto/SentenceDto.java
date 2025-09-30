package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a sentence within a charge
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentenceDto {
    
    @JsonProperty("sentence_sequence_number")
    @Size(max = 15)
    private String sentenceSequenceNumber;
    
    @JsonProperty("sentence_status_code")
    private Integer sentenceStatusCode;
    
    @JsonProperty("sentence_imposed_date")
    private LocalDate sentenceImposedDate;
    
    @JsonProperty("sentence_effective_date")
    private LocalDate sentenceEffectiveDate;
    
    @JsonProperty("sentence_code")
    private Integer sentenceCode;
    
    @JsonProperty("length_of_sentence_confinement")
    @Size(max = 1)
    private String lengthOfSentenceConfinement;
    
    @JsonProperty("confinement_type_code")
    @Size(max = 1)
    private String confinementTypeCode;
    
    @JsonProperty("judge_code_at_sentence")
    @Size(max = 30)
    private String judgeCodeAtSentence;
    
    @JsonProperty("division")
    @Size(max = 30)
    private String division;
}
