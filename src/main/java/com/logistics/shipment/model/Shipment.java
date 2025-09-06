package com.logistics.shipment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Shipment entity representing a package being tracked through the logistics system
 */
public class Shipment {
    
    @NotBlank(message = "Order ID is required")
    @JsonProperty("orderId")
    private String orderId;
    
    @NotBlank(message = "Origin is required")
    @JsonProperty("origin")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    @JsonProperty("destination")
    private String destination;
    
    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private ShipmentStatus status;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Shipment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ShipmentStatus.PENDING; // Default status
    }
    
    // Constructor with required fields
    public Shipment(String orderId, String origin, String destination) {
        this();
        this.orderId = orderId;
        this.origin = origin;
        this.destination = destination;
    }
    
    // Constructor with all fields
    public Shipment(String orderId, String origin, String destination, ShipmentStatus status) {
        this(orderId, origin, destination);
        this.status = status != null ? status : ShipmentStatus.PENDING;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public ShipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ShipmentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Updates the shipment status if the transition is valid
     */
    public boolean updateStatus(ShipmentStatus newStatus) {
        if (this.status.canTransitionTo(newStatus)) {
            setStatus(newStatus);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(orderId, shipment.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
    
    @Override
    public String toString() {
        return "Shipment{" +
                "orderId='" + orderId + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}