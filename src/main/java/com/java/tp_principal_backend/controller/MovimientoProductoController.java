package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.model.MovimientoInsumo;
import com.java.tp_principal_backend.model.MovimientoProducto;
import com.java.tp_principal_backend.services.MovimientoInsumoService;
import com.java.tp_principal_backend.services.MovimientoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
