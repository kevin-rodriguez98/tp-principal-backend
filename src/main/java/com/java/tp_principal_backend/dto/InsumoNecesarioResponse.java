package com.java.tp_principal_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InsumoNecesarioResponse {
    private String codigoInsumo;
    private String nombreInsumo;
    private BigDecimal cantidadNecesaria;
    private String unidad;
}
