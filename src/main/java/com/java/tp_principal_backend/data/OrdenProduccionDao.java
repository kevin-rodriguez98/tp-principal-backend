package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenProduccionDao extends JpaRepository<OrdenProduccion, Integer> {
}
