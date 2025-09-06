package com.logistics.shipment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.shipment.exception.DuplicateShipmentException;
import com.logistics.shipment.exception.InvalidStatusTransitionException;
import com.logistics.shipment.exception.ShipmentNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ShipmentController
 */
@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ShipmentService shipmentService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testCreateShipment() throws Exception {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        when(shipmentService.createShipment(any(Shipment.class))).thenReturn(shipment);
        
        mockMvc.perform(post("/api/v1/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shipment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORDER-001"))
                .andExpect(jsonPath("$.origin").value("New York"))
                .andExpect(jsonPath("$.destination").value("Los Angeles"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    void testCreateShipmentDuplicate() throws Exception {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        when(shipmentService.createShipment(any(Shipment.class)))
                .thenThrow(new DuplicateShipmentException("ORDER-001"));
        
        mockMvc.perform(post("/api/v1/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shipment)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Shipment"))
                .andExpect(jsonPath("$.message").value("Shipment with order ID already exists: ORDER-001"));
    }
    
    @Test
    void testCreateShipmentValidationError() throws Exception {
        Shipment invalidShipment = new Shipment("", "", ""); // Empty required fields
        
        mockMvc.perform(post("/api/v1/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidShipment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
    
    @Test
    void testGetShipmentByOrderId() throws Exception {
        Shipment shipment = new Shipment("ORDER-001", "New York", "Los Angeles");
        when(shipmentService.getShipmentByOrderId("ORDER-001")).thenReturn(shipment);
        
        mockMvc.perform(get("/api/v1/shipments/ORDER-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORDER-001"))
                .andExpect(jsonPath("$.origin").value("New York"))
                .andExpect(jsonPath("$.destination").value("Los Angeles"));
    }
    
    @Test
    void testGetShipmentByOrderIdNotFound() throws Exception {
        when(shipmentService.getShipmentByOrderId("NONEXISTENT"))
                .thenThrow(new ShipmentNotFoundException("NONEXISTENT"));
        
        mockMvc.perform(get("/api/v1/shipments/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Shipment Not Found"))
                .andExpect(jsonPath("$.message").value("Shipment not found with order ID: NONEXISTENT"));
    }
    
    @Test
    void testUpdateShipmentStatus() throws Exception {
        Shipment updatedShipment = new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.DISPATCHED);
        when(shipmentService.updateShipmentStatus("ORDER-001", "dispatched")).thenReturn(updatedShipment);
        
        ShipmentController.StatusUpdateRequest request = new ShipmentController.StatusUpdateRequest("dispatched");
        
        mockMvc.perform(patch("/api/v1/shipments/ORDER-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORDER-001"))
                .andExpect(jsonPath("$.status").value("DISPATCHED"));
    }
    
    @Test
    void testUpdateShipmentStatusInvalidTransition() throws Exception {
        when(shipmentService.updateShipmentStatus("ORDER-001", "delivered"))
                .thenThrow(new InvalidStatusTransitionException("Invalid transition"));
        
        ShipmentController.StatusUpdateRequest request = new ShipmentController.StatusUpdateRequest("delivered");
        
        mockMvc.perform(patch("/api/v1/shipments/ORDER-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Status Transition"));
    }
    
    @Test
    void testGetAllShipments() throws Exception {
        List<Shipment> shipments = Arrays.asList(
            new Shipment("ORDER-001", "New York", "Los Angeles"),
            new Shipment("ORDER-002", "Chicago", "Miami")
        );
        
        when(shipmentService.getShipments(null, null)).thenReturn(shipments);
        
        mockMvc.perform(get("/api/v1/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value("ORDER-001"))
                .andExpect(jsonPath("$[1].orderId").value("ORDER-002"));
    }
    
    @Test
    void testGetShipmentsWithFilters() throws Exception {
        List<Shipment> pendingShipments = Arrays.asList(
            new Shipment("ORDER-001", "New York", "Los Angeles", ShipmentStatus.PENDING)
        );
        
        when(shipmentService.getShipments("pending", "New York")).thenReturn(pendingShipments);
        
        mockMvc.perform(get("/api/v1/shipments")
                .param("status", "pending")
                .param("origin", "New York"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].origin").value("New York"));
    }
    
    @Test
    void testGetShipmentStats() throws Exception {
        Map<ShipmentStatus, Long> countByStatus = new HashMap<>();
        countByStatus.put(ShipmentStatus.PENDING, 3L);
        countByStatus.put(ShipmentStatus.DISPATCHED, 2L);
        countByStatus.put(ShipmentStatus.IN_TRANSIT, 1L);
        
        when(shipmentService.getShipmentCountByStatus()).thenReturn(countByStatus);
        when(shipmentService.getTotalShipmentCount()).thenReturn(6);
        
        mockMvc.perform(get("/api/v1/shipments/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(6))
                .andExpect(jsonPath("$.countByStatus.PENDING").value(3))
                .andExpect(jsonPath("$.countByStatus.DISPATCHED").value(2))
                .andExpect(jsonPath("$.countByStatus.IN_TRANSIT").value(1));
    }
}