package com.logistics.shipment.service;

import com.logistics.shipment.exception.DuplicateShipmentException;
import com.logistics.shipment.exception.InvalidStatusTransitionException;
import com.logistics.shipment.exception.ShipmentNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing shipments with in-memory storage
 */
@Service
public class ShipmentService {
    
    // Thread-safe in-memory storage
    private final Map<String, Shipment> shipments = new ConcurrentHashMap<>();
    
    /**
     * Creates a new shipment
     * @param shipment The shipment to create
     * @return The created shipment
     * @throws DuplicateShipmentException if a shipment with the same order ID already exists
     */
    public Shipment createShipment(Shipment shipment) {
        if (shipments.containsKey(shipment.getOrderId())) {
            throw new DuplicateShipmentException(shipment.getOrderId());
        }
        
        // Ensure default status is set if not provided
        if (shipment.getStatus() == null) {
            shipment.setStatus(ShipmentStatus.PENDING);
        }
        
        shipments.put(shipment.getOrderId(), shipment);
        return shipment;
    }
    
    /**
     * Retrieves a shipment by order ID
     * @param orderId The order ID to search for
     * @return The shipment with the given order ID
     * @throws ShipmentNotFoundException if no shipment is found
     */
    public Shipment getShipmentByOrderId(String orderId) {
        Shipment shipment = shipments.get(orderId);
        if (shipment == null) {
            throw new ShipmentNotFoundException(orderId);
        }
        return shipment;
    }
    
    /**
     * Updates the status of a shipment
     * @param orderId The order ID of the shipment to update
     * @param newStatus The new status to set
     * @return The updated shipment
     * @throws ShipmentNotFoundException if no shipment is found
     * @throws InvalidStatusTransitionException if the status transition is invalid
     */
    public Shipment updateShipmentStatus(String orderId, ShipmentStatus newStatus) {
        Shipment shipment = getShipmentByOrderId(orderId);
        
        if (!shipment.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(shipment.getStatus(), newStatus);
        }
        
        shipment.setStatus(newStatus);
        return shipment;
    }
    
    /**
     * Updates the status of a shipment using string value
     * @param orderId The order ID of the shipment to update
     * @param statusValue The new status value as string
     * @return The updated shipment
     */
    public Shipment updateShipmentStatus(String orderId, String statusValue) {
        ShipmentStatus newStatus;
        try {
            newStatus = ShipmentStatus.fromValue(statusValue);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusTransitionException("Invalid status value: " + statusValue);
        }
        
        return updateShipmentStatus(orderId, newStatus);
    }
    
    /**
     * Retrieves all shipments
     * @return List of all shipments
     */
    public List<Shipment> getAllShipments() {
        return new ArrayList<>(shipments.values());
    }
    
    /**
     * Retrieves shipments filtered by optional parameters
     * @param status Optional status filter
     * @param origin Optional origin filter
     * @return Filtered list of shipments
     */
    public List<Shipment> getShipments(String status, String origin) {
        return shipments.values().stream()
                .filter(shipment -> status == null || shipment.getStatus().getValue().equalsIgnoreCase(status))
                .filter(shipment -> origin == null || shipment.getOrigin().equalsIgnoreCase(origin))
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if a shipment exists with the given order ID
     * @param orderId The order ID to check
     * @return true if shipment exists, false otherwise
     */
    public boolean existsByOrderId(String orderId) {
        return shipments.containsKey(orderId);
    }
    
    /**
     * Gets the count of shipments by status
     * @return Map of status to count
     */
    public Map<ShipmentStatus, Long> getShipmentCountByStatus() {
        return shipments.values().stream()
                .collect(Collectors.groupingBy(
                    Shipment::getStatus,
                    Collectors.counting()
                ));
    }
    
    /**
     * Deletes a shipment by order ID (for testing purposes)
     * @param orderId The order ID of the shipment to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteShipment(String orderId) {
        return shipments.remove(orderId) != null;
    }
    
    /**
     * Gets the total number of shipments
     * @return Total count of shipments
     */
    public int getTotalShipmentCount() {
        return shipments.size();
    }
}