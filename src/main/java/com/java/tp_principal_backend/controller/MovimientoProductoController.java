package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.model.MovimientoInsumo;
import com.java.tp_principal_backend.model.MovimientoProducto;
import com.java.tp_principal_backend.services.MovimientoInsumoService;
import com.java.tp_principal_backend.services.MovimientoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimiento-producto")
public class MovimientoProductoController {

    @Autowired
    private MovimientoProductoService movimientoService;

    @PostMapping("/agregar")
    public ResponseEntity<MovimientoProducto> agregarMovimiento(@RequestBody MovimientoProductoRequest request) {
        MovimientoProducto movimiento = movimientoService.agregarMovimiento(request);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<MovimientoProducto>> obtenerMovimientos(){
        List<MovimientoProducto> movimientos = movimientoService.obtener();
        return ResponseEntity.ok(movimientos);
    }
}
