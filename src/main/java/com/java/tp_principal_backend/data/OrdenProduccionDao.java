package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenProduccionDao extends JpaRepository<OrdenProduccion, Integer> {
    Optional<OrdenProduccion> findByCodigoProducto(String codigoProducto);
    
    List<OrdenProduccion> findByFechaCreacionGreaterThanEqual(LocalDateTime fechaDesde);
    List<OrdenProduccion> findByFechaCreacionBetween(LocalDateTime desde, LocalDateTime hasta);
    
}
