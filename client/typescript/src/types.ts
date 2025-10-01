/**
 * Search request parameters
 */
export interface SearchRequest {
  firstName?: string;
  lastName?: string;
  middleName?: string;
  dateOfBirth?: string;
  ssn?: string;
  caseNumber?: string;
  ucn?: string;
  filingDateFrom?: string;
  filingDateTo?: string;
  county?: string;
  caseType?: string;
  includeCharges?: boolean;
  includeSentences?: boolean;
  includeDockets?: boolean;
  includeEvents?: boolean;
  includeDefendants?: boolean;
  page?: number;
  pageSize?: number;
}

/**
 * Search response containing case records and metadata
 */
export interface SearchResponse {
  apiVersion: string;
  clientRequestId: string;
  generatedAt: string;
  page: number;
  pageSize: number;
  totalRecords: number;
  totalRecordsIsEstimate: boolean;
  warnings: string[];
  data: CaseRecord[];
}

/**
 * Case record data structure
 */
export interface CaseRecord {
  caseNumber: string;
  ucn?: string;
  courtType?: string;
  county?: string;
  filingDate?: string;
  caseStatus?: string;
  caseType?: string;
  dispositionDate?: string;
  charges?: Charge[];
  sentences?: Sentence[];
  dockets?: Docket[];
  events?: Event[];
  defendants?: Defendant[];
}

/**
 * Charge information
 */
export interface Charge {
  chargeNumber: string;
  statuteNumber?: string;
  description?: string;
  level?: string;
  degree?: string;
  disposition?: string;
}

/**
 * Sentence information
 */
export interface Sentence {
  sentenceNumber: string;
  imposedDate?: string;
  sentenceCode?: string;
  confinementDays?: number;
  probationMonths?: number;
}

/**
 * Docket entry
 */
export interface Docket {
  docketNumber: string;
  actionDate?: string;
  actionCode?: string;
  docketText?: string;
}

/**
 * Court event
 */
export interface Event {
  eventNumber: string;
  eventDate?: string;
  eventType?: string;
  judgeName?: string;
  location?: string;
}

/**
 * Defendant information
 */
export interface Defendant {
  partyId: string;
  firstName?: string;
  lastName?: string;
  middleName?: string;
  dateOfBirth?: string;
  ssn?: string;
  race?: string;
  sex?: string;
}

/**
 * OAuth2 token response
 */
export interface TokenResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope?: string;
}
