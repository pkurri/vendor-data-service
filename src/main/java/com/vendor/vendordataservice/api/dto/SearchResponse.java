package com.vendor.vendordataservice.api.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchResponse {
    // Demographics
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private LocalDate dob;
    private String sex;
    private String race;
    private String driverLicense; // DL State and Number combined

    private Double matchScore; // 0-100

    private List<CaseRecord> cases = new ArrayList<>();
}
