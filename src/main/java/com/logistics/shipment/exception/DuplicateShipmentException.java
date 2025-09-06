package com.logistics.shipment.exception;

/**
 * Exception thrown when attempting to create a shipment with an order ID that already exists
 */
public class DuplicateShipmentException extends RuntimeException {
    
    public DuplicateShipmentException(String orderId) {
        super("Shipment with order ID already exists: " + orderId);
    }
    
    public DuplicateShipmentException(String orderId, Throwable cause) {
        super("Shipment with order ID already exists: " + orderId, cause);
    }
}