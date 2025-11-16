package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "insumo_por_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoPorProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Relación con Producto
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // 🔗 Relación con Insumo
    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @Column(name = "stock_necesario_insumo", nullable = false)
    private BigDecimal stockNecesarioInsumo;
    
    @Column(name = "unidad", nullable = false)
    private String unidad;
}
