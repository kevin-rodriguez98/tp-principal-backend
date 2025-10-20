package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrdenProduccionRequest {
    private String productoRequerido;
    private String marca;
    private BigDecimal stockRequerido;
    private LocalDate fechaEntrega;
}
