package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.TiempoProduccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TiempoProduccionServiceImpl implements TiempoProduccionService {

    @Autowired
    private TiempoProduccionDao tiempoDao;

    @Autowired
    private ProductosDao productosDao;

    @Override
    @Transactional
    public TiempoProduccion agregar(TiempoProduccion tiempo) {
        Producto producto = productosDao.findByCodigo(tiempo.getProducto().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Validar que no exista ya un tiempo
        boolean existe = tiempoDao.findByProductoCodigo(producto.getCodigo()).isPresent();
        if (existe) {
            throw new IllegalArgumentException("El producto ya tiene un tiempo de producción definido");
        }

        tiempo.setProducto(producto);
        return tiempoDao.save(tiempo);
    }

    @Override
    public List<TiempoProduccion> obtenerTodos() {
        return tiempoDao.findAll();
    }

    @Override
    public BigDecimal calcularTiempoTotal(String codigoProducto, BigDecimal cantidad) {
        TiempoProduccion tiempo = tiempoDao.findByProductoCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no tiene tiempo de producción definido"));

        return tiempo.getTiempoPorUnidad().multiply(cantidad);
    }

    @Override
    public BigDecimal obtenerTiempoPorProducto(String codigoProducto) {
        TiempoProduccion tiempo = tiempoDao.findByProductoCodigo(codigoProducto)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró tiempo de producción para el producto con código: " + codigoProducto));
        return tiempo.getTiempoPorUnidad();
    }
}
