package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsumosDao extends JpaRepository<Insumo, Integer> {

    Optional<Insumo> findByCodigo(String codigo);
    void deleteByCodigo(String codigo);
}