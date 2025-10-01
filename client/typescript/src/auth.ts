import axios, { AxiosInstance } from 'axios';
import { ResolvedClientConfig } from './config';
import { AuthenticationError } from './errors';
import { TokenResponse } from './types';

/**
 * Manages OAuth2 access tokens for M2M authentication
 */
export class OAuth2TokenManager {
  private accessToken: string | null = null;
  private tokenExpiry: Date | null = null;
  private readonly httpClient: AxiosInstance;

  constructor(private readonly config: ResolvedClientConfig) {
    this.httpClient = axios.create({
      timeout: config.timeout,
    });
  }

  /**
   * Gets a valid access token, refreshing if necessary
   */
  async getAccessToken(): Promise<string> {
    if (this.needsRefresh()) {
      await this.refreshToken();
    }
    return this.accessToken!;
  }

  /**
   * Forces a token refresh
   */
  async refreshToken(): Promise<void> {
    if (this.config.debug) {
      console.log('[VendorClient] Refreshing OAuth2 token');
    }

    try {
      const params = new URLSearchParams();
      params.append('grant_type', 'client_credentials');
      params.append('client_id', this.config.clientId);
      params.append('client_secret', this.config.clientSecret);
      params.append('scope', this.config.scope);

      const response = await this.httpClient.post<TokenResponse>(
        this.config.tokenUrl,
        params,
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        }
      );

      const tokenData = response.data;

      if (!tokenData.access_token) {
        throw new AuthenticationError('Token response missing access_token');
      }

      this.accessToken = tokenData.access_token;

      // Set expiry with buffer
      const expiresIn = tokenData.expires_in || 3600;
      const expirySeconds = expiresIn - this.config.tokenRefreshBuffer;
      this.tokenExpiry = new Date(Date.now() + expirySeconds * 1000);

      if (this.config.debug) {
        console.log(
          `[VendorClient] Token refreshed successfully, expires at ${this.tokenExpiry.toISOString()}`
        );
      }
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const message = error.response?.data?.error_description || error.message;
        throw new AuthenticationError(
          `Failed to refresh token: ${message}`,
          error
        );
      }
      throw new AuthenticationError('Failed to refresh token', error as Error);
    }
  }

  /**
   * Checks if token needs refresh
   */
  private needsRefresh(): boolean {
    return (
      !this.accessToken ||
      !this.tokenExpiry ||
      new Date() >= this.tokenExpiry
    );
  }
}
