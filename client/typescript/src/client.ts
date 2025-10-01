import axios, { AxiosInstance } from 'axios';
import axiosRetry from 'axios-retry';
import { randomUUID } from 'crypto';
import { OAuth2TokenManager } from './auth';
import { ClientConfig, resolveConfig, ResolvedClientConfig } from './config';
import { ApiError, TimeoutError } from './errors';
import { SearchRequest, SearchResponse } from './types';

/**
 * Main client for interacting with the Vendor Data Service API
 */
export class VendorDataClient {
  private readonly config: ResolvedClientConfig;
  private readonly httpClient: AxiosInstance;
  private readonly tokenManager: OAuth2TokenManager;

  constructor(config: ClientConfig) {
    this.config = resolveConfig(config);
    this.tokenManager = new OAuth2TokenManager(this.config);
    this.httpClient = this.createHttpClient();
  }

  /**
   * Performs a search using POST method with JSON body
   */
  async search(request: SearchRequest): Promise<SearchResponse> {
    const token = await this.tokenManager.getAccessToken();

    try {
      const response = await this.httpClient.post<SearchResponse>(
        '/api/v1/search',
        this.transformRequest(request),
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Request-Id': randomUUID(),
          },
        }
      );

      return this.transformResponse(response.data);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Performs a search using GET method with query parameters
   */
  async searchGet(request: SearchRequest): Promise<SearchResponse> {
    const token = await this.tokenManager.getAccessToken();

    try {
      const response = await this.httpClient.get<SearchResponse>(
        '/api/v1/search',
        {
          params: this.transformRequest(request),
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Request-Id': randomUUID(),
          },
        }
      );

      return this.transformResponse(response.data);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Checks the health status of the service
   */
  async healthCheck(): Promise<boolean> {
    try {
      const response = await this.httpClient.get('/actuator/health');
      return response.status === 200;
    } catch (error) {
      if (this.config.debug) {
        console.error('[VendorClient] Health check failed:', error);
      }
      return false;
    }
  }

  /**
   * Creates and configures the HTTP client
   */
  private createHttpClient(): AxiosInstance {
    const client = axios.create({
      baseURL: this.config.baseUrl,
      timeout: this.config.timeout,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Configure retry logic
    axiosRetry(client, {
      retries: this.config.maxRetries,
      retryDelay: (retryCount) => {
        const delay = this.config.retryDelay * Math.pow(2, retryCount - 1);
        if (this.config.debug) {
          console.log(
            `[VendorClient] Retrying request in ${delay}ms (attempt ${retryCount}/${this.config.maxRetries})`
          );
        }
        return delay;
      },
      retryCondition: (error) => {
        // Retry on network errors or 5xx errors
        return (
          axiosRetry.isNetworkOrIdempotentRequestError(error) ||
          (error.response?.status ?? 0) >= 500
        );
      },
    });

    // Add request interceptor for logging
    if (this.config.debug) {
      client.interceptors.request.use((config) => {
        console.log(
          `[VendorClient] ${config.method?.toUpperCase()} ${config.url}`
        );
        return config;
      });

      client.interceptors.response.use(
        (response) => {
          console.log(
            `[VendorClient] Response ${response.status} from ${response.config.url}`
          );
          return response;
        },
        (error) => {
          console.error('[VendorClient] Request failed:', error.message);
          return Promise.reject(error);
        }
      );
    }

    return client;
  }

  /**
   * Transforms request to use snake_case for API
   */
  private transformRequest(request: SearchRequest): Record<string, any> {
    const transformed: Record<string, any> = {};

    if (request.firstName) transformed.first_name = request.firstName;
    if (request.lastName) transformed.last_name = request.lastName;
    if (request.middleName) transformed.middle_name = request.middleName;
    if (request.dateOfBirth) transformed.date_of_birth = request.dateOfBirth;
    if (request.ssn) transformed.ssn = request.ssn;
    if (request.caseNumber) transformed.case_number = request.caseNumber;
    if (request.ucn) transformed.ucn = request.ucn;
    if (request.filingDateFrom)
      transformed.filing_date_from = request.filingDateFrom;
    if (request.filingDateTo) transformed.filing_date_to = request.filingDateTo;
    if (request.county) transformed.county = request.county;
    if (request.caseType) transformed.case_type = request.caseType;
    if (request.includeCharges !== undefined)
      transformed.include_charges = request.includeCharges;
    if (request.includeSentences !== undefined)
      transformed.include_sentences = request.includeSentences;
    if (request.includeDockets !== undefined)
      transformed.include_dockets = request.includeDockets;
    if (request.includeEvents !== undefined)
      transformed.include_events = request.includeEvents;
    if (request.includeDefendants !== undefined)
      transformed.include_defendants = request.includeDefendants;
    if (request.page) transformed.page = request.page;
    if (request.pageSize) transformed.page_size = request.pageSize;

    return transformed;
  }

  /**
   * Transforms response from snake_case to camelCase
   */
  private transformResponse(response: any): SearchResponse {
    return {
      apiVersion: response.api_version,
      clientRequestId: response.client_request_id,
      generatedAt: response.generated_at,
      page: response.page,
      pageSize: response.page_size,
      totalRecords: response.total_records,
      totalRecordsIsEstimate: response.total_records_is_estimate,
      warnings: response.warnings || [],
      data: (response.data || []).map((record: any) => ({
        caseNumber: record.case_number,
        ucn: record.ucn,
        courtType: record.court_type,
        county: record.county,
        filingDate: record.filing_date,
        caseStatus: record.case_status,
        caseType: record.case_type,
        dispositionDate: record.disposition_date,
        charges: record.charges,
        sentences: record.sentences,
        dockets: record.dockets,
        events: record.events,
        defendants: record.defendants,
      })),
    };
  }

  /**
   * Handles and transforms errors
   */
  private handleError(error: any): Error {
    if (axios.isAxiosError(error)) {
      if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
        return new TimeoutError('Request timed out', error);
      }

      const statusCode = error.response?.status;
      const responseBody = error.response?.data;

      return new ApiError(
        error.message,
        statusCode,
        responseBody,
        error
      );
    }

    return error;
  }
}
