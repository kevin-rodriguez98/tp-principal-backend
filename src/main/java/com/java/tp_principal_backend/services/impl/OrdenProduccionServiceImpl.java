package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.HistorialEtapaDao;
import com.java.tp_principal_backend.data.OrdenProduccionDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionRequest;
import com.java.tp_principal_backend.model.HistorialEtapa;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrdenProduccionServiceImpl implements OrdenProduccionService {

    @Autowired
    private OrdenProduccionDao ordenDao;

    @Autowired
    private ProductosDao productosDao;

    @Autowired
    private HistorialEtapaDao historialEtapaDao;

    @Autowired
    private MovimientoProductoServiceImpl movimientoProductoService;

    private String randomUsername() {
        String[] names = {"Ana", "Luis", "Juan", "María", "Carlos", "Selena", "Kevin", "Juliana", "Matias"};
        return names[new Random().nextInt(names.length)];
    }

    @Override
    public OrdenProduccion agregarOrden(OrdenProduccionRequest request) {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setProductoRequerido(request.getProductoRequerido());
        orden.setMarca(request.getMarca());
        orden.setStockRequerido(request.getStockRequerido());
        orden.setFechaEntrega(request.getFechaEntrega());
        orden.setEstado("Evaluación");
        orden.setCreationUsername(randomUsername());

        return ordenDao.save(orden);
    }

    @Override
    public List<OrdenProduccion> obtenerTodas() {
        return ordenDao.findAll();
    }

    @Override
    @Transactional
    public OrdenProduccion marcarEnProduccion(Integer ordenId, String codigoProducto) {
        OrdenProduccion orden = ordenDao.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        Producto producto = productosDao.findByCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // 1️⃣ Marcar la orden como en producción
        orden.setEstado("EN_PRODUCCION");
        orden.setCodigoProducto(codigoProducto);

        // 2️⃣ Restar los insumos usando el método público
        boolean impactado = movimientoProductoService.restarInsumos(producto, orden.getStockRequerido());
        orden.setImpactado(impactado);

        return ordenDao.save(orden);
    }

    @Override
    @Transactional
    public OrdenProduccion marcarCancelada(Integer ordenId) {
        OrdenProduccion orden = ordenDao.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        // Validar estado
        if (!"EVALUACION".equalsIgnoreCase(orden.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una orden que ya está en producción o finalizada.");
        }

        // Cambiar estado a CANCELADA
        orden.setEstado("CANCELADA");

        // Guardar cambios
        return ordenDao.save(orden);
    }

    @Override
    @Transactional
    public OrdenProduccion marcarFinalizada(Integer ordenId, BigDecimal stockProducidoReal, String destino) {
        OrdenProduccion orden = ordenDao.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        if (!"EN_PRODUCCION".equalsIgnoreCase(orden.getEstado())) {
            throw new IllegalStateException("Solo se puede finalizar una orden que está en producción.");
        }

        // Asignar lote desde el producto
        Producto producto = productosDao.findByCodigo(orden.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        orden.setLote(producto.getLote());

        // Asignar stock producido real
        if (stockProducidoReal == null) {
            stockProducidoReal = orden.getStockRequerido();
        }
        orden.setStockProducidoReal(stockProducidoReal);

        // Egreso automático si los insumos no se impactaron antes
        if (Boolean.FALSE.equals(orden.getImpactado())) {
            movimientoProductoService.egresoAutomatico(orden.getCodigoProducto(), stockProducidoReal, destino);
            orden.setImpactado(true);
        }

        // Cambiar estado a FINALIZADA_ENTREGADA
        orden.setEstado("FINALIZADA_ENTREGADA");

        return ordenDao.save(orden);
    }

    @Override
    @Transactional
    public OrdenProduccion agregarOrdenNormal(OrdenProduccionNormalRequest request) {
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
        orden.setCreationUsername(randomUsername());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setImpactado(false);
        orden.setStockProducidoReal(BigDecimal.ZERO);

        return ordenDao.save(orden);
    }

    @Override
    public OrdenProduccion actualizarEtapa(Integer ordenId, String nuevaEtapa) {
        Optional<OrdenProduccion> optional = ordenDao.findById(ordenId);
        if (optional.isEmpty()) {
            return null;
        }

        OrdenProduccion orden = optional.get();

        // 🔹 1. Registrar la nueva etapa en el historial
        HistorialEtapa historial = new HistorialEtapa();
        historial.setOrden(orden);
        historial.setEtapa(nuevaEtapa);
        historial.setFechaCambio(LocalDateTime.now());
        historialEtapaDao.save(historial);

        // 🔹 2. Actualizar la etapa actual de la orden
        orden.setEtapa(nuevaEtapa);

        // 🔹 3. Guardar los cambios en la orden
        return ordenDao.save(orden);
    }

    @Override
    public OrdenProduccion agregarNota(Integer ordenId, String nota) {
        Optional<OrdenProduccion> optional = ordenDao.findById(ordenId);
        if (optional.isEmpty()) {
            return null;
        }

        OrdenProduccion orden = optional.get();
        orden.setNota(nota);
        return ordenDao.save(orden);
    }

    @Override
    public List<HistorialEtapa> obtenerHistorialPorOrden(Integer ordenId) {
        return historialEtapaDao.findByOrdenIdOrderByFechaCambioAsc(ordenId);
    }
}
