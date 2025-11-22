package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

import com.java.tp_principal_backend.model.Locacion;

@Data
public class InsumoRequest {
    private String codigo;
    private String nombre;
    private String categoria;
    private String marca;
    private String unidad;
    private BigDecimal stock;
    private Integer umbralMinimoStock;
    private String proveedor;
    private String destino;
    private Locacion locacion;
}