package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimientoInsumoRequest {
    private String codigo;
    private String tipo;
    private BigDecimal stock;
    private String nombre;
    private String categoria;
    private String marca;
    private String unidad;
    private String lote;
}
