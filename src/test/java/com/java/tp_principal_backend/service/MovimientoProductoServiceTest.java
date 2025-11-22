package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.InsumoPorProductoDao;
import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.MovimientoProductoDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.dto.MovimientoProductoResponse;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.InsumoPorProducto;
import com.java.tp_principal_backend.model.MovimientoProducto;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.impl.MovimientoProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimientoProductoServiceTest {
	
	 @InjectMocks
    private MovimientoProductoServiceImpl service;

    @Mock
    private MovimientoProductoDao movimientoDao;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private EmpleadosDao empleadosDao;

    @Mock
    private InsumosDao insumosDao;

    @Mock
    private InsumoPorProductoDao recetaDao;

    private Producto producto;
    private Insumo insumo;
    private InsumoPorProducto receta;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1);
        producto.setCodigo("P001");
        producto.setStock(BigDecimal.valueOf(100));
        producto.setCategoria("DULCE");
        producto.setLinea("PREMIUM");
        producto.setUnidad("kg");
        producto.setNombre("Dulce de leche");
        producto.setFechaCreacion(LocalDateTime.now());

        insumo = new Insumo();
        insumo.setId(10);
        insumo.setNombre("Leche");
        insumo.setStock(BigDecimal.valueOf(200));

        receta = new InsumoPorProducto();
        receta.setId(100);
        receta.setInsumo(insumo);
        receta.setStockNecesarioInsumo(BigDecimal.valueOf(2)); 
    }

    @Test
    void restarInsumos_deberiaDescontarStockCuandoAlcanza() {

        BigDecimal cantidadAProducir = BigDecimal.valueOf(10); 
        when(recetaDao.findByProductoId(producto.getId()))
                .thenReturn(List.of(receta));

        boolean impactado = service.restarInsumos(producto, cantidadAProducir);

        assertTrue(impactado);
        assertEquals(BigDecimal.valueOf(180), insumo.getStock());
        verify(insumosDao, times(1)).save(insumo);
    }

    @Test
    void restarInsumos_deberiaLanzarExcepcionCuandoNoAlcanzaElStock() {
        insumo.setStock(BigDecimal.valueOf(10)); 
        BigDecimal cantidadAProducir = BigDecimal.valueOf(10);

        when(recetaDao.findByProductoId(producto.getId()))
                .thenReturn(List.of(receta));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.restarInsumos(producto, cantidadAProducir)
        );
        assertEquals("insumos insuficientes", ex.getMessage());

        verify(insumosDao, never()).save(any());
    }


    @Test
    void egresoAutomatico_deberiaCrearMovimientoYRestarInsumos() {
        BigDecimal cantidad = BigDecimal.valueOf(5); 

        when(productosDao.findByCodigo("P001"))
                .thenReturn(Optional.of(producto));
        when(recetaDao.findByProductoId(producto.getId()))
                .thenReturn(List.of(receta));

        MovimientoProducto movimientoGuardado = new MovimientoProducto();
        movimientoGuardado.setId(Long.valueOf("100"));
        movimientoGuardado.setCodigoProducto("P001");
        movimientoGuardado.setCantidad(cantidad);
        movimientoGuardado.setTipo("egreso");
        movimientoGuardado.setImpactado(true);
        movimientoGuardado.setDestino("DEP01");
        movimientoGuardado.setLote("L001");

        when(movimientoDao.save(any(MovimientoProducto.class)))
                .thenReturn(movimientoGuardado);

        MovimientoProducto result = service.egresoAutomatico("P001", cantidad, "DEP01", "L001");

        assertEquals("P001", result.getCodigoProducto());
        assertEquals(cantidad, result.getCantidad());
        assertEquals("egreso", result.getTipo());
        assertTrue(result.getImpactado());
        assertEquals("DEP01", result.getDestino());
        assertEquals("L001", result.getLote());

        verify(recetaDao).findByProductoId(producto.getId());
        verify(insumosDao, times(1)).save(insumo);
        verify(movimientoDao, times(1)).save(any(MovimientoProducto.class));
    }

    @Test
    void egresoAutomatico_deberiaLanzarExcepcionSiProductoNoExiste() {
        when(productosDao.findByCodigo("P999"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.egresoAutomatico("P999", BigDecimal.ONE, "DEP", "L001")
        );

        assertEquals("Producto no encontrado", ex.getMessage());
        verify(recetaDao, never()).findByProductoId(any());
        verify(movimientoDao, never()).save(any());
    }


    @Test
    void obtener_deberiaMapearMovimientosAResponse() {
        MovimientoProducto mov = new MovimientoProducto();
        mov.setId(Long.valueOf("100"));
        mov.setCodigoProducto("P001");
        mov.setEmpleado("100");

        Empleados empleado = new Empleados();
        empleado.setLegajo("100");
        empleado.setNombre("Juan");

        when(movimientoDao.findAll()).thenReturn(List.of(mov));
        when(empleadosDao.buscarPorLegajo("100")).thenReturn(empleado);

        List<MovimientoProductoResponse> result = service.obtener();

        assertEquals(1, result.size());
        verify(movimientoDao, times(1)).findAll();
        verify(empleadosDao, times(1)).buscarPorLegajo("100");
    }

    @Test
    void agregarMovimientoNormal_deberiaCrearMovimientoYRestarStockCuandoAlcanza() {
        MovimientoProductoRequest req = new MovimientoProductoRequest();
        req.setCodigoProducto("P001");
        req.setCantidad(BigDecimal.valueOf(10)); 
        req.setTipo("egreso");
        req.setDestino("DEP02");
        req.setCategoria("DULCE");
        req.setMarca("PREMIUM");
        req.setUnidad("kg");
        req.setLote("L002");
        req.setNombre("Dulce de leche");
        req.setLegajo("100");

        when(productosDao.findByCodigo("P001"))
                .thenReturn(Optional.of(producto));

        MovimientoProducto movGuardado = new MovimientoProducto();
        movGuardado.setId(Long.valueOf("100"));
        movGuardado.setCodigoProducto("P001");
        movGuardado.setCantidad(req.getCantidad());
        movGuardado.setTipo(req.getTipo());
        movGuardado.setDestino(req.getDestino());

        when(movimientoDao.save(any(MovimientoProducto.class)))
                .thenReturn(movGuardado);

        MovimientoProducto result = service.agregarMovimientoNormal(req);

        assertEquals(BigDecimal.valueOf(90), producto.getStock());
        assertEquals("P001", result.getCodigoProducto());
        assertEquals(req.getCantidad(), result.getCantidad());
        assertEquals("egreso", result.getTipo());
        assertEquals("DEP02", result.getDestino());

        verify(productosDao, times(1)).save(producto);
        verify(movimientoDao, times(1)).save(any(MovimientoProducto.class));
    }

    @Test
    void agregarMovimientoNormal_deberiaLanzarExcepcionSiNoAlcanzaStock() {
        producto.setStock(BigDecimal.valueOf(50));

        MovimientoProductoRequest req = new MovimientoProductoRequest();
        req.setCodigoProducto("P001");
        req.setCantidad(BigDecimal.valueOf(60));

        when(productosDao.findByCodigo("P001"))
                .thenReturn(Optional.of(producto));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.agregarMovimientoNormal(req)
        );

        assertEquals("No hay sufuciente insumos para egresar", ex.getMessage());
        verify(productosDao, never()).save(any());
        verify(movimientoDao, never()).save(any());
    }
}
