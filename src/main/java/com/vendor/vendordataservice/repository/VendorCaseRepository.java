package com.vendor.vendordataservice.repository;

import com.vendor.vendordataservice.repository.model.VendorCaseRow;

import java.time.LocalDate;
import java.util.List;

public interface VendorCaseRepository {
    List<VendorCaseRow> search(String ssnLast4,
                               LocalDate dob,
                               String firstName,
                               String middleName,
                               String lastName,
                               int offset,
                               int limit);
}
