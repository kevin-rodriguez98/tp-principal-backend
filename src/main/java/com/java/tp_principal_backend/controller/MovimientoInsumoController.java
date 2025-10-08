package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.model.MovimientoInsumo;
import com.java.tp_principal_backend.services.MovimientoInsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimiento-insumo")
public class MovimientoInsumoController {

    @Autowired
    private MovimientoInsumoService movimientoService;

    @PostMapping("/agregar")
    public ResponseEntity<MovimientoInsumo> agregarMovimiento(@RequestBody MovimientoInsumoRequest request) {
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<MovimientoInsumo>> obtenerMovimiento() {
        List<MovimientoInsumo> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }
}
