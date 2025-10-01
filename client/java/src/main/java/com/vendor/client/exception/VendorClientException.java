package com.vendor.client.exception;

/**
 * Exception thrown when vendor client operations fail.
 */
public class VendorClientException extends RuntimeException {
    
    public VendorClientException(String message) {
        super(message);
    }
    
    public VendorClientException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VendorClientException(Throwable cause) {
        super(cause);
    }
}
