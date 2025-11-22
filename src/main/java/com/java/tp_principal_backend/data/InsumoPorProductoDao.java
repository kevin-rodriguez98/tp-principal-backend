package com.java.tp_principal_backend.data;

import com.java.tp_principal_backend.model.InsumoPorProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsumoPorProductoDao extends JpaRepository<InsumoPorProducto, Integer> {

    List<InsumoPorProducto> findByProductoId(Integer idProducto);
    InsumoPorProducto findFirstByInsumoIdAndProductoId(Integer idInsumo, Integer idProducto);
}
