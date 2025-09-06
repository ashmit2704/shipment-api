package com.logistics.shipment.exception;

/**
 * Exception thrown when a shipment with the given order ID is not found
 */
public class ShipmentNotFoundException extends RuntimeException {
    
    public ShipmentNotFoundException(String orderId) {
        super("Shipment not found with order ID: " + orderId);
    }
    
    public ShipmentNotFoundException(String orderId, Throwable cause) {
        super("Shipment not found with order ID: " + orderId, cause);
    }
}