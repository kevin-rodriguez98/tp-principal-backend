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

    @Column(name = "tiempo_preparacion")
    private BigDecimal tiempoPreparacion;

    @Column(name = "tiempo_ciclo")
    private BigDecimal tiempoCiclo;
    
    @Column(name = "cantidad_max_tanda")
    private BigDecimal cantidaTanda;
}
