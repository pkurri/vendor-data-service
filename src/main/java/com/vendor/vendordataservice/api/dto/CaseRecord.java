package com.vendor.vendordataservice.api.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CaseRecord {
    private String county;
    private String state; // e.g., FL
    private String caseNumber;
    private String charge;
    private String chargeType; // Felony, Misdemeanor, Traffic, etc
    private String dispositionType;
    private LocalDate dispositionDate;
    private LocalDate fileDate;
    private LocalDate offenseDate;
}
