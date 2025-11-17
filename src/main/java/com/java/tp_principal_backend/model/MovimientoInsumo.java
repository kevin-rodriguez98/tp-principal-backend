package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_insumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String codigo;

    @Column
    private String nombre;

    @Column
    private String categoria;

    @Column
    private String marca;

    @Column
    private String unidad;

    @Column(nullable = false)
    private BigDecimal stock = BigDecimal.ZERO;

    @Column
    private String lote;

    @Column(name = "umbral_minimo_stock")
    private Integer umbralMinimoStock = 0;

    @Column
    private String tipo;

    @Column
    private Boolean impactado;

    @Column
    private String proveedor;
    
    @Column(name = "legajo_empleado")
    private String empleado;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
}
