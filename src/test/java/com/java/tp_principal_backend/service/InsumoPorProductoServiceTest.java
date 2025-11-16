package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.dto.InsumoPorProductoRequest;
import com.java.tp_principal_backend.model.Producto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.InsumoPorProducto;
import com.java.tp_principal_backend.data.InsumoPorProductoDao;
import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.services.impl.InsumoPorProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Disabled
public class InsumoPorProductoServiceTest {

    @Mock
    private InsumoPorProductoDao recetaRepo;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private InsumosDao insumosDao;

    @InjectMocks
    private InsumoPorProductoServiceImpl recetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 🔹 Test exitoso
    @Test
    void agregarReceta_debeGuardarRecetaCorrectamente() {
        // Datos de prueba
        InsumoPorProductoRequest request = new InsumoPorProductoRequest();
        request.setCodigoProducto("P001");
        request.getInsumo().setCodigoInsumo("I002");
        request.getInsumo().setCantidadNecesaria(BigDecimal.valueOf(0.5));

        Producto producto = new Producto();
        producto.setId(1);
        producto.setCodigo("P001");

        Insumo insumo = new Insumo();
        insumo.setId(2);
        insumo.setCodigo("I002");

        // Mockear búsqueda de producto e insumo
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(insumosDao.findByCodigo("I002")).thenReturn(Optional.of(insumo));

        // Mockear guardado en la DB
        when(recetaRepo.save(any(InsumoPorProducto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método
        InsumoPorProducto resultado = recetaService.agregarReceta(request);

        // Verificaciones
        assertEquals(producto, resultado.getProducto());
        assertEquals(insumo, resultado.getInsumo());
        assertEquals(BigDecimal.valueOf(0.5), resultado.getStockNecesarioInsumo());

        verify(productosDao, times(1)).findByCodigo("P001");
        verify(insumosDao, times(1)).findByCodigo("I002");
        verify(recetaRepo, times(1)).save(any(InsumoPorProducto.class));
    }

    // 🔹 Test producto no encontrado
    @Test
    void agregarReceta_debeLanzarExcepcionSiProductoNoExiste() {
        InsumoPorProductoRequest request = new InsumoPorProductoRequest();
        request.setCodigoProducto("P999");
        request.getInsumo().setCodigoInsumo("I001");
        request.getInsumo().setCantidadNecesaria(BigDecimal.ONE);

        when(productosDao.findByCodigo("P999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recetaService.agregarReceta(request));

        assertEquals("Producto no encontrado", ex.getMessage());
        verify(recetaRepo, never()).save(any());
    }

    // 🔹 Test insumo no encontrado
    @Test
    void agregarReceta_debeLanzarExcepcionSiInsumoNoExiste() {
        InsumoPorProductoRequest request = new InsumoPorProductoRequest();
        request.setCodigoProducto("P001");
        request.getInsumo().setCodigoInsumo("I999");
        request.getInsumo().setCantidadNecesaria(BigDecimal.ONE);

        Producto producto = new Producto();
        producto.setCodigo("P001");
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(insumosDao.findByCodigo("I999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recetaService.agregarReceta(request));

        assertEquals("Insumo no encontrado", ex.getMessage());
        verify(recetaRepo, never()).save(any());
    }

    @Disabled
    @Test
    void calcularInsumosNecesarios_ProductoExistente_RetornaCantidades() {
        // Datos de prueba
        String codigoProducto = "PROD123";
        BigDecimal cantidadProducto = new BigDecimal("2");

        Producto producto = new Producto();
        producto.setId(1);
        producto.setCodigo(codigoProducto);
        producto.setNombre("Producto Test");

        Insumo insumo1 = new Insumo();
        insumo1.setCodigo("INS001");
        insumo1.setNombre("Insumo 1");

        Insumo insumo2 = new Insumo();
        insumo2.setCodigo("INS002");
        insumo2.setNombre("Insumo 2");

        // Stock necesario está en InsumoPorProducto
        InsumoPorProducto receta1 = new InsumoPorProducto();
        receta1.setProducto(producto);
        receta1.setInsumo(insumo1);
        receta1.setStockNecesarioInsumo(new BigDecimal("3"));

        InsumoPorProducto receta2 = new InsumoPorProducto();
        receta2.setProducto(producto);
        receta2.setInsumo(insumo2);
        receta2.setStockNecesarioInsumo(new BigDecimal("7"));

        // Mockear repositorios
        when(productosDao.findByCodigo(codigoProducto)).thenReturn(Optional.of(producto));
        when(recetaRepo.findByProductoId(producto.getId())).thenReturn(List.of(receta1, receta2));

        // Ejecutar método
        var resultados = recetaService.calcularInsumosNecesarios(codigoProducto, cantidadProducto);

        // Verificar resultados
        assertEquals(2, resultados.size());
        assertEquals(new BigDecimal("6"), resultados.get(0).getCantidadNecesaria()); // 3 * 2
        assertEquals(new BigDecimal("14"), resultados.get(1).getCantidadNecesaria()); // 7 * 2

        verify(productosDao, times(1)).findByCodigo(codigoProducto);
        verify(recetaRepo, times(1)).findByProductoId(producto.getId());
    }
}
