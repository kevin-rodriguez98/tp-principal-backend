package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.TiempoProduccionRequest;
import com.java.tp_principal_backend.dto.TiempoProduccionResponse;
import com.java.tp_principal_backend.model.TiempoProduccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TiempoProduccionService {
	TiempoProduccion agregar(TiempoProduccionRequest tiempo);
    List<TiempoProduccion> obtenerTodos();
    Map<String, BigDecimal> calcularTiempoTotal(String codigoProducto, BigDecimal cantidad);
    TiempoProduccionResponse obtenerTiempoPorProducto(String codigoProducto);
    BigDecimal obtenertiemposProduccion(String codigoProducto);
}
