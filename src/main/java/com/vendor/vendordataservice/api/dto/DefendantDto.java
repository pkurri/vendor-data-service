package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a defendant in a case
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefendantDto {
    
    @JsonProperty("party_id")
    @Size(max = 50)
    private String partyId;
    
    @JsonProperty("last_name")
    @Size(max = 50)
    private String lastName;
    
    @JsonProperty("first_name")
    @Size(max = 50)
    private String firstName;
    
    @JsonProperty("middle_name")
    @Size(max = 50)
    private String middleName;
    
    @JsonProperty("dob")
    private LocalDate dob;
    
    @JsonProperty("sex")
    @Size(max = 1)
    private String sex;
    
    @JsonProperty("race")
    @Size(max = 4)
    private String race;
    
    @JsonProperty("aka")
    private java.util.List<String> aka;
}
