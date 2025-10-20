package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.InsumoNecesarioResponse;
import com.java.tp_principal_backend.dto.InsumoPorProductoRequest;
import com.java.tp_principal_backend.model.InsumoPorProducto;
import com.java.tp_principal_backend.services.InsumoPorProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/recetas")
public class InsumoPorProductoController {

    @Autowired
    private InsumoPorProductoService recetaService;

    @PostMapping("/agregar")
    public ResponseEntity<InsumoPorProducto> agregarReceta(
            @RequestBody InsumoPorProductoRequest request) {
        InsumoPorProducto receta = recetaService.agregarReceta(request);
        return ResponseEntity.ok(receta);
    }

    @GetMapping("/insumos-necesarios")
    public ResponseEntity<List<InsumoNecesarioResponse>> obtenerInsumosNecesarios(
            @RequestParam String codigoProducto,
            @RequestParam BigDecimal cantidad) {

        List<InsumoNecesarioResponse> resultado = recetaService.calcularInsumosNecesarios(codigoProducto, cantidad);
        return ResponseEntity.ok(resultado);
    }
}
