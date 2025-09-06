package com.logistics.shipment.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShipmentStatus enum
 */
class ShipmentStatusTest {
    
    @Test
    void testValidStatusTransitions() {
        // Test valid transitions
        assertTrue(ShipmentStatus.PENDING.canTransitionTo(ShipmentStatus.DISPATCHED));
        assertTrue(ShipmentStatus.DISPATCHED.canTransitionTo(ShipmentStatus.IN_TRANSIT));
        assertTrue(ShipmentStatus.IN_TRANSIT.canTransitionTo(ShipmentStatus.DELIVERED));
    }
    
    @Test
    void testInvalidStatusTransitions() {
        // Test invalid transitions
        assertFalse(ShipmentStatus.PENDING.canTransitionTo(ShipmentStatus.IN_TRANSIT));
        assertFalse(ShipmentStatus.PENDING.canTransitionTo(ShipmentStatus.DELIVERED));
        assertFalse(ShipmentStatus.DISPATCHED.canTransitionTo(ShipmentStatus.PENDING));
        assertFalse(ShipmentStatus.DISPATCHED.canTransitionTo(ShipmentStatus.DELIVERED));
        assertFalse(ShipmentStatus.IN_TRANSIT.canTransitionTo(ShipmentStatus.PENDING));
        assertFalse(ShipmentStatus.IN_TRANSIT.canTransitionTo(ShipmentStatus.DISPATCHED));
        
        // No transitions allowed from DELIVERED
        assertFalse(ShipmentStatus.DELIVERED.canTransitionTo(ShipmentStatus.PENDING));
        assertFalse(ShipmentStatus.DELIVERED.canTransitionTo(ShipmentStatus.DISPATCHED));
        assertFalse(ShipmentStatus.DELIVERED.canTransitionTo(ShipmentStatus.IN_TRANSIT));
        assertFalse(ShipmentStatus.DELIVERED.canTransitionTo(ShipmentStatus.DELIVERED));
    }
    
    @Test
    void testGetNextStatus() {
        assertEquals(ShipmentStatus.DISPATCHED, ShipmentStatus.PENDING.getNextStatus());
        assertEquals(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DISPATCHED.getNextStatus());
        assertEquals(ShipmentStatus.DELIVERED, ShipmentStatus.IN_TRANSIT.getNextStatus());
        assertNull(ShipmentStatus.DELIVERED.getNextStatus());
    }
    
    @Test
    void testFromValue() {
        assertEquals(ShipmentStatus.PENDING, ShipmentStatus.fromValue("pending"));
        assertEquals(ShipmentStatus.DISPATCHED, ShipmentStatus.fromValue("dispatched"));
        assertEquals(ShipmentStatus.IN_TRANSIT, ShipmentStatus.fromValue("in-transit"));
        assertEquals(ShipmentStatus.DELIVERED, ShipmentStatus.fromValue("delivered"));
        
        // Test case insensitivity
        assertEquals(ShipmentStatus.PENDING, ShipmentStatus.fromValue("PENDING"));
        assertEquals(ShipmentStatus.DISPATCHED, ShipmentStatus.fromValue("Dispatched"));
    }
    
    @Test
    void testFromValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            ShipmentStatus.fromValue("invalid-status");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            ShipmentStatus.fromValue(null);
        });
    }
    
    @Test
    void testGetValue() {
        assertEquals("pending", ShipmentStatus.PENDING.getValue());
        assertEquals("dispatched", ShipmentStatus.DISPATCHED.getValue());
        assertEquals("in-transit", ShipmentStatus.IN_TRANSIT.getValue());
        assertEquals("delivered", ShipmentStatus.DELIVERED.getValue());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(ShipmentStatus.PENDING.getDescription());
        assertNotNull(ShipmentStatus.DISPATCHED.getDescription());
        assertNotNull(ShipmentStatus.IN_TRANSIT.getDescription());
        assertNotNull(ShipmentStatus.DELIVERED.getDescription());
        
        assertTrue(ShipmentStatus.PENDING.getDescription().contains("pending"));
        assertTrue(ShipmentStatus.DELIVERED.getDescription().contains("delivered"));
    }
}