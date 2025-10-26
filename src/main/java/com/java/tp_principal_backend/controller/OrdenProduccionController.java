package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionRequest;
import com.java.tp_principal_backend.model.HistorialEtapa;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orden-produccion")
public class OrdenProduccionController {
    @Autowired
    private OrdenProduccionService ordenService;

    @PostMapping("/agregarautomatizado")
    public ResponseEntity<OrdenProduccion> agregarOrden(@RequestBody OrdenProduccionRequest request) {
        OrdenProduccion orden = ordenService.agregarOrden(request);
        return ResponseEntity.ok(orden);
    }

    @PostMapping("/agregar")
    public ResponseEntity<OrdenProduccion> agregarOrden(@RequestBody OrdenProduccionNormalRequest request) {
        OrdenProduccion orden = ordenService.agregarOrdenNormal(request);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<OrdenProduccion>> obtenerOrdenes() {
        List<OrdenProduccion> ordenes = ordenService.obtenerTodas();
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/marcar-en-produccion/{id}")
    public ResponseEntity<OrdenProduccion> marcarEnProduccion(
            @PathVariable Integer id,
            @RequestParam String codigoProducto) {
        OrdenProduccion ordenActualizada = ordenService.marcarEnProduccion(id, codigoProducto);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PutMapping("/finalizar/{ordenId}")
    public ResponseEntity<OrdenProduccion> marcarFinalizada(
            @PathVariable Integer ordenId,
            @RequestParam(required = false) BigDecimal stockProducidoReal,
            @RequestParam String destino) {
        // Ahora se pasa destino para el egreso automático
        OrdenProduccion orden = ordenService.marcarFinalizada(ordenId, stockProducidoReal, destino);
        return ResponseEntity.ok(orden);
    }

    @PutMapping("/cancelar/{ordenId}")
    public ResponseEntity<OrdenProduccion> cancelarOrden(@PathVariable Integer ordenId) {
        OrdenProduccion orden = ordenService.marcarCancelada(ordenId);
        return ResponseEntity.ok(orden);
    }

    @PutMapping("/notificar-etapa/{ordenId}")
    public ResponseEntity<OrdenProduccion> notificarEtapa(
            @PathVariable Integer ordenId,
            @RequestBody String nuevaEtapa) {

        OrdenProduccion ordenActualizada = ordenService.actualizarEtapa(ordenId, nuevaEtapa.trim());

        if (ordenActualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ordenActualizada);
    }

    @PutMapping("/agregar-nota/{ordenId}")
    public ResponseEntity<OrdenProduccion> agregarNota(
            @PathVariable Integer ordenId,
            @RequestBody String nota) {

        OrdenProduccion ordenActualizada = ordenService.agregarNota(ordenId, nota.trim());

        if (ordenActualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ordenActualizada);
    }

    @GetMapping("/{ordenId}/historial-etapas")
    public ResponseEntity<List<HistorialEtapa>> obtenerHistorialEtapas(@PathVariable Integer ordenId) {
        List<HistorialEtapa> historial = ordenService.obtenerHistorialPorOrden(ordenId);

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historial);
    }
}
