package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.ProductosServices;
import com.java.tp_principal_backend.services.TiempoProduccionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductosServicesImpl implements ProductosServices {
    @Autowired
    private ProductosDao productosDao;
    
    @Autowired
    private TiempoProduccionService tiempoProduccionService;
    
    @Autowired
    private EmpleadosDao empleadosDao;

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        return productosDao.findAll();
    }

    @Override
    public Producto agregarProducto(ProductoRequest request) {
        // 1️⃣ Verificar si ya existe un producto con ese código
        Optional<Producto> existente = productosDao.findByCodigo(request.getCodigo());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + request.getCodigo());
        }
        
        Empleados empleado = empleadosDao.buscarPorLegajo(request.getLegajoResponsable() == null? "100" : request.getLegajoResponsable());

        // 2️⃣ Crear nuevo producto
        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setLinea(request.getLinea());
        producto.setStock(BigDecimal.ZERO);
        producto.setUnidad(request.getUnidad());
        
        producto.setEmpleados(empleado);
        producto.setCreationUsername(empleado.getLegajo() +" - "+ empleado.getNombre());
        Producto response =  productosDao.save(producto);
        TiempoProduccion tiempoInicial = new TiempoProduccion();
        tiempoInicial.setProducto(producto);
        tiempoInicial.setTiempoPorUnidad(BigDecimal.ZERO);
        tiempoProduccionService.agregar(tiempoInicial);
        return response;
    }

    @Override
    public Producto editarProducto(String codigo, Map<String, Object> cambios) {
        // 1️⃣ Buscar producto existente
        Producto producto = productosDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe producto con código: " + codigo));

        // 2️⃣ Aplicar cambios dinámicamente con validación
        cambios.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre" -> producto.setNombre((String) valor);
                case "categoria" -> producto.setCategoria((String) valor);
                case "marca" -> producto.setLinea((String) valor);
                case "unidad" -> producto.setUnidad((String) valor);
                case "stock" -> producto.setStock(new BigDecimal(valor.toString()));
                case "codigo" -> {
                    String nuevoCodigo = (String) valor;

                    // ⚠️ Si el nuevo código es diferente, verificar duplicado
                    if (!nuevoCodigo.equals(producto.getCodigo())) {
                        boolean existe = productosDao.findByCodigo(nuevoCodigo).isPresent();
                        if (existe) {
                            throw new IllegalArgumentException("Ya existe un producto con el código: " + nuevoCodigo);
                        }
                        producto.setCodigo(nuevoCodigo);
                    }
                }
            }
        });

        // 3️⃣ Guardar cambios
        return productosDao.save(producto);
    }

    @Override
    public void eliminarProducto(String codigo) {
        // 1️⃣ Buscar producto por código
        Producto producto = productosDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe producto con el código: " + codigo));

        // 2️⃣ Eliminar producto
        productosDao.delete(producto);
    }
}
