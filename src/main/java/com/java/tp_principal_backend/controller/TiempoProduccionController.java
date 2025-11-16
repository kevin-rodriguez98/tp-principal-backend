package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.TiempoProduccionRequest;
import com.java.tp_principal_backend.dto.TiempoProduccionResponse;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.TiempoProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tiempo-produccion")
public class TiempoProduccionController {

    @Autowired
    private TiempoProduccionService tiempoService;

    @PostMapping("/agregar")
    public ResponseEntity<TiempoProduccion> agregar(@RequestBody TiempoProduccionRequest request) {
        TiempoProduccion tiempo = new TiempoProduccion();
        tiempo.setTiempoPorUnidad(request.getTiempoPorUnidad());
        tiempo.setProducto(new com.java.tp_principal_backend.model.Producto() {{
            setCodigo(request.getCodigoProducto());
        }});

        return ResponseEntity.ok(tiempoService.agregar(tiempo));
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<TiempoProduccion>> obtenerTodos() {
        return ResponseEntity.ok(tiempoService.obtenerTodos());
    }

    @GetMapping("/obtener-tiempo-unitario")
    public ResponseEntity<TiempoProduccionResponse> obtenerTiempoPorProducto(@RequestParam String codigoProducto) {
    	TiempoProduccionResponse tiempo = tiempoService.obtenerTiempoPorProducto(codigoProducto);
        return ResponseEntity.ok(tiempo);
    }

    @GetMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTiempo(
            @RequestParam String codigoProducto,
            @RequestParam BigDecimal cantidad) {
        return ResponseEntity.ok(tiempoService.calcularTiempoTotal(codigoProducto, cantidad));
    }
}
