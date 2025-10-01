/**
 * Configuration options for the Vendor Data Service client
 */
export interface ClientConfig {
  /**
   * Base URL of the Vendor Data Service API
   */
  baseUrl: string;

  /**
   * OAuth2 client ID for M2M authentication
   */
  clientId: string;

  /**
   * OAuth2 client secret for M2M authentication
   */
  clientSecret: string;

  /**
   * OAuth2 token endpoint URL
   */
  tokenUrl: string;

  /**
   * OAuth2 scope (default: "vendor.search")
   */
  scope?: string;

  /**
   * Request timeout in milliseconds (default: 60000)
   */
  timeout?: number;

  /**
   * Maximum number of retry attempts (default: 3)
   */
  maxRetries?: number;

  /**
   * Initial retry delay in milliseconds (default: 1000)
   * Uses exponential backoff
   */
  retryDelay?: number;

  /**
   * Token refresh buffer in seconds (default: 60)
   * Refresh token this many seconds before expiration
   */
  tokenRefreshBuffer?: number;

  /**
   * Enable debug logging (default: false)
   */
  debug?: boolean;
}

/**
 * Internal configuration with defaults applied
 */
export interface ResolvedClientConfig extends Required<ClientConfig> {}

/**
 * Applies default values to client configuration
 */
export function resolveConfig(config: ClientConfig): ResolvedClientConfig {
  return {
    ...config,
    scope: config.scope ?? 'vendor.search',
    timeout: config.timeout ?? 60000,
    maxRetries: config.maxRetries ?? 3,
    retryDelay: config.retryDelay ?? 1000,
    tokenRefreshBuffer: config.tokenRefreshBuffer ?? 60,
    debug: config.debug ?? false,
  };
}
