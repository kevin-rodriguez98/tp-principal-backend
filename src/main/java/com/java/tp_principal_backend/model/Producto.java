package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String linea;

    @Column(nullable = false)
    private String unidad;

    @Column
    private BigDecimal stock;

    @Column
    private String lote;

    @Column(name = "creation_username")
    private String creationUsername;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "presentacion")
    private String presentacion;

    @Column(name= "envasado")
    private String envasado;
    
    @Column(name = "legajo_empleado")
    private String empleados;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
        	fechaCreacion = LocalDateTime.now();
        }
    }
}
