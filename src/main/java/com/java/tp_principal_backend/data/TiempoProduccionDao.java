package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.TiempoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TiempoProduccionDao extends JpaRepository<TiempoProduccion, Long> {
    Optional<TiempoProduccion> findByProductoCodigo(String codigoProducto);

    @Query("SELECT t.tiempoEtapa FROM TiempoProduccion t WHERE t.etapa = :etapa")
    Integer findTiempoEtapaByEtapa(String etapa);

    @Query("SELECT SUM(t.tiempoEtapa) FROM TiempoProduccion t")
    Integer sumAllTiempos();
}
