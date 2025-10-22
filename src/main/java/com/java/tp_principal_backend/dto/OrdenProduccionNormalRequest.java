package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrdenProduccionNormalRequest {
    private String productoRequerido;
    private String marca;
    private BigDecimal stockRequerido;
    private String codigoProducto;
    private LocalDate fechaEntrega;
    private String estado;  // opcional, si no se manda, se pone "Evaluación"
    private String lote;
}
