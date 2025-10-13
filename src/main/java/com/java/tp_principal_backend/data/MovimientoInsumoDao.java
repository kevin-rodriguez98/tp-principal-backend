package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.MovimientoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInsumoDao extends JpaRepository<MovimientoInsumo, Integer> {

    List<MovimientoInsumo> findByTipoIgnoreCase(String tipo);

}
