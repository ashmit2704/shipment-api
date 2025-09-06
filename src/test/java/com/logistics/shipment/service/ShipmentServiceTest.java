package com.logistics.shipment.service;

import com.logistics.shipment.exception.DuplicateShipmentException;
import com.logistics.shipment.exception.InvalidStatusTransitionException;
import com.logistics.shipment.exception.ShipmentNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShipmentService
 */
class ShipmentServiceTest {
    
    private ShipmentService shipmentService;
    
    @BeforeEach
    void setUp() {
        shipmentService = new ShipmentService();
    }
    
    @Test
    void testCreateShipment() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        
        Shipment created = shipmentService.createShipment(shipment);
        
        assertNotNull(created);
        assertEquals("ORDER-001", created.getOrderId());
        assertEquals("New York", created.getOrigin());
        assertEquals("Los Angeles", created.getDestination());
        assertEquals(ShipmentStatus.PENDING, created.getStatus());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
    }
    
    @Test
    void testCreateShipmentWithStatus() {
        Shipment shipment = new Shipment("ORDER-002", "Chicago", "Miami", ShipmentStatus.DISPATCHED);
        
        Shipment created = shipmentService.createShipment(shipment);
        
        assertEquals(ShipmentStatus.DISPATCHED, created.getStatus());
    }
    
    @Test
    void testCreateDuplicateShipment() {
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles");
        Shipment shipment2 = new Shipment("ORDER-001", "Boston", "Seattle");
        
        shipmentService.createShipment(shipment1);
        
        assertThrows(DuplicateShipmentException.class, () -> {
            shipmentService.createShipment(shipment2);
        });
    }
    
    @Test
    void testGetShipmentByOrderId() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        Shipment retrieved = shipmentService.getShipmentByOrderId("ORDER-001");
        
        assertNotNull(retrieved);
        assertEquals("ORDER-001", retrieved.getOrderId());
        assertEquals("New York", retrieved.getOrigin());
        assertEquals("Los Angeles", retrieved.getDestination());
    }
    
    @Test
    void testGetShipmentByOrderIdNotFound() {
        assertThrows(ShipmentNotFoundException.class, () -> {
            shipmentService.getShipmentByOrderId("NONEXISTENT");
        });
    }
    
    @Test
    void testValidStatusTransition() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        // Test valid transition: pending -> dispatched
        Shipment updated = shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.DISPATCHED);
        assertEquals(ShipmentStatus.DISPATCHED, updated.getStatus());
        
        // Test valid transition: dispatched -> in-transit
        updated = shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.IN_TRANSIT);
        assertEquals(ShipmentStatus.IN_TRANSIT, updated.getStatus());
        
        // Test valid transition: in-transit -> delivered
        updated = shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.DELIVERED);
        assertEquals(ShipmentStatus.DELIVERED, updated.getStatus());
    }
    
    @Test
    void testInvalidStatusTransition() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        // Test invalid transition: pending -> in-transit (skipping dispatched)
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.IN_TRANSIT);
        });
        
        // Test invalid transition: pending -> delivered (skipping intermediate states)
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.DELIVERED);
        });
    }
    
    @Test
    void testBackwardStatusTransition() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.DISPATCHED);
        shipmentService.createShipment(shipment);
        
        // Test backward transition: dispatched -> pending (not allowed)
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.PENDING);
        });
    }
    
    @Test
    void testUpdateStatusFromDelivered() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.DELIVERED);
        shipmentService.createShipment(shipment);
        
        // No transitions allowed from delivered state
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.PENDING);
        });
        
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.DISPATCHED);
        });
        
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", ShipmentStatus.IN_TRANSIT);
        });
    }
    
    @Test
    void testUpdateStatusWithStringValue() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        Shipment updated = shipmentService.updateShipmentStatus("ORDER-001", "dispatched");
        assertEquals(ShipmentStatus.DISPATCHED, updated.getStatus());
    }
    
    @Test
    void testUpdateStatusWithInvalidString() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        assertThrows(InvalidStatusTransitionException.class, () -> {
            shipmentService.updateShipmentStatus("ORDER-001", "invalid-status");
        });
    }
    
    @Test
    void testGetAllShipments() {
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles");
        Shipment shipment2 = new Shipment("ORDER-002", "Chicago", "Miami");
        
        shipmentService.createShipment(shipment1);
        shipmentService.createShipment(shipment2);
        
        List<Shipment> allShipments = shipmentService.getAllShipments();
        assertEquals(2, allShipments.size());
    }
    
    @Test
    void testGetShipmentsWithStatusFilter() {
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.PENDING);
        Shipment shipment2 = new Shipment("ORDER-002", "Chicago", "Miami", ShipmentStatus.DISPATCHED);
        Shipment shipment3 = new Shipment("ORDER-003", "Boston", "Seattle", ShipmentStatus.PENDING);
        
        shipmentService.createShipment(shipment1);
        shipmentService.createShipment(shipment2);
        shipmentService.createShipment(shipment3);
        
        List<Shipment> pendingShipments = shipmentService.getShipments("pending", null);
        assertEquals(2, pendingShipments.size());
        
        List<Shipment> dispatchedShipments = shipmentService.getShipments("dispatched", null);
        assertEquals(1, dispatchedShipments.size());
    }
    
    @Test
    void testGetShipmentsWithOriginFilter() {
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles");
        Shipment shipment2 = new Shipment("ORDER-002", "Chicago", "Miami");
        Shipment shipment3 = new Shipment("ORDER-003", "New York", "Seattle");
        
        shipmentService.createShipment(shipment1);
        shipmentService.createShipment(shipment2);
        shipmentService.createShipment(shipment3);
        
        List<Shipment> newYorkShipments = shipmentService.getShipments(null, "New York");
        assertEquals(2, newYorkShipments.size());
        
        List<Shipment> chicagoShipments = shipmentService.getShipments(null, "Chicago");
        assertEquals(1, chicagoShipments.size());
    }
    
    @Test
    void testExistsByOrderId() {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        shipmentService.createShipment(shipment);
        
        assertTrue(shipmentService.existsByOrderId("ORDER-001"));
        assertFalse(shipmentService.existsByOrderId("NONEXISTENT"));
    }
    
    @Test
    void testGetShipmentCountByStatus() {
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.PENDING);
        Shipment shipment2 = new Shipment("ORDER-002", "Chicago", "Miami", ShipmentStatus.DISPATCHED);
        Shipment shipment3 = new Shipment("ORDER-003", "Boston", "Seattle", ShipmentStatus.PENDING);
        
        shipmentService.createShipment(shipment1);
        shipmentService.createShipment(shipment2);
        shipmentService.createShipment(shipment3);
        
        Map<ShipmentStatus, Long> countByStatus = shipmentService.getShipmentCountByStatus();
        
        assertEquals(2L, countByStatus.get(ShipmentStatus.PENDING));
        assertEquals(1L, countByStatus.get(ShipmentStatus.DISPATCHED));
        assertNull(countByStatus.get(ShipmentStatus.IN_TRANSIT));
        assertNull(countByStatus.get(ShipmentStatus.DELIVERED));
    }
    
    @Test
    void testGetTotalShipmentCount() {
        assertEquals(0, shipmentService.getTotalShipmentCount());
        
        Shipment shipment1 = new Shipment("ORDER-001", "New York", "Los Angeles");
        Shipment shipment2 = new Shipment("ORDER-002", "Chicago", "Miami");
        
        shipmentService.createShipment(shipment1);
        assertEquals(1, shipmentService.getTotalShipmentCount());
        
        shipmentService.createShipment(shipment2);
        assertEquals(2, shipmentService.getTotalShipmentCount());
    }
}