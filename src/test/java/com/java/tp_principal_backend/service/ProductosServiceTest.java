package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.dto.ProductosResponse;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.impl.ProductosServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductosServiceTest {
	
	@InjectMocks
    private ProductosServicesImpl service;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private TiempoProduccionDao tiempoProduccionDao;

    @Mock
    private EmpleadosDao empleadosDao;

    private Producto producto;

    @BeforeEach
    void setup() {
        producto = new Producto();
        producto.setCodigo("P001");
        producto.setNombre("Dulce de leche");
        producto.setCategoria("Lácteos");
        producto.setLinea("Premium");
        producto.setUnidad("kg");
        producto.setEmpleados("100");
        producto.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void obtenerTodosLosProductos() {
        Empleados emp = new Empleados();
        emp.setNombre("Carlos");

        when(productosDao.findAll()).thenReturn(List.of(producto));
        when(empleadosDao.buscarPorLegajo("100")).thenReturn(emp);

        List<ProductosResponse> result = service.obtenerTodosLosProductos();

        assertEquals(1, result.size());
        assertEquals("Dulce de leche", result.get(0).getProducto().getNombre());
    }

    @Test
    void agregarProducto_ok() {
        ProductoRequest req = new ProductoRequest();
        req.setCodigo("P001");
        req.setNombre("Dulce de leche");
        req.setCategoria("Lácteos");
        req.setLinea("Premium");
        req.setUnidad("kg");
        req.setLegajoResponsable("100");
        req.setPresentacion("500g");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.empty());
        when(productosDao.save(any(Producto.class))).thenReturn(producto);

        Producto result = service.agregarProducto(req);

        assertEquals("P001", result.getCodigo());
        verify(tiempoProduccionDao, times(1)).save(any(TiempoProduccion.class));
    }

    @Test
    void agregarProducto_codigoDuplicado() {
        ProductoRequest req = new ProductoRequest();
        req.setCodigo("P001");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.agregarProducto(req)
        );

        assertTrue(ex.getMessage().contains("Ya existe un producto"));
    }

    @Test
    void editarProducto_ok() {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", "Nuevo nombre");
        cambios.put("stock", "50");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(productosDao.save(any())).thenReturn(producto);

        Producto result = service.editarProducto("P001", cambios);

        assertEquals("Nuevo nombre", result.getNombre());
        assertEquals(new BigDecimal("50"), result.getStock());
    }

    @Test
    void editarProducto_cambiarCodigoDuplicado() {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("codigo", "P999");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(productosDao.findByCodigo("P999")).thenReturn(Optional.of(new Producto()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.editarProducto("P001", cambios)
        );

        assertTrue(ex.getMessage().contains("Ya existe un producto"));
    }

    @Test
    void eliminarProducto_ok() {
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));

        service.eliminarProducto("P001");

        verify(productosDao, times(1)).delete(producto);
    }

    @Test
    void eliminarProducto_noExiste() {
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.eliminarProducto("P001")
        );
    }
}
