package com.java.tp_principal_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class HistorialEtapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenProduccion orden;
    
    @Column
    private String etapa;
    
    @Column
    private LocalDateTime fechaCambio;
    
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleados empleado;
}
