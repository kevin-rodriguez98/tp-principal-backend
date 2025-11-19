package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TiempoProduccionRequest {
    private String codigoProducto;
    private BigDecimal tiempoPreparacion;
    private BigDecimal tiempoCiclo;
    private BigDecimal maximoTanda;
}
