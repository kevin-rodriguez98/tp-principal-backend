package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.model.MovimientoProducto;

import java.math.BigDecimal;
import java.util.List;

public interface MovimientoProductoService {
    MovimientoProducto agregarMovimiento(MovimientoProductoRequest request);
    MovimientoProducto egresoAutomatico(String codigoProducto, BigDecimal cantidad, String destino);
    List<MovimientoProducto> obtener();
}
