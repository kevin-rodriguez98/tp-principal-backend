package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<?> notificarEtapa(@RequestBody EtapaRequest nuevaEtapa) {
        try {
			return ResponseEntity.ok(ordenService.actualizarEtapa(nuevaEtapa));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Cantidad insuficiente de insumos");
		}
       
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
    
    @GetMapping("/obtener-ultimas-orden/{dias}")
    public ResponseEntity<List<OrdenProduccion>> obtenerUltimasOrdenes(@PathVariable Integer dias){
    	try {
			return ResponseEntity.ok(ordenService.obtenerOrdenesUltimosdias(dias));
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
    }
    
    @GetMapping("/obtener-ordenes-dia")
    public ResponseEntity<List<OrdenProduccion>> obtenerOrdenesFecha( @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
    	try {
			return ResponseEntity.ok(ordenService.obtenerOrdenesFecha(fecha));
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
    }
    
}
