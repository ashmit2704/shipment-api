package com.logistics.shipment.exception;

import com.logistics.shipment.model.ShipmentStatus;

/**
 * Exception thrown when an invalid status transition is attempted
 */
public class InvalidStatusTransitionException extends RuntimeException {
    
    public InvalidStatusTransitionException(ShipmentStatus currentStatus, ShipmentStatus targetStatus) {
        super(String.format("Invalid status transition from %s to %s. Valid next status is: %s", 
              currentStatus.getValue(), 
              targetStatus.getValue(),
              currentStatus.getNextStatus() != null ? currentStatus.getNextStatus().getValue() : "none"));
    }
    
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}