package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.MovimientoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInsumoDao extends JpaRepository<MovimientoInsumo, Integer> {
}
