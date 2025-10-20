package com.java.tp_principal_backend.controller;

import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.ProductosServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    @Autowired
    private ProductosServices productosServices;

    @PostMapping("/agregar")
    public ResponseEntity<Producto> agregarProducto(@RequestBody ProductoRequest request) {
        Producto nuevoProducto = productosServices.agregarProducto(request);
        return ResponseEntity.ok(nuevoProducto);
    }

    @GetMapping("/obtener")
    public ResponseEntity<List<Producto>> obtenerTodos() {
        List<Producto> productos = productosServices.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/editar/{codigo}")
    public ResponseEntity<Producto> editarProducto(
            @PathVariable String codigo,
            @RequestBody Map<String, Object> cambios) {
        Producto productoEditado = productosServices.editarProducto(codigo, cambios);
        return ResponseEntity.ok(productoEditado);
    }

    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<String> eliminarProducto(@PathVariable String codigo) {
        productosServices.eliminarProducto(codigo);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}
