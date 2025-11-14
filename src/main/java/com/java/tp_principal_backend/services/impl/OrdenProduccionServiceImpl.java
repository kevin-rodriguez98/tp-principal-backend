package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.*;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionRequest;
import com.java.tp_principal_backend.model.*;
import com.java.tp_principal_backend.services.OrdenProduccionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private MovimientoProductoServiceImpl movimientoProductoService;

    private String randomUsername() {
        String[] names = {"Ana", "Luis", "Juan", "María", "Carlos", "Selena", "Kevin", "Juliana", "Matias"};
        return names[new Random().nextInt(names.length)];
    }

//    @Override
//    public OrdenProduccion agregarOrden(OrdenProduccionRequest request) {
//        OrdenProduccion orden = new OrdenProduccion();
//        orden.setProductoRequerido(request.getProductoRequerido());
//        orden.setMarca(request.getMarca());
//        orden.setStockRequerido(request.getStockRequerido());
//        orden.setFechaEntrega(request.getFechaEntrega());
//        orden.setEstado("Evaluación");
//        orden.setCreationUsername(randomUsername());
//
//        return ordenDao.save(orden);
//    }

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

        registrarHistorial(orden, "EN_PRODUCCION");
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

        registrarHistorial(orden, "CANCELADA");
        // Guardar cambios
        return ordenDao.save(orden);
    }

    @Override
    @Transactional
    public OrdenProduccion marcarFinalizada(
            Integer ordenId,
            BigDecimal stockProducidoReal,
            String destino
    ) {
        OrdenProduccion orden = ordenDao.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        if (!"EN_PRODUCCION".equalsIgnoreCase(orden.getEstado())) {
            throw new IllegalStateException("Solo se puede finalizar una orden que está en producción.");
        }

        // Obtener producto
        Producto producto = productosDao.findByCodigo(orden.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Asignar stock producido real (si no viene)
        if (stockProducidoReal == null) {
            stockProducidoReal = orden.getStockRequerido();
        }
        orden.setStockProducidoReal(stockProducidoReal);

        // Asignar lote del producto
        orden.setLote(producto.getLote());

        // Impactar insumos si aún no se hizo
        if (Boolean.FALSE.equals(orden.getImpactado())) {

            // 1) Egreso directo del producto generado (tu lógica actual)
            movimientoProductoService.egresoAutomatico(
                    orden.getCodigoProducto(),
                    stockProducidoReal,
                    destino
            );

            // 2) Obtener receta por ID del producto
            List<InsumoPorProducto> receta =
                    insumoPorProductoDao.findByProductoId(producto.getId());

            if (receta == null || receta.isEmpty()) {
                log.warn("⚠ El producto {} no tiene insumos asociados. No se descontó receta.",
                        producto.getCodigo());

            } else {
                // 3) Restar insumos según receta
                for (InsumoPorProducto r : receta) {

                    BigDecimal cantidadARestar =
                            r.getStockNecesarioInsumo().multiply(stockProducidoReal);

                    restarStockInsumo(
                            r.getInsumo().getCodigo(),
                            cantidadARestar
                    );

                    log.info("Insumo {} descontado: {} {}",
                            r.getInsumo().getCodigo(),
                            cantidadARestar,
                            r.getInsumo().getUnidad());
                }
            }

            orden.setImpactado(true);
        }

        // 4) Sumar el stock producido al producto final
        if (producto.getStock() == null) {
            producto.setStock(BigDecimal.ZERO);
        }

        producto.setStock(
                producto.getStock().add(stockProducidoReal)
        );

        productosDao.save(producto);

        // 5) Cambiar estado
        orden.setEstado("FINALIZADA_ENTREGADA");

        registrarHistorial(orden, "FINALIZADA");
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
        historial.setUsuario(randomUsername());
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

    private void registrarHistorial(OrdenProduccion orden, String estado) {
        HistorialEtapa historial = new HistorialEtapa();
        historial.setOrden(orden);
        historial.setEtapa(estado);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setUsuario(randomUsername());
        historialEtapaDao.save(historial);
    }
}
