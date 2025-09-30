package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a docket entry in a case
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocketDto {
    
    @JsonProperty("docket_id")
    @Size(max = 14)
    private String docketId;
    
    @JsonProperty("docket_action_date")
    private LocalDate docketActionDate;
    
    @JsonProperty("docket_code")
    @Size(max = 20)
    private String docketCode;
    
    @JsonProperty("standard_docket_code")
    private Integer standardDocketCode;
    
    @JsonProperty("docket_text")
    @Size(max = 8000)
    private String docketText;
}
