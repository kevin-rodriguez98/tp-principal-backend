package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.dto.MovimientoProductoResponse;
import com.java.tp_principal_backend.model.MovimientoProducto;
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

    @PostMapping("/agregarautomatizado")
    public ResponseEntity<MovimientoProducto> agregarMovimientoAutomatic(@RequestBody MovimientoProductoRequest request) {
        MovimientoProducto movimiento = movimientoService.agregarMovimiento(request);
        return ResponseEntity.ok(movimiento);
    }

    @PostMapping("/agregar")
    public ResponseEntity<MovimientoProducto> agregarMovimientoNormal(@RequestBody MovimientoProductoRequest request) {
        MovimientoProducto movimiento = movimientoService.agregarMovimientoNormal(request);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<MovimientoProductoResponse> > obtenerMovimientos(){
    	List<MovimientoProductoResponse>  movimientos = movimientoService.obtener();
        return ResponseEntity.ok(movimientos);
    }
}
