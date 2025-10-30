package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.TiempoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TiempoProduccionDao extends JpaRepository<TiempoProduccion, Long> {
    Optional<TiempoProduccion> findByProductoCodigo(String codigoProducto);
    Optional<TiempoProduccion> findByProductoCodigoProducto(String codigoProducto);
}
