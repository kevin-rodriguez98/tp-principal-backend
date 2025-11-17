package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orden-produccion")
public class OrdenProduccionController {
    @Autowired
    private OrdenProduccionService ordenService;

    @PostMapping("/agregar")
    public ResponseEntity<OrdenProduccion> agregarOrden(@RequestBody OrdenProduccionNormalRequest request) {
        OrdenProduccion orden = ordenService.agregarOrdenNormal(request);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<OrdenProduccion>> obtenerOrdenes() {
        List<OrdenProduccion> ordenes = ordenService.obtenerTodas();
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/finalizar")
    public ResponseEntity<OrdenProduccion> marcarFinalizada(@RequestBody OrdenFinalizadaRequest ordenFinalizada) {
        OrdenProduccion orden = ordenService.marcarFinalizada(ordenFinalizada);
        return ResponseEntity.ok(orden);
    }

    @PutMapping("/notificar-etapa")
    public ResponseEntity<OrdenProduccion> notificarEtapa(@RequestBody EtapaRequest nuevaEtapa) {
        OrdenProduccion ordenActualizada = ordenService.actualizarEtapa(nuevaEtapa);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PutMapping("/agregar-nota/{ordenId}")
    public ResponseEntity<OrdenProduccion> agregarNota(@PathVariable Integer ordenId, @RequestBody String nota) {
        OrdenProduccion ordenActualizada = ordenService.agregarNota(ordenId, nota.trim());
        return ResponseEntity.ok(ordenActualizada);
    }

    @GetMapping("/{ordenId}/historial-etapas")
    public ResponseEntity<List<HistorialEtapasResponse>> obtenerHistorialEtapas(@PathVariable Integer ordenId) {
        List<HistorialEtapasResponse> historial = ordenService.obtenerHistorialPorOrden(ordenId);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}
