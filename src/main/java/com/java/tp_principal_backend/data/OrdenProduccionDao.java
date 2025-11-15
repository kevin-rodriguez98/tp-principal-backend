package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdenProduccionDao extends JpaRepository<OrdenProduccion, Integer> {
    //Optional<OrdenProduccion> findByCodigo(Integer id);
    Optional<OrdenProduccion> findByCodigoProducto(String codigoProducto);
}
