package com.java.tp_principal_backend.dto;

import lombok.Data;

@Data
public class ProductoRequest {
    private String codigo;
    private String nombre;
    private String categoria;
    private String marca;
    private String unidad;
    private String legajoResponsable;
}
