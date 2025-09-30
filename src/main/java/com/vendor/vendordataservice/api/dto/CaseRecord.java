package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a complete case record with all nested objects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseRecord {
    
    @JsonProperty("case_id")
    @Size(max = 50)
    private String caseId;
    
    @JsonProperty("case_number")
    @Size(max = 50)
    private String caseNumber;
    
    @JsonProperty("ssn")
    @Size(max = 11)
    private String ssn;
    
    @JsonProperty("court_type")
    @Size(max = 50)
    private String courtType;
    
    @JsonProperty("severity_code")
    @Size(max = 50)
    private String severityCode;
    
    @JsonProperty("case_type")
    @Size(max = 40)
    private String caseType;
    
    @JsonProperty("judge_code")
    @Size(max = 30)
    private String judgeCode;
    
    @JsonProperty("outstanding_warrant")
    private Boolean outstandingWarrant;
    
    @JsonProperty("reopen_date")
    private LocalDate reopenDate;
    
    @JsonProperty("reopen_reason")
    @Size(max = 1)
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
    @Size(max = 30)
    private String judgeCodeAtDisposition;
    
    @JsonProperty("case_system_entry_date")
    private LocalDate caseSystemEntryDate;
    
    @JsonProperty("case_status_code")
    private Integer caseStatusCode;
    
    @JsonProperty("clerk_case_number")
    @Size(max = 20)
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
    @Size(max = 20)
    private String ucn;
    
    @JsonProperty("last_name")
    @Size(max = 30)
    private String lastName;
    
    @JsonProperty("first_name")
    @Size(max = 20)
    private String firstName;
    
    @JsonProperty("middle_name")
    @Size(max = 20)
    private String middleName;
    
    @JsonProperty("suffix_code")
    @Size(max = 1)
    private String suffixCode;
    
    @JsonProperty("name_type_code")
    @Size(max = 1)
    private String nameTypeCode;
    
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    
    @JsonProperty("sex_code")
    @Size(max = 1)
    private String sexCode;
    
    @JsonProperty("race_code")
    @Size(max = 1)
    private String raceCode;
    
    @JsonProperty("place_of_birth")
    @Size(max = 60)
    private String placeOfBirth;
    
    @JsonProperty("date_of_death")
    private LocalDate dateOfDeath;
    
    @JsonProperty("country")
    @Size(max = 2)
    private String country;
    
    @JsonProperty("charges")
    private List<ChargeDto> charges;
    
    @JsonProperty("dockets")
    private List<DocketDto> dockets;
    
    @JsonProperty("events")
    private List<EventDto> events;
    
    @JsonProperty("defendants")
    private List<DefendantDto> defendants;
}
