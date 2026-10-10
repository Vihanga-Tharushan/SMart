package com.supermarket.inventoryservice.controller;

import com.supermarket.inventoryservice.dto.StockReservationDTO;
import com.supermarket.inventoryservice.service.StockReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StockReservationController {

    @Autowired
    private StockReservationService stockReservationService;

    @GetMapping("/getreservations")
    public List<StockReservationDTO> getReservations() {
        return stockReservationService.getAllReservations();
    }

    @GetMapping("/reservation/{reservationId}")
    public StockReservationDTO getReservationById(@PathVariable UUID reservationId) {
        return stockReservationService.getReservationById(reservationId);
    }

    @GetMapping("/reservations/order/{orderId}")
    public List<StockReservationDTO> getReservationsByOrder(@PathVariable UUID orderId) {
        return stockReservationService.getReservationsByOrder(orderId);
    }

    @PostMapping("/addreservation")
    public StockReservationDTO saveReservation(@RequestBody StockReservationDTO stockReservationDTO) {
        return stockReservationService.saveReservation(stockReservationDTO);
    }

    @PostMapping("/reservestock")
    public StockReservationDTO reserveStock(@RequestBody StockReservationDTO stockReservationDTO) {
        return stockReservationService.reserveStock(stockReservationDTO);
    }

    @PostMapping("/confirmreservation/{reservationId}")
    public StockReservationDTO confirmReservation(@PathVariable UUID reservationId) {
        return stockReservationService.confirmReservation(reservationId);
    }

    @PostMapping("/releasereservation/{reservationId}")
    public StockReservationDTO releaseReservation(@PathVariable UUID reservationId) {
        return stockReservationService.releaseReservation(reservationId);
    }

    @PutMapping("/updatereservation")
    public StockReservationDTO updateReservation(@RequestBody StockReservationDTO stockReservationDTO) {
        return stockReservationService.updateReservation(stockReservationDTO);
    }

    @DeleteMapping("/deletereservation/{reservationId}")
    public String deleteReservation(@PathVariable UUID reservationId) {
        return stockReservationService.deleteReservation(reservationId);
    }

}
