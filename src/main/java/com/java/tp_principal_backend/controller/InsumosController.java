package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.InsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.services.InsumosServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos/insumos")
public class InsumosController {

    @Autowired
    private InsumosServices insumosServices;

    @PostMapping("/agregar")
    public ResponseEntity<Insumo> agregarInsumo(@RequestBody InsumoRequest request) {
        Insumo nuevoInsumo = insumosServices.agregarInsumo(request);
        return ResponseEntity.ok(nuevoInsumo);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<Insumo>> obtenerTodos() {
        List<Insumo> insumos = insumosServices.obtenerTodosLosInsumos();
        return ResponseEntity.ok(insumos);
    }

    @PutMapping("/editar/{codigo}")
    public ResponseEntity<Insumo> editarInsumo(
            @PathVariable String codigo,
            @RequestBody Map<String, Object> cambios) {
        Insumo insumoEditado = insumosServices.editarInsumo(codigo, cambios);
        return ResponseEntity.ok(insumoEditado);
    }

    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<String> eliminarInsumo(@PathVariable String codigo) {
        insumosServices.eliminarInsumo(codigo);
        return ResponseEntity.ok("Insumo eliminado correctamente");
    }
}
