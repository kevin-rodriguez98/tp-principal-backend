package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimientoProductoRequest {
    private String codigoProducto;
    private BigDecimal cantidad;
    private String tipo;
    private String destino;
}
