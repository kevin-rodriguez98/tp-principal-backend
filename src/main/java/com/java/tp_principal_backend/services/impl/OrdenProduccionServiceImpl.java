package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.*;
import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.*;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import com.java.tp_principal_backend.services.TiempoProduccionService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class OrdenProduccionServiceImpl implements OrdenProduccionService {

    @Autowired
    private OrdenProduccionDao ordenDao;

    @Autowired
    private ProductosDao productosDao;

    @Autowired
    private HistorialEtapaDao historialEtapaDao;
    
    @Autowired
    private EmpleadosDao empleadosDao;
    
    @Autowired
    private TiempoProduccionService tiempoProduccionService;

    @Autowired
    private MovimientoProductoServiceImpl movimientoProductoService;

    @Override
    public List<OrdenProduccion> obtenerTodas() {
        return ordenDao.findAll();
    }
    
    @Override
    public  OrdenProduccion marcarFinalizada(OrdenFinalizadaRequest ordenFinalizada) {
    	Empleados empleado = empleadosDao.buscarPorLegajo(ordenFinalizada.getLegajo() == null?"100":ordenFinalizada.getLegajo());
    	OrdenProduccion orden = ordenDao.findById(ordenFinalizada.getOrdenId()).orElseThrow();
        Producto producto = productosDao.findByCodigo(orden.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (ordenFinalizada.getStockProducidoReal() == null) {
        	ordenFinalizada.setStockProducidoReal(orden.getStockRequerido());
        }
        orden.setStockProducidoReal(ordenFinalizada.getStockProducidoReal());
        movimientoProductoService.egresoAutomatico(orden.getCodigoProducto(), ordenFinalizada.getStockProducidoReal(), ordenFinalizada.getDestino());
        if (producto.getStock() == null) {
            producto.setStock(BigDecimal.ZERO);
        }
        producto.setStock( producto.getStock().add(ordenFinalizada.getStockProducidoReal()));
        productosDao.save(producto);
        orden.setEstado("FINALIZADA");
        OrdenProduccion ordenResponse = ordenDao.save(orden);
        guradarHistorial(orden,empleado,"FINALIZADA");
        return ordenResponse;
    }

    @Override
    @Transactional
    public OrdenProduccion agregarOrdenNormal(OrdenProduccionNormalRequest request) {
    	Empleados empleado = empleadosDao.buscarPorLegajo(request.getLegajo()==null? "100":request.getLegajo());
    	BigDecimal tiempoProduccion = (tiempoProduccionService.calcularTiempoTotal(request.getCodigoProducto(), request.getStockRequerido())).get("tiempoEstimado");
    	
    	OrdenProduccion orden = new OrdenProduccion();
        orden.setProductoRequerido(request.getProductoRequerido());
        orden.setMarca(request.getMarca());
        orden.setEnvasado(request.getEnvasado());
        orden.setPresentacion(request.getPresentacion());
        orden.setStockRequerido(request.getStockRequerido());
        orden.setCodigoProducto(request.getCodigoProducto());
        orden.setFechaEntrega(request.getFechaEntrega());
        orden.setLote(request.getLote());
        orden.setEstado(request.getEstado() != null ? request.getEstado() : "Evaluación");
        orden.setCreationUsername(empleado.getNombre());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setImpactado(false);
        orden.setStockProducidoReal(BigDecimal.ZERO);
        orden.setEmpleado(empleado);
        orden.setTiempoProduccion(tiempoProduccion);
        ordenDao.save(orden);
        
        guradarHistorial(orden,empleado,request.getEstado());
        return orden;
    }

    @Override
    public OrdenProduccion actualizarEtapa(EtapaRequest nuevoEstado) {
    	Empleados empleado = empleadosDao.buscarPorLegajo(nuevoEstado.getLegajo());
        OrdenProduccion orden = ordenDao.findById(nuevoEstado.getIdOrden()).orElseThrow(() -> new RuntimeException("orden no encontrado"));
        
        if(nuevoEstado.getIsEstado()) {
        	if(nuevoEstado.getEstado().equals("EN_PRODUCCION")) {
            	marcarEnProduccion(orden);
            }
        	orden.setEstado(nuevoEstado.getEstado());
        }else
        	orden.setEtapa(nuevoEstado.getEstado());
        
        guradarHistorial(orden,empleado,nuevoEstado.getEstado());
        return ordenDao.save(orden);
    }
    
    private void guradarHistorial(OrdenProduccion orden, Empleados empleado, String etapa) {
    	HistorialEtapa historial = new HistorialEtapa();
        historial.setOrden(orden);
        historial.setEtapa(etapa);
        historial.setEmpleado(empleado);
        historial.setFechaCambio(LocalDateTime.now());
        historialEtapaDao.save(historial);
    }

    @Override
    public OrdenProduccion agregarNota(Integer ordenId, String nota) {
        OrdenProduccion orden = ordenDao.findById(ordenId).orElseThrow(() -> new RuntimeException("orden no encontrado"));
        orden.setNota(nota);
        return ordenDao.save(orden);
    }

    @Override
    public List<HistorialEtapasResponse> obtenerHistorialPorOrden(Integer ordenId) {
    	List<HistorialEtapa> historial = historialEtapaDao.findByOrdenIdOrderByFechaCambioAsc(ordenId);
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    	List<HistorialEtapasResponse> historialEtapas =
    	        historial.stream()
    	                 .map(h -> {
    	                     HistorialEtapasResponse dto = new HistorialEtapasResponse();
    	                     dto.setEtapa(h.getEtapa());
    	                     dto.setFechaCambio(h.getFechaCambio().format(formatter));
    	                     dto.setEmpleado(h.getEmpleado());
    	                     return dto;})
    	                 .toList();
    	
    	return historialEtapas;
    }
    
    private OrdenProduccion marcarEnProduccion(OrdenProduccion orden) {
    	Producto producto = productosDao.findByCodigo(orden.getCodigoProducto()).orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        boolean impactado = movimientoProductoService.restarInsumos(producto, orden.getStockRequerido());
        orden.setImpactado(impactado);
        return ordenDao.save(orden);
    }

}
