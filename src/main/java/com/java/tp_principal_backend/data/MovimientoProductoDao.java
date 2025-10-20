package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.MovimientoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoProductoDao extends JpaRepository<MovimientoProducto, Integer> {
}
