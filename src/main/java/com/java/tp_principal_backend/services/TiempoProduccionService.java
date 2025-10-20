package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.model.TiempoProduccion;

import java.math.BigDecimal;
import java.util.List;

public interface TiempoProduccionService {
    TiempoProduccion agregar(TiempoProduccion tiempo);
    List<TiempoProduccion> obtenerTodos();
    BigDecimal calcularTiempoTotal(String codigoProducto, BigDecimal cantidad);
}
