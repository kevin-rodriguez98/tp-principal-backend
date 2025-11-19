package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.dto.TiempoProduccionRequest;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.impl.TiempoProduccionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Disabled
public class TiempoProduccionServiceTest {
    @Mock
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
    }
}
