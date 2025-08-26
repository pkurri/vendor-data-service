-- Example SQL Server schema for vendor_cases table
-- Adjust schema, constraints, and indexes to your needs.

CREATE TABLE dbo.vendor_cases (
    id               BIGINT IDENTITY PRIMARY KEY,
    -- Person/Demographics
    first_name       NVARCHAR(100) NULL,
    middle_name      NVARCHAR(100) NULL,
    last_name       NVARCHAR(100) NULL,
    suffix           NVARCHAR(20)  NULL,
    dob              DATE NULL,
    sex              NVARCHAR(10)  NULL,
    race             NVARCHAR(50)  NULL,
    dl_state         NVARCHAR(10)  NULL,
    dl_number        NVARCHAR(64)  NULL,
    -- Caution: do not store full SSN. If last4 is present, store as text.
    ssn_last4        CHAR(4) NULL,

    -- Case
    county           NVARCHAR(100) NULL,
    state            NVARCHAR(2)   NULL,
    case_number      NVARCHAR(64)  NULL,
    charge           NVARCHAR(256) NULL,
    charge_type      NVARCHAR(50)  NULL,
    disposition_type NVARCHAR(128) NULL,
    disposition_date DATE NULL,
    file_date        DATE NULL,
    offense_date     DATE NULL
);

-- Helpful indexes for search patterns used
CREATE INDEX IX_vendor_cases_name_dob ON dbo.vendor_cases (last_name, first_name, dob);
CREATE INDEX IX_vendor_cases_ssn_last4 ON dbo.vendor_cases (ssn_last4);
CREATE INDEX IX_vendor_cases_case_number ON dbo.vendor_cases (case_number);
CREATE INDEX IX_vendor_cases_dl ON dbo.vendor_cases (dl_state, dl_number);
