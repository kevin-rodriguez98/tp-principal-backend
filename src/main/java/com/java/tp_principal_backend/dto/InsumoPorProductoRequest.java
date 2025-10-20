package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsumoPorProductoRequest {
    private String codigoProducto;
    private String codigoInsumo;
    private BigDecimal stockNecesarioInsumo;
}
