package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_produccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_producto")
    private String codigoProducto;

    @Column(name = "stock_requerido", nullable = false)
    private BigDecimal stockRequerido;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private String estado = "Evaluación";

    private String lote;

    @Column(name = "stock_producido_real")
    private BigDecimal stockProducidoReal = BigDecimal.ZERO;

    @Column(name = "creation_username")
    private String creationUsername;

    @Column(name = "producto_requerido", nullable = false)
    private String productoRequerido;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "impactado")
    private Boolean impactado = false;
}
