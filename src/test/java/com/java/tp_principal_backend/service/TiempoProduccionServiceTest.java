package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.dto.TiempoProduccionRequest;
import com.java.tp_principal_backend.dto.TiempoProduccionResponse;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.impl.TiempoProduccionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TiempoProduccionServiceTest {
	
	 @InjectMocks
	    private TiempoProduccionServiceImpl service;

	    @Mock
	    private TiempoProduccionDao tiempoDao;

	    @Mock
	    private ProductosDao productosDao;

	    private TiempoProduccion tiempo;
	    private Producto producto;

	    @BeforeEach
	    void setup() {
	        producto = new Producto();
	        producto.setCodigo("P001");

	        tiempo = new TiempoProduccion();
	        tiempo.setProducto(producto);
	        tiempo.setTiempoCiclo(BigDecimal.valueOf(5));
	        tiempo.setTiempoPreparacion(BigDecimal.valueOf(10));
	        tiempo.setCantidaTanda(BigDecimal.valueOf(20));
	    }

	    @Test
	    void agregar_ok() {
	        TiempoProduccionRequest req = new TiempoProduccionRequest();
	        req.setCodigoProducto("P001");
	        req.setTiempoCiclo(BigDecimal.valueOf(6));
	        req.setTiempoPreparacion(BigDecimal.valueOf(4));
	        req.setMaximoTanda(BigDecimal.valueOf(50));

	        when(tiempoDao.findByProductoCodigo("P001")).thenReturn(Optional.of(tiempo));
	        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
	        when(tiempoDao.save(any())).thenReturn(tiempo);

	        TiempoProduccion result = service.agregar(req);

	        assertEquals(BigDecimal.valueOf(6), result.getTiempoCiclo());
	        assertEquals(BigDecimal.valueOf(4), result.getTiempoPreparacion());
	        assertEquals(BigDecimal.valueOf(50), result.getCantidaTanda());
	    }

	    @Test
	    void agregar_productoNoExiste() {
	        TiempoProduccionRequest req = new TiempoProduccionRequest();
	        req.setCodigoProducto("X001");

	        when(tiempoDao.findByProductoCodigo("X001")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () -> service.agregar(req));
	    }

	    @Test
	    void obtenerTodos() {
	        when(tiempoDao.findAll()).thenReturn(List.of(tiempo));

	        List<TiempoProduccion> result = service.obtenerTodos();

	        assertEquals(1, result.size());
	    }

	    @Test
	    void calcularTiempoTotal_casoMenorATanda() {
	        when(tiempoDao.findByProductoCodigo("P001")).thenReturn(Optional.of(tiempo));

	        Map<String, BigDecimal> result = service.calcularTiempoTotal("P001", BigDecimal.valueOf(10));

	        BigDecimal esperado = BigDecimal.valueOf(10)
	                .add(BigDecimal.ONE.multiply(BigDecimal.valueOf(20 * 5)))
	                .setScale(0, RoundingMode.HALF_UP);

	        assertEquals(esperado, result.get("tiempoEstimado"));
	    }

	    @Test
	    void calcularTiempoTotal_casoMayorATanda() {
	        when(tiempoDao.findByProductoCodigo("P001")).thenReturn(Optional.of(tiempo));

	        Map<String, BigDecimal> result = service.calcularTiempoTotal("P001", BigDecimal.valueOf(40));

	        BigDecimal tandas = BigDecimal.valueOf(40).divide(BigDecimal.valueOf(20));
	        BigDecimal esperado = BigDecimal.valueOf(10)
	                .add(tandas.multiply(BigDecimal.valueOf(20 * 5)))
	                .setScale(0, RoundingMode.HALF_UP);

	        assertEquals(esperado, result.get("tiempoEstimado"));
	    }

	    @Test
	    void obtenerTiempoPorProducto_ok() {
	        when(tiempoDao.findByProductoCodigo("P001")).thenReturn(Optional.of(tiempo));

	        TiempoProduccionResponse result = service.obtenerTiempoPorProducto("P001");

	        assertEquals(tiempo.getCantidaTanda(), result.getCantidadMaximaTanda());
	        assertEquals(tiempo.getTiempoCiclo(), result.getTiempoCiclo());
	        assertEquals(tiempo.getTiempoPreparacion(), result.getTiempoPreparacion());
	        assertEquals(tiempo.getTiempoCiclo().add(tiempo.getTiempoPreparacion()), result.getTiempoTotal());
	    }

	    @Test
	    void obtenerTiempoPorProducto_noExiste() {
	        when(tiempoDao.findByProductoCodigo("X001")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () -> service.obtenerTiempoPorProducto("X001"));
	    }
    /*@Mock
    private TiempoProduccionDao tiempoDao;

    @Mock
    private ProductosDao productosDao;

    @InjectMocks
    private TiempoProduccionServiceImpl tiempoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAgregar_Success() {
        Producto producto = new Producto();
        producto.setCodigo("P1");

        TiempoProduccionRequest tiempo = new TiempoProduccionRequest();
        tiempo.setCodigoProducto("P001");
        tiempo.setTiempoCiclo(BigDecimal.valueOf(2));

        when(productosDao.findByCodigo("P1")).thenReturn(Optional.of(producto));
        when(tiempoDao.findByProductoCodigo("P1")).thenReturn(Optional.empty());
        when(tiempoDao.save(any(TiempoProduccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TiempoProduccion result = tiempoService.agregar(tiempo);

        assertNotNull(result);
    }

    @Disabled
    @Test
    void testAgregar_Fail_AlreadyExists() {
        Producto producto = new Producto();
        producto.setCodigo("P1");

        TiempoProduccionRequest tiempo = new TiempoProduccionRequest();
        tiempo.setCodigoProducto("P001");
        tiempo.setTiempoCiclo(BigDecimal.valueOf(2));

        when(productosDao.findByCodigo("P1")).thenReturn(Optional.of(producto));
        when(tiempoDao.findByProductoCodigo("P1")).thenReturn(Optional.of(new TiempoProduccion()));

        assertThrows(IllegalArgumentException.class, () -> tiempoService.agregar(tiempo));
    }

    @Test
    void testObtenerTodos() {
        TiempoProduccion t1 = new TiempoProduccion();
        TiempoProduccion t2 = new TiempoProduccion();

        when(tiempoDao.findAll()).thenReturn(List.of(t1, t2));

        List<TiempoProduccion> result = tiempoService.obtenerTodos();

        assertEquals(2, result.size());
    }

    @Test
    void testCalcularTiempoTotal_Success() {
        Producto producto = new Producto();
        producto.setCodigo("P1");

        TiempoProduccion tiempo = new TiempoProduccion();
        tiempo.setProducto(producto);
        tiempo.setTiempoCiclo(BigDecimal.valueOf(3));

        when(tiempoDao.findByProductoCodigo("P1")).thenReturn(Optional.of(tiempo));

        Map<String, BigDecimal> total = tiempoService.calcularTiempoTotal("P1", BigDecimal.valueOf(5));

        assertNotNull(total);
    }

    @Test
    void testCalcularTiempoTotal_Fail_NoTiempo() {
        when(tiempoDao.findByProductoCodigo("P1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                tiempoService.calcularTiempoTotal("P1", BigDecimal.valueOf(5)));
    }*/
}
