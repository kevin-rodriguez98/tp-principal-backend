package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.OrdenProduccionDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.OrdenProduccionRequest;
import com.java.tp_principal_backend.model.MovimientoProducto;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.impl.MovimientoProductoServiceImpl;
import com.java.tp_principal_backend.services.impl.OrdenProduccionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
public class OrdenProduccionServiceTest {
    @Mock
    private OrdenProduccionDao ordenDao;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private MovimientoProductoServiceImpl movimientoProductoService;

    @InjectMocks
    private OrdenProduccionServiceImpl ordenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Disabled
    @Test
    void testAgregarOrden() {
        OrdenProduccionRequest request = new OrdenProduccionRequest();
        request.setProductoRequerido("Producto1");
        request.setMarca("Marca1");
        request.setStockRequerido(BigDecimal.valueOf(10));
        request.setFechaEntrega(LocalDate.now().plusDays(5));

        OrdenProduccion savedOrden = new OrdenProduccion();
        savedOrden.setProductoRequerido(request.getProductoRequerido());
        savedOrden.setMarca(request.getMarca());
        savedOrden.setStockRequerido(request.getStockRequerido());
        savedOrden.setFechaEntrega(request.getFechaEntrega());
        savedOrden.setEstado("Evaluación");

        when(ordenDao.save(any(OrdenProduccion.class))).thenReturn(savedOrden);

        /*OrdenProduccion result = ordenService.agregarOrden(request);

        assertNotNull(result);
        assertEquals("Evaluación", result.getEstado());
        assertEquals("Producto1", result.getProductoRequerido());*/
    }

    @Test
    void testObtenerTodas() {
        List<OrdenProduccion> ordenes = new ArrayList<>();
        ordenes.add(new OrdenProduccion());
        ordenes.add(new OrdenProduccion());

        when(ordenDao.findAll()).thenReturn(ordenes);

        List<OrdenProduccion> result = ordenService.obtenerTodas();

        assertEquals(2, result.size());
    }

    @Test
    void testMarcarEnProduccion_Success() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setStockRequerido(BigDecimal.valueOf(5));
        orden.setEstado("EVALUACION");

        Producto producto = new Producto();
        producto.setCodigo("P1");
        producto.setLote("Lote123");

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(productosDao.findByCodigo("P1")).thenReturn(Optional.of(producto));
        when(movimientoProductoService.restarInsumos(producto, orden.getStockRequerido())).thenReturn(true);
        when(ordenDao.save(any(OrdenProduccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenProduccion result = ordenService.marcarEnProduccion(1, "P1");

        assertEquals("EN_PRODUCCION", result.getEstado());
        assertEquals("P1", result.getCodigoProducto());
        assertTrue(result.getImpactado());
    }

    @Test
    void testMarcarCancelada_Success() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("EVALUACION");

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(ordenDao.save(any(OrdenProduccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenProduccion result = ordenService.marcarCancelada(1);

        assertEquals("CANCELADA", result.getEstado());
    }

    @Test
    void testMarcarCancelada_Fail() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("EN_PRODUCCION");

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));

        assertThrows(IllegalStateException.class, () -> ordenService.marcarCancelada(1));
    }

    @Test
    void testMarcarFinalizada_Success_WithStockReal_Y_Egreso() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("EN_PRODUCCION");
        orden.setCodigoProducto("P1");
        orden.setStockRequerido(BigDecimal.valueOf(10));

        Producto producto = new Producto();
        producto.setCodigo("P1");
        producto.setLote("LoteABC");

        String destino = "Cliente1";

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(productosDao.findByCodigo("P1")).thenReturn(Optional.of(producto));
        when(ordenDao.save(any(OrdenProduccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Se espera que se llame al egreso automático
        doReturn(new MovimientoProducto()).when(movimientoProductoService).egresoAutomatico("P1", BigDecimal.valueOf(10), destino);

        OrdenProduccion result = ordenService.marcarFinalizada(1, BigDecimal.valueOf(12), destino);

        assertEquals("FINALIZADA_ENTREGADA", result.getEstado());
        assertEquals("LoteABC", result.getLote());
        assertEquals(BigDecimal.valueOf(12), result.getStockProducidoReal());

        // Verifica que se llamó al egreso automático
        verify(movimientoProductoService, times(1)).egresoAutomatico("P1", BigDecimal.valueOf(12), destino);
    }

    @Test
    void testMarcarFinalizada_Success_WithoutStockReal_Y_Egreso() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("EN_PRODUCCION");
        orden.setCodigoProducto("P1");
        orden.setStockRequerido(BigDecimal.valueOf(10));

        Producto producto = new Producto();
        producto.setCodigo("P1");
        producto.setLote("LoteABC");

        String destino = "Cliente1";

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(productosDao.findByCodigo("P1")).thenReturn(Optional.of(producto));
        when(ordenDao.save(any(OrdenProduccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doReturn(new MovimientoProducto()).when(movimientoProductoService).egresoAutomatico("P1", BigDecimal.valueOf(10), destino);

        OrdenProduccion result = ordenService.marcarFinalizada(1, null, destino);

        assertEquals("FINALIZADA_ENTREGADA", result.getEstado());
        assertEquals("LoteABC", result.getLote());
        assertEquals(BigDecimal.valueOf(10), result.getStockProducidoReal());

        verify(movimientoProductoService, times(1)).egresoAutomatico("P1", BigDecimal.valueOf(10), destino);
    }

    @Test
    void testMarcarFinalizada_Fail_NotEnProduccion() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("EVALUACION");

        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));

        assertThrows(IllegalStateException.class, () -> ordenService.marcarFinalizada(1, BigDecimal.valueOf(5), "Cliente1"));

        // Se asegura de que no se llamó al egreso automático
        verify(movimientoProductoService, never()).egresoAutomatico(anyString(), any(), anyString());
    }
}
