package com.vendor.vendordataservice.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchRequest {
    @Pattern(regexp = "^$|^[0-9]{4}$", message = "ssnLast4 must be exactly 4 digits")
    private String ssnLast4;

    // ISO-8601 date (yyyy-MM-dd)
    private LocalDate dob;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @Size(max = 100)
    private String lastName;
}
