-- Comprehensive SQL Server schema for court case data
-- Based on JSON schema specification for vendor-data-service
-- This schema supports the complete data model with all nested objects

-- Main Cases table
CREATE TABLE dbo.cases (
    case_id BIGINT IDENTITY PRIMARY KEY,
    case_number NVARCHAR(50) NOT NULL,
    ucn NVARCHAR(20) NULL,
    county_id INT NULL,
    court_type NVARCHAR(50) NULL,
    severity_code NVARCHAR(50) NULL,
    case_type NVARCHAR(40) NULL,
    case_status_code INT NULL,
    judge_code NVARCHAR(30) NULL,
    judge_code_at_disposition NVARCHAR(30) NULL,
    outstanding_warrant BIT NULL,
    contested BIT NULL,
    jury_trial BIT NULL,
    
    -- Dates
    filed_date DATE NULL,
    clerk_file_date DATE NULL,
    reopen_date DATE NULL,
    last_docket_date DATE NULL,
    disposition_date DATE NULL,
    case_system_entry_date DATE NULL,
    
    -- Person demographics (denormalized for search performance)
    last_name NVARCHAR(30) NULL,
    first_name NVARCHAR(20) NULL,
    middle_name NVARCHAR(20) NULL,
    suffix_code NVARCHAR(1) NULL,
    name_type_code NVARCHAR(1) NULL,
    date_of_birth DATE NULL,
    sex_code NVARCHAR(1) NULL,
    race_code NVARCHAR(1) NULL,
    place_of_birth NVARCHAR(60) NULL,
    date_of_death DATE NULL,
    country NVARCHAR(2) NULL,
    ssn NVARCHAR(11) NULL,
    
    -- Other fields
    clerk_case_number NVARCHAR(20) NULL,
    reopen_reason NVARCHAR(1) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT UQ_case_number UNIQUE (case_number)
);

-- Defendants table (party information)
CREATE TABLE dbo.defendants (
    defendant_id BIGINT IDENTITY PRIMARY KEY,
    case_id BIGINT NOT NULL,
    party_id NVARCHAR(50) NULL,
    last_name NVARCHAR(50) NULL,
    first_name NVARCHAR(50) NULL,
    middle_name NVARCHAR(50) NULL,
    dob DATE NULL,
    sex NVARCHAR(1) NULL,
    race NVARCHAR(4) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT FK_defendants_case FOREIGN KEY (case_id) REFERENCES dbo.cases(case_id) ON DELETE CASCADE
);

-- Defendant AKA (aliases)
CREATE TABLE dbo.defendant_aka (
    aka_id BIGINT IDENTITY PRIMARY KEY,
    defendant_id BIGINT NOT NULL,
    aka_name NVARCHAR(150) NOT NULL,
    
    CONSTRAINT FK_defendant_aka FOREIGN KEY (defendant_id) REFERENCES dbo.defendants(defendant_id) ON DELETE CASCADE
);

-- Charges table
CREATE TABLE dbo.charges (
    charge_id BIGINT IDENTITY PRIMARY KEY,
    case_id BIGINT NOT NULL,
    charge_sequence_number NVARCHAR(15) NULL,
    
    -- Dates
    initial_filing_date DATE NULL,
    offense_date DATE NULL,
    prosecutor_decision_date DATE NULL,
    court_decision_date DATE NULL,
    d6_date DATE NULL,
    citation_issued_date DATE NULL,
    
    -- Initial charge
    initial_fl_statute_number NVARCHAR(14) NULL,
    initial_fl_statute_description NVARCHAR(60) NULL,
    initial_charge_level_code NVARCHAR(1) NULL,
    initial_charge_degree_code NVARCHAR(1) NULL,
    
    -- Prosecutor charge
    prosecution_action_code NVARCHAR(1) NULL,
    prosecutor_fl_statute_number NVARCHAR(14) NULL,
    prosecutor_fl_statute_description NVARCHAR(60) NULL,
    prosecutor_charge_level_code NVARCHAR(1) NULL,
    prosecutor_charge_degree_code NVARCHAR(1) NULL,
    prosecutor_charge_count INT NULL,
    
    -- Court charge
    court_fl_statute_number NVARCHAR(14) NULL,
    court_fl_statute_description NVARCHAR(60) NULL,
    court_action_code NVARCHAR(1) NULL,
    court_charge_level_code NVARCHAR(1) NULL,
    court_charge_degree_code NVARCHAR(1) NULL,
    
    -- Other
    trial_type_code INT NULL,
    traffic_disposition_code NVARCHAR(6) NULL,
    citation_number NVARCHAR(20) NULL,
    defendant_final_plea_code INT NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT FK_charges_case FOREIGN KEY (case_id) REFERENCES dbo.cases(case_id) ON DELETE CASCADE
);

-- Sentences table
CREATE TABLE dbo.sentences (
    sentence_id BIGINT IDENTITY PRIMARY KEY,
    charge_id BIGINT NOT NULL,
    sentence_sequence_number NVARCHAR(15) NULL,
    sentence_status_code INT NULL,
    sentence_imposed_date DATE NULL,
    sentence_effective_date DATE NULL,
    sentence_code INT NULL,
    length_of_sentence_confinement NVARCHAR(1) NULL,
    confinement_type_code NVARCHAR(1) NULL,
    judge_code_at_sentence NVARCHAR(30) NULL,
    division NVARCHAR(30) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT FK_sentences_charge FOREIGN KEY (charge_id) REFERENCES dbo.charges(charge_id) ON DELETE CASCADE
);

-- Dockets table
CREATE TABLE dbo.dockets (
    docket_id_pk BIGINT IDENTITY PRIMARY KEY,
    case_id BIGINT NOT NULL,
    docket_id NVARCHAR(14) NULL,
    docket_action_date DATE NULL,
    docket_code NVARCHAR(20) NULL,
    standard_docket_code INT NULL,
    docket_text NVARCHAR(MAX) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT FK_dockets_case FOREIGN KEY (case_id) REFERENCES dbo.cases(case_id) ON DELETE CASCADE
);

-- Court Events table
CREATE TABLE dbo.court_events (
    event_id_pk BIGINT IDENTITY PRIMARY KEY,
    case_id BIGINT NOT NULL,
    event_id NVARCHAR(50) NULL,
    court_appearance_date DATETIME2 NULL,
    court_appearance_time NVARCHAR(50) NULL,
    judge_code NVARCHAR(30) NULL,
    court_event_description NVARCHAR(40) NULL,
    standard_court_event_code INT NULL,
    court_location NVARCHAR(30) NULL,
    court_room NVARCHAR(6) NULL,
    prosecutor NVARCHAR(30) NULL,
    defendant_attorney NVARCHAR(30) NULL,
    division NVARCHAR(30) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT FK_court_events_case FOREIGN KEY (case_id) REFERENCES dbo.cases(case_id) ON DELETE CASCADE
);

-- Indexes for search performance
CREATE INDEX IX_cases_name_dob ON dbo.cases (last_name, first_name, date_of_birth);
CREATE INDEX IX_cases_ssn ON dbo.cases (ssn) WHERE ssn IS NOT NULL;
CREATE INDEX IX_cases_filed_date ON dbo.cases (filed_date);
CREATE INDEX IX_cases_county ON dbo.cases (county_id);
CREATE INDEX IX_cases_case_type ON dbo.cases (case_type);
CREATE INDEX IX_cases_ucn ON dbo.cases (ucn) WHERE ucn IS NOT NULL;

CREATE INDEX IX_defendants_case ON dbo.defendants (case_id);
CREATE INDEX IX_defendants_name ON dbo.defendants (last_name, first_name);

CREATE INDEX IX_charges_case ON dbo.charges (case_id);
CREATE INDEX IX_charges_offense_date ON dbo.charges (offense_date);

CREATE INDEX IX_sentences_charge ON dbo.sentences (charge_id);

CREATE INDEX IX_dockets_case ON dbo.dockets (case_id);
CREATE INDEX IX_dockets_date ON dbo.dockets (docket_action_date);

CREATE INDEX IX_court_events_case ON dbo.court_events (case_id);
CREATE INDEX IX_court_events_date ON dbo.court_events (court_appearance_date);

-- Full-text search indexes (optional, for advanced text search)
-- CREATE FULLTEXT CATALOG ft_catalog AS DEFAULT;
-- CREATE FULLTEXT INDEX ON dbo.cases(last_name, first_name) KEY INDEX PK__cases;
-- CREATE FULLTEXT INDEX ON dbo.dockets(docket_text) KEY INDEX PK__dockets;

GO
