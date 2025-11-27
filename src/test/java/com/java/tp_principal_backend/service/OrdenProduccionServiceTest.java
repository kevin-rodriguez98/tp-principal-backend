package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.HistorialEtapaDao;
import com.java.tp_principal_backend.data.OrdenProduccionDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.EtapaRequest;
import com.java.tp_principal_backend.dto.HistorialEtapasResponse;
import com.java.tp_principal_backend.dto.OrdenFinalizadaRequest;
import com.java.tp_principal_backend.dto.OrdenProduccionNormalRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.HistorialEtapa;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.TiempoProduccionService;
import com.java.tp_principal_backend.services.impl.MovimientoProductoServiceImpl;
import com.java.tp_principal_backend.services.impl.OrdenProduccionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class OrdenProduccionServiceTest {
	
    @InjectMocks
    private OrdenProduccionServiceImpl service;

    @Mock
    private OrdenProduccionDao ordenDao;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private HistorialEtapaDao historialEtapaDao;

    @Mock
    private EmpleadosDao empleadosDao;

    @Mock
    private TiempoProduccionService tiempoProduccionService;

    @Mock
    private MovimientoProductoServiceImpl movimientoProductoService;

    private Empleados empleado;
    private OrdenProduccion orden;
    private Producto producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empleado = new Empleados();
        empleado.setNombre("EmpleadoTest");

        producto = new Producto();
        producto.setCodigo("P001");
        producto.setStock(BigDecimal.ZERO);

        orden = new OrdenProduccion();
        orden.setId(1);
        orden.setCodigoProducto("P001");
        orden.setStockRequerido(BigDecimal.TEN);
        orden.setEstado("Evaluación");
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setLote("L001");
    }

    // ------------------------------------------------------------
    // TEST obtenerTodas()
    // ------------------------------------------------------------
    @Test
    void testObtenerTodas() {
        when(ordenDao.findAll()).thenReturn(List.of(orden));

        List<OrdenProduccion> result = service.obtenerTodas();
        assertEquals(1, result.size());
    }

    // ------------------------------------------------------------
    // TEST marcarFinalizada()
    // ------------------------------------------------------------
    @Test
    void testMarcarFinalizada() {
        OrdenFinalizadaRequest req = new OrdenFinalizadaRequest();
        req.setOrdenId(1);
        req.setStockProducidoReal(BigDecimal.TEN);
        req.setLegajo("100");
        req.setDestino("DEP01");

        when(empleadosDao.buscarPorLegajo("100")).thenReturn(empleado);
        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(ordenDao.save(any())).thenReturn(orden);

        OrdenProduccion r = service.marcarFinalizada(req);

        assertEquals("FINALIZADA", r.getEstado());
        assertEquals(BigDecimal.TEN, producto.getStock());
        verify(historialEtapaDao, times(1)).save(any());
        verify(movimientoProductoService, times(1))
                .egresoAutomatico(any(), any(), any(), any(),any());
    }

    // ------------------------------------------------------------
    // TEST agregarOrdenNormal()
    // ------------------------------------------------------------
    @Test
    void testAgregarOrdenNormal() {
        OrdenProduccionNormalRequest req = new OrdenProduccionNormalRequest();
        req.setCodigoProducto("P001");
        req.setStockRequerido(BigDecimal.TEN);
        req.setLegajo("100");
        req.setEstado("Evaluación");

        Map<String, BigDecimal> mapa = new HashMap<>();
        mapa.put("tiempoEstimado", BigDecimal.valueOf(50));
        when(tiempoProduccionService.calcularTiempoTotal("P001", BigDecimal.TEN)).thenReturn(mapa);
        when(empleadosDao.buscarPorLegajo("100")).thenReturn(empleado);
        when(ordenDao.save(any())).thenReturn(orden);

        OrdenProduccion r = service.agregarOrdenNormal(req);

        assertEquals(BigDecimal.valueOf(50), r.getTiempoProduccion());
        verify(historialEtapaDao, times(1)).save(any());
    }

    // ------------------------------------------------------------
    // TEST actualizarEtapa()
    // ------------------------------------------------------------
    @Test
    void testActualizarEtapa() {
        EtapaRequest req = new EtapaRequest();
        req.setLegajo("100");
        req.setEstado("EN_PRODUCCION");
        req.setIdOrden(1);
        req.setIsEstado(true);

        when(empleadosDao.buscarPorLegajo("100")).thenReturn(empleado);
        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(movimientoProductoService.restarInsumos(any(), any())).thenReturn(true);
        when(ordenDao.save(any())).thenReturn(orden);

        OrdenProduccion r = service.actualizarEtapa(req);

        assertEquals("EN_PRODUCCION", r.getEstado());
        verify(historialEtapaDao, times(1)).save(any());
    }

    // ------------------------------------------------------------
    // TEST agregarNota()
    // ------------------------------------------------------------
    @Test
    void testAgregarNota() {
        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        when(ordenDao.save(any())).thenReturn(orden);

        OrdenProduccion r = service.agregarNota(1, "nota test");

        assertEquals("nota test", r.getNota());
    }

    // ------------------------------------------------------------
    // TEST obtenerHistorialPorOrden()
    // ------------------------------------------------------------
    @Test
    void testObtenerHistorialPorOrden() {
        HistorialEtapa h = new HistorialEtapa();
        h.setEtapa("CREADA");
        h.setFechaCambio(LocalDateTime.now());
        h.setEmpleado(empleado);

        when(historialEtapaDao.findByOrdenIdOrderByFechaCambioAsc(1))
                .thenReturn(List.of(h));

        List<HistorialEtapasResponse> r = service.obtenerHistorialPorOrden(1);

        assertEquals(1, r.size());
        assertEquals("CREADA", r.get(0).getEtapa());
    }

    // ------------------------------------------------------------
    // TEST obtenerOrdenesUltimosDIas()
    // ------------------------------------------------------------
    @Test
    void testObtenerOrdenesUltimosdias() {
        when(ordenDao.findByFechaCreacionGreaterThanEqual(any()))
                .thenReturn(List.of(orden));

        List<OrdenProduccion> r = service.obtenerOrdenesUltimosdias(10);

        assertEquals(1, r.size());
    }

    // ------------------------------------------------------------
    // TEST obtenerOrdenesFecha()
    // ------------------------------------------------------------
    @Test
    void testObtenerOrdenesFecha() {
        LocalDate fecha = LocalDate.of(2025, 1, 10);
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

        when(ordenDao.findByFechaCreacionBetween(desde, hasta))
                .thenReturn(List.of(orden));

        List<OrdenProduccion> r = service.obtenerOrdenesFecha(fecha);
        assertEquals(1, r.size());
    }

    /*@Mock
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

        OrdenProduccion result = ordenService.agregarOrden(request);

        assertNotNull(result);
        assertEquals("Evaluación", result.getEstado());
        assertEquals("Producto1", result.getProductoRequerido());
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
        EtapaRequest etapa = new EtapaRequest();
        etapa.setEstado("EN_PRODUCCION");
        etapa.setIdOrden(1);
        etapa.setLegajo("100");
        OrdenProduccion result = ordenService.actualizarEtapa(etapa);

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
        EtapaRequest etapa = new EtapaRequest();
        etapa.setEstado("EVALUACION");
        etapa.setIdOrden(1);
        etapa.setLegajo("100");
        OrdenProduccion result = ordenService.actualizarEtapa(etapa);

        assertEquals("CANCELADA", result.getEstado());
    }

    @Test
    void testMarcarCancelada_Fail() {
        OrdenProduccion orden = new OrdenProduccion();
        orden.setId(1);
        orden.setEstado("CANCELDA");
        EtapaRequest etapa = new EtapaRequest();
        etapa.setEstado("CANCELDA");
        etapa.setIdOrden(1);
        etapa.setLegajo("100");
        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));

        assertThrows(IllegalStateException.class, () -> ordenService.actualizarEtapa(etapa));
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
        OrdenFinalizadaRequest ordenFinalizada = new OrdenFinalizadaRequest();
        ordenFinalizada.setDestino(destino);
        ordenFinalizada.setLegajo("100");
        ordenFinalizada.setOrdenId(12);
        ordenFinalizada.setStockProducidoReal(BigDecimal.valueOf(12));
        OrdenProduccion result = ordenService.marcarFinalizada(ordenFinalizada);

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
        OrdenFinalizadaRequest ordenFinalizada = new OrdenFinalizadaRequest();
        ordenFinalizada.setDestino(destino);
        ordenFinalizada.setLegajo("100");
        ordenFinalizada.setOrdenId(12);
        ordenFinalizada.setStockProducidoReal(BigDecimal.valueOf(12));
        OrdenProduccion result = ordenService.marcarFinalizada(ordenFinalizada);

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
        String destino = "Cliente1";
        when(ordenDao.findById(1)).thenReturn(Optional.of(orden));
        OrdenFinalizadaRequest ordenFinalizada = new OrdenFinalizadaRequest();
        ordenFinalizada.setDestino(destino);
        ordenFinalizada.setLegajo("100");
        ordenFinalizada.setOrdenId(12);
        ordenFinalizada.setStockProducidoReal(BigDecimal.valueOf(12));
        assertThrows(IllegalStateException.class, () -> ordenService.marcarFinalizada(ordenFinalizada));

        // Se asegura de que no se llamó al egreso automático
        verify(movimientoProductoService, never()).egresoAutomatico(anyString(), any(), anyString());
    }*/
}
