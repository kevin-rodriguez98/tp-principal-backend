package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.model.Producto;

import java.util.List;
import java.util.Map;

public interface ProductosServices {

    Producto agregarProducto(ProductoRequest request);

    List<Producto> obtenerTodosLosProductos();

    Producto editarProducto(String codigo, Map<String, Object> cambios);

    void eliminarProducto(String codigo);
}
