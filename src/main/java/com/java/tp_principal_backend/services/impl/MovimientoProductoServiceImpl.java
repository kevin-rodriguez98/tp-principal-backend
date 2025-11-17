package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.InsumoPorProductoDao;
import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.MovimientoProductoDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.dto.MovimientoProductoResponse;
import com.java.tp_principal_backend.model.*;
import com.java.tp_principal_backend.services.MovimientoProductoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoProductoServiceImpl implements MovimientoProductoService {

    @Autowired
    private MovimientoProductoDao movimientoDao;

    @Autowired
    private ProductosDao productosDao;
    
    @Autowired
    private EmpleadosDao empleadosDao;

    @Autowired
    private InsumosDao insumosDao;

    @Autowired
    private InsumoPorProductoDao recetaDao;

    @Override
    @Transactional
    public MovimientoProducto agregarMovimiento(MovimientoProductoRequest request) {

        if (!"egreso".equalsIgnoreCase(request.getTipo())) {
            throw new IllegalArgumentException("Solo se permiten movimientos de tipo 'egreso'.");
        }
        Producto producto = productosDao.findByCodigo(request.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        boolean impactado = restarInsumos(producto, request.getCantidad());

        MovimientoProducto movimiento = new MovimientoProducto();
        movimiento.setCodigoProducto(request.getCodigoProducto());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setTipo(request.getTipo());
        movimiento.setImpactado(impactado);
        movimiento.setDestino(request.getDestino());
        movimiento.setCreationUsername("");
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setCategoria(request.getCategoria());
        movimiento.setMarca(request.getMarca());
        movimiento.setUnidad(request.getUnidad());
        movimiento.setLote(request.getLote());
        movimiento.setNombre(request.getNombre());
        movimiento.setEmpleado(request.getLegajo());
        return movimientoDao.save(movimiento);
    }
    
    public boolean restarInsumos(Producto producto, BigDecimal cantidad) {
        List<InsumoPorProducto> recetas = recetaDao.findByProductoId(producto.getId());
        boolean impactado = true;

        for (InsumoPorProducto receta : recetas) {
            Insumo insumo = receta.getInsumo();
            BigDecimal stockNecesario = receta.getStockNecesarioInsumo().multiply(cantidad);

            if (insumo.getStock().compareTo(stockNecesario) < 0) {
                impactado = false;
            } else {
                insumo.setStock(insumo.getStock().subtract(stockNecesario));
                insumosDao.save(insumo);
            }
        }

        return impactado;
    }

    @Override
    @Transactional
    public MovimientoProducto egresoAutomatico(String codigoProducto, BigDecimal cantidad, String destino) {
        Producto producto = productosDao.findByCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        boolean impactado = restarInsumos(producto, cantidad);

        MovimientoProducto movimiento = new MovimientoProducto();
        movimiento.setCodigoProducto(codigoProducto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo("egreso");
        movimiento.setImpactado(impactado);
        movimiento.setDestino(destino);
        movimiento.setCreationUsername("");
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setCategoria(producto.getCategoria());
        movimiento.setMarca(producto.getLinea());
        movimiento.setUnidad(producto.getUnidad());
        movimiento.setLote(producto.getLote());
        movimiento.setNombre(producto.getNombre());

        return movimientoDao.save(movimiento);
    }

    @Override
    public List<MovimientoProductoResponse> obtener() {
    	return movimientoDao.findAll().stream()
	    			.map(m -> new MovimientoProductoResponse(m,
	    			empleadosDao.buscarPorLegajo(m.getEmpleado())))
	    			.toList();
    }

    @Override
    @Transactional
    public MovimientoProducto agregarMovimientoNormal(MovimientoProductoRequest request) {
        MovimientoProducto movimiento = new MovimientoProducto();
        movimiento.setCodigoProducto(request.getCodigoProducto());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setTipo(request.getTipo());
        movimiento.setDestino(request.getDestino());
        movimiento.setCategoria(request.getCategoria());
        movimiento.setMarca(request.getMarca());
        movimiento.setUnidad(request.getUnidad());
        movimiento.setLote(request.getLote());
        movimiento.setNombre(request.getNombre());
        movimiento.setImpactado(false);
        movimiento.setCreationUsername("");
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setEmpleado(request.getLegajo());
        return movimientoDao.save(movimiento);
    }
}
