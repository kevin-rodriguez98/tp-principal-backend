package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.*;
import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.*;
import com.java.tp_principal_backend.services.OrdenProduccionService;
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
    private InsumoPorProductoDao insumoPorProductoDao;

    @Autowired
    private InsumosDao insumoDao;

    @Autowired
    private HistorialEtapaDao historialEtapaDao;
    
    @Autowired
    private EmpleadosDao empleadosDao;

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
        
        if (Boolean.FALSE.equals(orden.getImpactado())) 
        {
            movimientoProductoService.egresoAutomatico(orden.getCodigoProducto(), ordenFinalizada.getStockProducidoReal(), ordenFinalizada.getDestino());
            List<InsumoPorProducto> receta = insumoPorProductoDao.findByProductoId(producto.getId());

            if (receta == null || receta.isEmpty()) {
                log.warn("⚠ El producto {} no tiene insumos asociados. No se descontó receta.",
                        producto.getCodigo());
            } else {
                for (InsumoPorProducto r : receta) {
                    BigDecimal cantidadARestar = r.getStockNecesarioInsumo().multiply(ordenFinalizada.getStockProducidoReal());
                    restarStockInsumo(r.getInsumo().getCodigo(),cantidadARestar );
                    log.info("Insumo {} descontado: {} {}",r.getInsumo().getCodigo(),cantidadARestar,r.getInsumo().getUnidad());
                }
            }
            orden.setImpactado(true);
        }

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
    	OrdenProduccion orden = new OrdenProduccion();

        orden.setProductoRequerido(request.getProductoRequerido());
        orden.setMarca(request.getMarca());
        orden.setEnvasado(request.getEnvasado());
        orden.setPresentacion(request.getPresentacion());
        orden.setStockRequerido(request.getStockRequerido());
        orden.setCodigoProducto(request.getCodigoProducto());
        orden.setFechaEntrega(request.getFechaEntrega());
        orden.setLote(request.getLote());

        // Valores por defecto o calculados
        orden.setEstado(
                request.getEstado() != null ? request.getEstado() : "Evaluación"
        );
        orden.setCreationUsername(empleado.getNombre());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setImpactado(false);
        orden.setStockProducidoReal(BigDecimal.ZERO);
        orden.setEmpleado(empleado);
        ordenDao.save(orden);
        guradarHistorial(orden,empleado,request.getEstado());
        return orden;
    }

    @Override
    public OrdenProduccion actualizarEtapa(EtapaRequest nuevoEstado) {
    	Empleados empleado = empleadosDao.buscarPorLegajo(nuevoEstado.getLegajo());
        OrdenProduccion orden = ordenDao.findById(nuevoEstado.getIdOrden()).orElseThrow(() -> new RuntimeException("orden no encontrado"));
        
        if(nuevoEstado.getIsEstado()) {
        	orden.setEstado(nuevoEstado.getEstado());
        	if(nuevoEstado.getEstado().equals("EN_PRODUCCION")) {
            	marcarEnProduccion(orden);
            }
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
    	                     return dto;
    	                 })
    	                 .toList();
    	
    	return historialEtapas;
    }

    private void restarStockInsumo(String codigoInsumo, BigDecimal cantidad) {

        Insumo insumo = insumoDao.findByCodigo(codigoInsumo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Insumo no encontrado para el código: " + codigoInsumo));

        if (insumo.getStock() == null) {
            insumo.setStock(BigDecimal.ZERO);
        }

        BigDecimal nuevoStock = insumo.getStock().subtract(cantidad);

        if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("⚠ El insumo {} quedó con stock negativo al restar {}",
                    codigoInsumo, cantidad);
        }

        insumo.setStock(nuevoStock);
        insumoDao.save(insumo);
    }
    
    private OrdenProduccion marcarEnProduccion(OrdenProduccion orden) {
    	Producto producto = productosDao.findByCodigo(orden.getCodigoProducto()).orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        boolean impactado = movimientoProductoService.restarInsumos(producto, orden.getStockRequerido());
        orden.setImpactado(impactado);
        return ordenDao.save(orden);
    }

}
