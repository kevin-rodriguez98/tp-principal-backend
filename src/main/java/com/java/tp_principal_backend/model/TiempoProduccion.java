package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tiempo_produccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiempoProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "tiempo_por_unidad", nullable = false)
    private BigDecimal tiempoPorUnidad;
}
