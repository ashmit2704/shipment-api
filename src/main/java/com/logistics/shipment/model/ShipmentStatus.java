package com.logistics.shipment.model;

/**
 * Enum representing the possible statuses of a shipment.
 * Status transitions follow: pending → dispatched → in-transit → delivered
 */
public enum ShipmentStatus {
    PENDING("pending", "Shipment is pending processing"),
    DISPATCHED("dispatched", "Shipment has been dispatched"),
    IN_TRANSIT("in-transit", "Shipment is in transit"),
    DELIVERED("delivered", "Shipment has been delivered");
    
    private final String value;
    private final String description;
    
    ShipmentStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this status can transition to the target status
     */
    public boolean canTransitionTo(ShipmentStatus targetStatus) {
        return switch (this) {
            case PENDING -> targetStatus == DISPATCHED;
            case DISPATCHED -> targetStatus == IN_TRANSIT;
            case IN_TRANSIT -> targetStatus == DELIVERED;
            case DELIVERED -> false; // No further transitions allowed
        };
    }
    
    /**
     * Get the next valid status in the workflow
     */
    public ShipmentStatus getNextStatus() {
        return switch (this) {
            case PENDING -> DISPATCHED;
            case DISPATCHED -> IN_TRANSIT;
            case IN_TRANSIT -> DELIVERED;
            case DELIVERED -> null; // No next status
        };
    }
    
    public static ShipmentStatus fromValue(String value) {
        for (ShipmentStatus status : ShipmentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid shipment status: " + value);
    }
}