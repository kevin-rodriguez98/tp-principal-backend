package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.OrdenProduccion;

import java.time.LocalDate;
import java.util.List;

public interface OrdenProduccionService {
    List<OrdenProduccion> obtenerTodas();
    OrdenProduccion marcarFinalizada(OrdenFinalizadaRequest ordenFinalizada);
    OrdenProduccion agregarOrdenNormal(OrdenProduccionNormalRequest request);
    OrdenProduccion actualizarEtapa(EtapaRequest nuevaEtapa);
    OrdenProduccion agregarNota(Integer ordenId, String nota);
    List<HistorialEtapasResponse> obtenerHistorialPorOrden(Integer ordenId);
    
    List<OrdenProduccion> obtenerOrdenesUltimosdias (Integer dias); 
    List<OrdenProduccion> obtenerOrdenesFecha(LocalDate fecha);
}
