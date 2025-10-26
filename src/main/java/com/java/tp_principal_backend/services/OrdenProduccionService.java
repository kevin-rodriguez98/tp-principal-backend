package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionRequest;
import com.java.tp_principal_backend.model.HistorialEtapa;
import com.java.tp_principal_backend.model.OrdenProduccion;

import java.math.BigDecimal;
import java.util.List;

public interface OrdenProduccionService {
    OrdenProduccion agregarOrden(OrdenProduccionRequest request);
    List<OrdenProduccion> obtenerTodas();
    OrdenProduccion marcarEnProduccion(Integer ordenId, String codigoProducto);
    OrdenProduccion marcarCancelada(Integer ordenId);
    OrdenProduccion marcarFinalizada(Integer ordenId, BigDecimal stockProducidoReal, String destino);
    OrdenProduccion agregarOrdenNormal(OrdenProduccionNormalRequest request);
    OrdenProduccion actualizarEtapa(Integer ordenId, String nuevaEtapa);
    OrdenProduccion agregarNota(Integer ordenId, String nota);
    List<HistorialEtapa> obtenerHistorialPorOrden(Integer ordenId);
}
