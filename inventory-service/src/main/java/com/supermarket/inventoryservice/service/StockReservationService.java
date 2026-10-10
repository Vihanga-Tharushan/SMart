package com.supermarket.inventoryservice.service;

import com.supermarket.inventoryservice.dto.StockReservationDTO;
import com.supermarket.inventoryservice.model.ReservationStatus;
import com.supermarket.inventoryservice.model.StockLevel;
import com.supermarket.inventoryservice.model.StockReservation;
import com.supermarket.inventoryservice.repository.StockLevelRepository;
import com.supermarket.inventoryservice.repository.StockReservationRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StockReservationService {

    @Autowired
    private StockReservationRepository stockReservationRepository;

    @Autowired
    private StockLevelRepository stockLevelRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<StockReservationDTO> getAllReservations() {
        return modelMapper.map(stockReservationRepository.findAll(), new TypeToken<List<StockReservationDTO>>() {}.getType());
    }

    public StockReservationDTO getReservationById(UUID id) {
        return modelMapper.map(stockReservationRepository.findById(id).orElse(null), StockReservationDTO.class);
    }

    public List<StockReservationDTO> getReservationsByOrder(UUID orderId) {
        return modelMapper.map(stockReservationRepository.findByOrderId(orderId), new TypeToken<List<StockReservationDTO>>() {}.getType());
    }

    public StockReservationDTO reserveStock(StockReservationDTO stockReservationDTO) {
        StockLevel stockLevel = stockLevelRepository
                .findByBranchIdAndProductIdForUpdate(stockReservationDTO.getBranchId(), stockReservationDTO.getProductId())
                .orElseThrow(() -> new IllegalStateException("No stock level found for the branch/product"));

        if (stockLevel.getAvailableQuantity().compareTo(stockReservationDTO.getQuantity()) < 0) {
            throw new IllegalStateException("Insufficient available stock to reserve");
        }

        stockLevel.setQuantityReserved(stockLevel.getQuantityReserved().add(stockReservationDTO.getQuantity()));
        stockLevelRepository.save(stockLevel);

        StockReservation reservation = modelMapper.map(stockReservationDTO, StockReservation.class);
        reservation.setId(null);
        reservation.setStatus(ReservationStatus.ACTIVE);
        return modelMapper.map(stockReservationRepository.save(reservation), StockReservationDTO.class);
    }

    public StockReservationDTO confirmReservation(UUID id) {
        StockReservation reservation = stockReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return modelMapper.map(stockReservationRepository.save(reservation), StockReservationDTO.class);
    }

    public StockReservationDTO releaseReservation(UUID id) {
        StockReservation reservation = stockReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.ACTIVE) {
            stockLevelRepository
                    .findByBranchIdAndProductIdForUpdate(reservation.getBranchId(), reservation.getProductId())
                    .ifPresent(stockLevel -> {
                        BigDecimal reserved = stockLevel.getQuantityReserved().subtract(reservation.getQuantity());
                        stockLevel.setQuantityReserved(reserved.signum() < 0 ? BigDecimal.ZERO : reserved);
                        stockLevelRepository.save(stockLevel);
                    });
        }

        reservation.setStatus(ReservationStatus.RELEASED);
        return modelMapper.map(stockReservationRepository.save(reservation), StockReservationDTO.class);
    }

    public StockReservationDTO saveReservation(StockReservationDTO stockReservationDTO) {
        StockReservation reservation = modelMapper.map(stockReservationDTO, StockReservation.class);
        reservation.setId(null);
        return modelMapper.map(stockReservationRepository.save(reservation), StockReservationDTO.class);
    }

    public StockReservationDTO updateReservation(StockReservationDTO stockReservationDTO) {
        StockReservation reservation = modelMapper.map(stockReservationDTO, StockReservation.class);
        return modelMapper.map(stockReservationRepository.save(reservation), StockReservationDTO.class);
    }

    public String deleteReservation(UUID id) {
        stockReservationRepository.deleteById(id);
        return "Reservation Deleted Successfully";
    }

}
