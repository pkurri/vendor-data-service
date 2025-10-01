package com.vendor.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Search response containing case records and metadata.
 * Matches the service API specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    @JsonProperty("api_version")
    private String apiVersion;
    
    @JsonProperty("client_request_id")
    private String clientRequestId;
    
    @JsonProperty("generated_at")
    private OffsetDateTime generatedAt;
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("page_size")
    private Integer pageSize;
    
    @JsonProperty("total_records_is_estimate")
    private Boolean totalRecordsIsEstimate;
    
    @JsonProperty("warnings")
    private List<String> warnings;
    
    @JsonProperty("data")
    @Builder.Default
    private List<CaseRecord> data = new ArrayList<>();
    
    /**
     * Case record data structure matching service API.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaseRecord {
        @JsonProperty("case_id")
        private String caseId;
        
        @JsonProperty("case_number")
        private String caseNumber;
        
        @JsonProperty("ssn")
        private String ssn;
        
        @JsonProperty("court_type")
        private String courtType;
        
        @JsonProperty("severity_code")
        private String severityCode;
        
        @JsonProperty("case_type")
        private String caseType;
        
        @JsonProperty("judge_code")
        private String judgeCode;
        
        @JsonProperty("outstanding_warrant")
        private Boolean outstandingWarrant;
        
        @JsonProperty("reopen_date")
        private LocalDate reopenDate;
        
        @JsonProperty("reopen_reason")
        private String reopenReason;
        
        @JsonProperty("last_docket_date")
        private LocalDate lastDocketDate;
        
        @JsonProperty("disposition_date")
        private LocalDate dispositionDate;
        
        @JsonProperty("contested")
        private Boolean contested;
        
        @JsonProperty("jury_trial")
        private Boolean juryTrial;
        
        @JsonProperty("judge_code_at_disposition")
        private String judgeCodeAtDisposition;
        
        @JsonProperty("case_system_entry_date")
        private LocalDate caseSystemEntryDate;
        
        @JsonProperty("case_status_code")
        private Integer caseStatusCode;
        
        @JsonProperty("clerk_case_number")
        private String clerkCaseNumber;
        
        @JsonProperty("clerk_file_date")
        private LocalDate clerkFileDate;
        
        @JsonProperty("filed_date")
        private LocalDate filedDate;
        
        @JsonProperty("filed_date_from")
        private LocalDate filedDateFrom;
        
        @JsonProperty("filed_date_to")
        private LocalDate filedDateTo;
        
        @JsonProperty("county_id")
        private Integer countyId;
        
        @JsonProperty("ucn")
        private String ucn;
        
        @JsonProperty("last_name")
        private String lastName;
        
        @JsonProperty("first_name")
        private String firstName;
        
        @JsonProperty("middle_name")
        private String middleName;
        
        @JsonProperty("suffix_code")
        private String suffixCode;
        
        @JsonProperty("name_type_code")
        private String nameTypeCode;
        
        @JsonProperty("date_of_birth")
        private LocalDate dateOfBirth;
        
        @JsonProperty("sex_code")
        private String sexCode;
        
        @JsonProperty("race_code")
        private String raceCode;
        
        @JsonProperty("place_of_birth")
        private String placeOfBirth;
        
        @JsonProperty("date_of_death")
        private LocalDate dateOfDeath;
        
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("charges")
        private List<Charge> charges;
        
        @JsonProperty("dockets")
        private List<Docket> dockets;
        
        @JsonProperty("events")
        private List<Event> events;
        
        @JsonProperty("defendants")
        private List<Defendant> defendants;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charge {
        @JsonProperty("charge_sequence_number")
        private String chargeSequenceNumber;
        
        @JsonProperty("initial_filing_date")
        private LocalDate initialFilingDate;
        
        @JsonProperty("offense_date")
        private LocalDate offenseDate;
        
        @JsonProperty("initial_fl_statute_number")
        private String initialFlStatuteNumber;
        
        @JsonProperty("initial_fl_statute_description")
        private String initialFlStatuteDescription;
        
        @JsonProperty("prosecution_action_code")
        private String prosecutionActionCode;
        
        @JsonProperty("prosecutor_fl_statute_number")
        private String prosecutorFlStatuteNumber;
        
        @JsonProperty("prosecutor_fl_statute_description")
        private String prosecutorFlStatuteDescription;
        
        @JsonProperty("prosecutor_decision_date")
        private LocalDate prosecutorDecisionDate;
        
        @JsonProperty("court_fl_statute_number")
        private String courtFlStatuteNumber;
        
        @JsonProperty("court_fl_statute_description")
        private String courtFlStatuteDescription;
        
        @JsonProperty("court_decision_date")
        private LocalDate courtDecisionDate;
        
        @JsonProperty("court_action_code")
        private String courtActionCode;
        
        @JsonProperty("d6_date")
        private LocalDate d6Date;
        
        @JsonProperty("trial_type_code")
        private Integer trialTypeCode;
        
        @JsonProperty("traffic_disposition_code")
        private String trafficDispositionCode;
        
        @JsonProperty("citation_issued_date")
        private LocalDate citationIssuedDate;
        
        @JsonProperty("citation_number")
        private String citationNumber;
        
        @JsonProperty("defendant_final_plea_code")
        private Integer defendantFinalPleaCode;
        
        @JsonProperty("initial_charge_level_code")
        private String initialChargeLevelCode;
        
        @JsonProperty("initial_charge_degree_code")
        private String initialChargeDegreeCode;
        
        @JsonProperty("prosecutor_charge_level_code")
        private String prosecutorChargeLevelCode;
        
        @JsonProperty("prosecutor_charge_degree_code")
        private String prosecutorChargeDegreeCode;
        
        @JsonProperty("prosecutor_charge_count")
        private Integer prosecutorChargeCount;
        
        @JsonProperty("court_charge_level_code")
        private String courtChargeLevelCode;
        
        @JsonProperty("court_charge_degree_code")
        private String courtChargeDegreeCode;
        
        @JsonProperty("sentences")
        private List<Sentence> sentences;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sentence {
        @JsonProperty("sentence_sequence_number")
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
        private String lengthOfSentenceConfinement;
        
        @JsonProperty("confinement_type_code")
        private String confinementTypeCode;
        
        @JsonProperty("judge_code_at_sentence")
        private String judgeCodeAtSentence;
        
        @JsonProperty("division")
        private String division;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Docket {
        @JsonProperty("docket_id")
        private String docketId;
        
        @JsonProperty("docket_action_date")
        private LocalDate docketActionDate;
        
        @JsonProperty("docket_code")
        private String docketCode;
        
        @JsonProperty("standard_docket_code")
        private Integer standardDocketCode;
        
        @JsonProperty("docket_text")
        private String docketText;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Event {
        @JsonProperty("event_id")
        private String eventId;
        
        @JsonProperty("court_appearance_date")
        private LocalDateTime courtAppearanceDate;
        
        @JsonProperty("judge_code")
        private String judgeCode;
        
        @JsonProperty("court_event_description")
        private String courtEventDescription;
        
        @JsonProperty("standard_court_event_code")
        private Integer standardCourtEventCode;
        
        @JsonProperty("court_appearance_time")
        private String courtAppearanceTime;
        
        @JsonProperty("court_location")
        private String courtLocation;
        
        @JsonProperty("court_room")
        private String courtRoom;
        
        @JsonProperty("prosecutor")
        private String prosecutor;
        
        @JsonProperty("defendant_attorney")
        private String defendantAttorney;
        
        @JsonProperty("division")
        private String division;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Defendant {
        @JsonProperty("party_id")
        private String partyId;
        
        @JsonProperty("last_name")
        private String lastName;
        
        @JsonProperty("first_name")
        private String firstName;
        
        @JsonProperty("middle_name")
        private String middleName;
        
        @JsonProperty("dob")
        private LocalDate dob;
        
        @JsonProperty("sex")
        private String sex;
        
        @JsonProperty("race")
        private String race;
        
        @JsonProperty("aka")
        private List<String> aka;
    }
}
