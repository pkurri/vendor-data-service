/**
 * Base error class for vendor client errors
 */
export class VendorClientError extends Error {
  constructor(message: string, public readonly cause?: Error) {
    super(message);
    this.name = 'VendorClientError';
    Object.setPrototypeOf(this, VendorClientError.prototype);
  }
}

/**
 * Error thrown when authentication fails
 */
export class AuthenticationError extends VendorClientError {
  constructor(message: string, cause?: Error) {
    super(message, cause);
    this.name = 'AuthenticationError';
    Object.setPrototypeOf(this, AuthenticationError.prototype);
  }
}

/**
 * Error thrown when API request fails
 */
export class ApiError extends VendorClientError {
  constructor(
    message: string,
    public readonly statusCode?: number,
    public readonly responseBody?: any,
    cause?: Error
  ) {
    super(message, cause);
    this.name = 'ApiError';
    Object.setPrototypeOf(this, ApiError.prototype);
  }
}

/**
 * Error thrown when request times out
 */
export class TimeoutError extends VendorClientError {
  constructor(message: string, cause?: Error) {
    super(message, cause);
    this.name = 'TimeoutError';
    Object.setPrototypeOf(this, TimeoutError.prototype);
  }
}

/**
 * Error thrown when validation fails
 */
export class ValidationError extends VendorClientError {
  constructor(message: string, cause?: Error) {
    super(message, cause);
    this.name = 'ValidationError';
    Object.setPrototypeOf(this, ValidationError.prototype);
  }
}
