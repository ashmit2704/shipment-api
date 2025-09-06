package com.logistics.shipment.controller;

import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Shipment Tracking API
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Shipment Tracking", description = "API for tracking shipments in a logistics system")
public class ShipmentController {
    
    private final ShipmentService shipmentService;
    
    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }
    
    /**
     * POST /shipments - Create a new shipment
     */
    @PostMapping("/shipments")
    @Operation(summary = "Create a new shipment", 
               description = "Creates a new shipment with orderId, origin, destination, and status (default: pending)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Shipment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Shipment with orderId already exists")
    })
    public ResponseEntity<Shipment> createShipment(
            @Valid @RequestBody Shipment shipment) {
        
        Shipment createdShipment = shipmentService.createShipment(shipment);
        return new ResponseEntity<>(createdShipment, HttpStatus.CREATED);
    }
    
    /**
     * GET /shipments/{orderId} - Fetch shipment details by orderId
     */
    @GetMapping("/shipments/{orderId}")
    @Operation(summary = "Get shipment by order ID", 
               description = "Retrieves shipment details for the specified order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipment found"),
        @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<Shipment> getShipmentByOrderId(
            @Parameter(description = "Order ID of the shipment to retrieve")
            @PathVariable String orderId) {
        
        Shipment shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipment);
    }
    
    /**
     * PATCH /shipments/{orderId} - Update the shipment's status
     */
    @PatchMapping("/shipments/{orderId}")
    @Operation(summary = "Update shipment status", 
               description = "Updates the status of a shipment. Valid transitions: pending → dispatched → in-transit → delivered")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    public ResponseEntity<Shipment> updateShipmentStatus(
            @Parameter(description = "Order ID of the shipment to update")
            @PathVariable String orderId,
            @RequestBody StatusUpdateRequest statusUpdateRequest) {
        
        Shipment updatedShipment = shipmentService.updateShipmentStatus(
            orderId, 
            statusUpdateRequest.getStatus()
        );
        return ResponseEntity.ok(updatedShipment);
    }
    
    /**
     * GET /shipments - Return all shipments with optional query filters
     */
    @GetMapping("/shipments")
    @Operation(summary = "Get all shipments", 
               description = "Retrieves all shipments with optional filtering by status and origin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipments retrieved successfully")
    })
    public ResponseEntity<List<Shipment>> getAllShipments(
            @Parameter(description = "Filter by shipment status")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by origin location")
            @RequestParam(required = false) String origin) {
        
        List<Shipment> shipments = shipmentService.getShipments(status, origin);
        return ResponseEntity.ok(shipments);
    }
    
    /**
     * GET /shipments/stats - Get shipment statistics
     */
    @GetMapping("/shipments/stats")
    @Operation(summary = "Get shipment statistics", 
               description = "Returns statistics about shipments including count by status")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<ShipmentStats> getShipmentStats() {
        Map<ShipmentStatus, Long> countByStatus = shipmentService.getShipmentCountByStatus();
        int totalCount = shipmentService.getTotalShipmentCount();
        
        ShipmentStats stats = new ShipmentStats(totalCount, countByStatus);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Request body for status updates
     */
    public static class StatusUpdateRequest {
        private String status;
        
        public StatusUpdateRequest() {}
        
        public StatusUpdateRequest(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    /**
     * Response model for shipment statistics
     */
    public static class ShipmentStats {
        private int totalShipments;
        private Map<ShipmentStatus, Long> countByStatus;
        
        public ShipmentStats(int totalShipments, Map<ShipmentStatus, Long> countByStatus) {
            this.totalShipments = totalShipments;
            this.countByStatus = countByStatus;
        }
        
        public int getTotalShipments() {
            return totalShipments;
        }
        
        public void setTotalShipments(int totalShipments) {
            this.totalShipments = totalShipments;
        }
        
        public Map<ShipmentStatus, Long> getCountByStatus() {
            return countByStatus;
        }
        
        public void setCountByStatus(Map<ShipmentStatus, Long> countByStatus) {
            this.countByStatus = countByStatus;
        }
    }
}