package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a charge within a case
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDto {
    
    @JsonProperty("charge_sequence_number")
    @Size(max = 15)
    private String chargeSequenceNumber;
    
    @JsonProperty("initial_filing_date")
    private LocalDate initialFilingDate;
    
    @JsonProperty("offense_date")
    private LocalDate offenseDate;
    
    @JsonProperty("initial_fl_statute_number")
    @Size(max = 14)
    private String initialFlStatuteNumber;
    
    @JsonProperty("initial_fl_statute_description")
    @Size(max = 60)
    private String initialFlStatuteDescription;
    
    @JsonProperty("prosecution_action_code")
    @Size(max = 1)
    private String prosecutionActionCode;
    
    @JsonProperty("prosecutor_fl_statute_number")
    @Size(max = 14)
    private String prosecutorFlStatuteNumber;
    
    @JsonProperty("prosecutor_fl_statute_description")
    @Size(max = 60)
    private String prosecutorFlStatuteDescription;
    
    @JsonProperty("prosecutor_decision_date")
    private LocalDate prosecutorDecisionDate;
    
    @JsonProperty("court_fl_statute_number")
    @Size(max = 14)
    private String courtFlStatuteNumber;
    
    @JsonProperty("court_fl_statute_description")
    @Size(max = 60)
    private String courtFlStatuteDescription;
    
    @JsonProperty("court_decision_date")
    private LocalDate courtDecisionDate;
    
    @JsonProperty("court_action_code")
    @Size(max = 1)
    private String courtActionCode;
    
    @JsonProperty("d6_date")
    private LocalDate d6Date;
    
    @JsonProperty("trial_type_code")
    private Integer trialTypeCode;
    
    @JsonProperty("traffic_disposition_code")
    @Size(max = 6)
    private String trafficDispositionCode;
    
    @JsonProperty("citation_issued_date")
    private LocalDate citationIssuedDate;
    
    @JsonProperty("citation_number")
    @Size(max = 20)
    private String citationNumber;
    
    @JsonProperty("defendant_final_plea_code")
    private Integer defendantFinalPleaCode;
    
    @JsonProperty("initial_charge_level_code")
    @Size(max = 1)
    private String initialChargeLevelCode;
    
    @JsonProperty("initial_charge_degree_code")
    @Size(max = 1)
    private String initialChargeDegreeCode;
    
    @JsonProperty("prosecutor_charge_level_code")
    @Size(max = 1)
    private String prosecutorChargeLevelCode;
    
    @JsonProperty("prosecutor_charge_degree_code")
    @Size(max = 1)
    private String prosecutorChargeDegreeCode;
    
    @JsonProperty("prosecutor_charge_count")
    private Integer prosecutorChargeCount;
    
    @JsonProperty("court_charge_level_code")
    @Size(max = 1)
    private String courtChargeLevelCode;
    
    @JsonProperty("court_charge_degree_code")
    @Size(max = 1)
    private String courtChargeDegreeCode;
    
    @JsonProperty("sentences")
    private java.util.List<SentenceDto> sentences;
}
