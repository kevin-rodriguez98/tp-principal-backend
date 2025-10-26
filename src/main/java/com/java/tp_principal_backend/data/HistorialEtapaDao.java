package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.HistorialEtapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEtapaDao extends JpaRepository<HistorialEtapa, Integer> {
    List<HistorialEtapa> findByOrdenIdOrderByFechaCambioAsc(Integer ordenId);
}