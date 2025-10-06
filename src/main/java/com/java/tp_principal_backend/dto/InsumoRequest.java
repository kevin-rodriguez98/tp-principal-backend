package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsumoRequest {
    private String codigo;
    private String nombre;
    private String categoria;
    private String marca;
    private String unidad;
    private BigDecimal stock;
    private String lote;
    private Integer umbralMinimoStock;
}