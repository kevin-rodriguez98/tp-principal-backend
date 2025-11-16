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
    @JoinColumn(name = "producto_id", unique = true)
    private Producto producto;

    @Column(name = "tiempo_por_unidad")
    private BigDecimal tiempoPorUnidad;

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "stock_soportado")
    private Integer stockSoportado;

    @Column(name = "unidad")
    private String unidad;

    @Column(name = "tiempo_etapa")
    private Integer tiempoEtapa;
}
