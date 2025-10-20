package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.InsumoNecesarioResponse;
import com.java.tp_principal_backend.dto.InsumoPorProductoRequest;
import com.java.tp_principal_backend.model.InsumoPorProducto;

import java.math.BigDecimal;
import java.util.List;

public interface InsumoPorProductoService {
    InsumoPorProducto agregarReceta(InsumoPorProductoRequest request);

    List<InsumoNecesarioResponse> calcularInsumosNecesarios(String codigoProducto, BigDecimal cantidadProducto);
}
