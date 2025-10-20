package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String unidad;

    @Column(nullable = false)
    private BigDecimal stock = BigDecimal.ZERO;

    @Column(nullable = false)
    private String lote;

    @Column(name = "creation_username")
    private String creationUsername;
}
