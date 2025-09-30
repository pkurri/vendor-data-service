package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a court event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    
    @JsonProperty("event_id")
    @Size(max = 50)
    private String eventId;
    
    @JsonProperty("court_appearance_date")
    private LocalDateTime courtAppearanceDate;
    
    @JsonProperty("judge_code")
    @Size(max = 30)
    private String judgeCode;
    
    @JsonProperty("court_event_description")
    @Size(max = 40)
    private String courtEventDescription;
    
    @JsonProperty("standard_court_event_code")
    private Integer standardCourtEventCode;
    
    @JsonProperty("court_appearance_time")
    @Size(max = 50)
    private String courtAppearanceTime;
    
    @JsonProperty("court_location")
    @Size(max = 30)
    private String courtLocation;
    
    @JsonProperty("court_room")
    @Size(max = 6)
    private String courtRoom;
    
    @JsonProperty("prosecutor")
    @Size(max = 30)
    private String prosecutor;
    
    @JsonProperty("defendant_attorney")
    @Size(max = 30)
    private String defendantAttorney;
    
    @JsonProperty("division")
    @Size(max = 30)
    private String division;
}
