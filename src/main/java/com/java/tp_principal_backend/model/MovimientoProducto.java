package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_producto", nullable = false)
    private String codigoProducto;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private String tipo; // ingreso o egreso

    private Boolean impactado;

    @Column(name = "creation_username")
    private String creationUsername;

    private String destino;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    // Para setear la fecha automáticamente al crear un registro
    @PrePersist
    public void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
