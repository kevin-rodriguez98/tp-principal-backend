package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.ProductoRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.TiempoProduccionService;
import com.java.tp_principal_backend.services.impl.ProductosServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductosServiceTest {

    @Mock
    private ProductosDao productosDao;
    
    @Mock
    private TiempoProduccionService timpoProduccionSercvice; 
    
    @Mock
    private EmpleadosDao empleadosDao;

    @InjectMocks
    private ProductosServicesImpl productosServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 🔹 Test obtenerTodosLosProductos()
    @Test
    void obtenerTodosLosProductos_debeRetornarListaDeProductos() {
        List<Producto> productos = List.of(new Producto(), new Producto());
        when(productosDao.findAll()).thenReturn(productos);

        List<Producto> resultado = productosServices.obtenerTodosLosProductos();

        assertEquals(2, resultado.size());
        verify(productosDao, times(1)).findAll();
    }

    // 🔹 Test agregarProducto() exitoso
    @Test
    void agregarProducto_debeGuardarProductoSiNoExisteCodigo() {
        ProductoRequest request = new ProductoRequest();
        request.setCodigo("P001");
        request.setNombre("Leche");
        request.setCategoria("Lácteos");
        request.setLinea("Entero");
        request.setUnidad("Litro");
  
        when(empleadosDao.buscarPorLegajo(any())).thenReturn(new Empleados());
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.empty());
        when(timpoProduccionSercvice.agregar(any())).thenReturn(new TiempoProduccion());
        when(productosDao.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productosServices.agregarProducto(request);

        assertEquals("P001", resultado.getCodigo());
        assertEquals("Leche", resultado.getNombre());
        assertNotNull(resultado.getCreationUsername());
        verify(productosDao).save(any(Producto.class));
    }

    // 🔹 Test agregarProducto() cuando ya existe el código
    @Test
    void agregarProducto_debeLanzarExcepcionSiCodigoYaExiste() {
        ProductoRequest request = new ProductoRequest();
        request.setCodigo("P001");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(new Producto()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productosServices.agregarProducto(request));

        assertEquals("Ya existe un producto con el código: P001", exception.getMessage());
        verify(productosDao, never()).save(any());
    }

    // 🔹 Test editarProducto() exitoso
    @Test
    void editarProducto_debeActualizarCamposYGuardar() {
        Producto productoExistente = new Producto();
        productoExistente.setCodigo("P001");
        productoExistente.setNombre("Leche");

        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", "Leche Descremada");
        cambios.put("stock", BigDecimal.valueOf(50));

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(productoExistente));
        when(productosDao.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productosServices.editarProducto("P001", cambios);

        assertEquals("Leche Descremada", resultado.getNombre());
        assertEquals(BigDecimal.valueOf(50), resultado.getStock());
        verify(productosDao).save(productoExistente);
    }

    // 🔹 Test editarProducto() con código duplicado
    @Test
    void editarProducto_debeLanzarExcepcionSiNuevoCodigoYaExiste() {
        Producto productoExistente = new Producto();
        productoExistente.setCodigo("P001");

        Map<String, Object> cambios = Map.of("codigo", "P002");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(productoExistente));
        when(productosDao.findByCodigo("P002")).thenReturn(Optional.of(new Producto()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productosServices.editarProducto("P001", cambios));

        assertEquals("Ya existe un producto con el código: P002", ex.getMessage());
        verify(productosDao, never()).save(any());
    }

    // 🔹 Test eliminarProducto() exitoso
    @Test
    void eliminarProducto_debeEliminarProductoExistente() {
        Producto producto = new Producto();
        producto.setCodigo("P001");

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));

        productosServices.eliminarProducto("P001");

        verify(productosDao).delete(producto);
    }

    // 🔹 Test eliminarProducto() cuando no existe
    @Test
    void eliminarProducto_debeLanzarExcepcionSiNoExiste() {
        when(productosDao.findByCodigo("P999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productosServices.eliminarProducto("P999"));

        assertEquals("No existe producto con el código: P999", ex.getMessage());
        verify(productosDao, never()).delete(any());
    }
}
