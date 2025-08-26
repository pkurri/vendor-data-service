package com.vendor.vendordataservice.repository.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VendorCaseRow {
    // Person/Demographics
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private LocalDate dob;
    private String sex;
    private String race;
    private String dlState;
    private String dlNumber;

    // Case
    private String county;
    private String state;
    private String caseNumber;
    private String charge;
    private String chargeType;
    private String dispositionType;
    private LocalDate dispositionDate;
    private LocalDate fileDate;
    private LocalDate offenseDate;
}
