package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.dto.ProductosResponse;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.ProductosServices;

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
    private TiempoProduccionDao tiempoProduccionDao;
    
    @Autowired
    private EmpleadosDao empleadosDao;

    @Override
    public List<ProductosResponse> obtenerTodosLosProductos() {
    	return productosDao.findAll().stream()
    			.map(P -> new ProductosResponse(P,empleadosDao.buscarPorLegajo(P.getEmpleados())))
    			.toList();
    }
    

    @Override
    public Producto agregarProducto(ProductoRequest request) {
        Optional<Producto> existente = productosDao.findByCodigo(request.getCodigo());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + request.getCodigo());
        }

        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setLinea(request.getLinea());
        producto.setStock(BigDecimal.ZERO);
        producto.setUnidad(request.getUnidad());
        producto.setEmpleados(request.getLegajoResponsable());
        producto.setCreationUsername("");
        producto.setPresentacion(request.getPresentacion());
        
        Producto response =  productosDao.save(producto);
        
        TiempoProduccion tiempoInicial = new TiempoProduccion();
        tiempoInicial.setProducto(response);
        tiempoInicial.setTiempoCiclo(BigDecimal.ZERO);
        tiempoInicial.setTiempoPreparacion(BigDecimal.ZERO);
        tiempoInicial.setCantidaTanda(BigDecimal.ZERO);
        tiempoProduccionDao.save(tiempoInicial);
        return response;
    }

    @Override
    public Producto editarProducto(String codigo, Map<String, Object> cambios) {
        Producto producto = productosDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe producto con código: " + codigo));

        cambios.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre" -> producto.setNombre((String) valor);
                case "categoria" -> producto.setCategoria((String) valor);
                case "marca" -> producto.setLinea((String) valor);
                case "unidad" -> producto.setUnidad((String) valor);
                case "stock" -> producto.setStock(new BigDecimal(valor.toString()));
                case "codigo" -> {
                    String nuevoCodigo = (String) valor;

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

        return productosDao.save(producto);
    }

    @Override
    public void eliminarProducto(String codigo) {
        Producto producto = productosDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe producto con el código: " + codigo));

        productosDao.delete(producto);
    }
}
