package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.InsumoPorProductoDao;
import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.MovimientoProductoDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.MovimientoProductoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.InsumoPorProducto;
import com.java.tp_principal_backend.model.MovimientoProducto;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.impl.MovimientoProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MovimientoProductoServiceTest {

    @Mock
    private MovimientoProductoDao movimientoDao;

    @Mock
    private ProductosDao productosDao;

    @Mock
    private InsumosDao insumosDao;

    @Mock
    private InsumoPorProductoDao recetaDao;

    @InjectMocks
    private MovimientoProductoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void agregarMovimiento_egresoConStockSuficiente_impactadoTrue() {
        // 1️⃣ Datos de entrada
        MovimientoProductoRequest request = new MovimientoProductoRequest();
        request.setCodigoProducto("P001");
        request.setCantidad(new BigDecimal("2"));
        request.setTipo("egreso");
        request.setDestino("Sucursal A");

        Producto producto = new Producto();
        producto.setId(1);
        producto.setCodigo("P001");

        Insumo insumo1 = new Insumo();
        insumo1.setId(10);
        insumo1.setCodigo("I001");
        insumo1.setStock(new BigDecimal("10")); // stock suficiente

        InsumoPorProducto receta = new InsumoPorProducto();
        receta.setProducto(producto);
        receta.setInsumo(insumo1);
        receta.setStockNecesarioInsumo(new BigDecimal("2")); // necesita 2 por producto

        // 2️⃣ Mockeos
        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(recetaDao.findByProductoId(1)).thenReturn(List.of(receta));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoDao.save(any(MovimientoProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 3️⃣ Ejecutar método
        MovimientoProducto resultado = service.agregarMovimiento(request);

        // 4️⃣ Verificaciones
        assertTrue(resultado.getImpactado(), "El movimiento debería estar impactado");
        assertEquals(new BigDecimal("6"), insumo1.getStock(), "El stock del insumo debe haberse reducido correctamente");

        // Verificar que se llamó a save
        verify(insumosDao, times(1)).save(insumo1);
        verify(movimientoDao, times(1)).save(resultado);
    }

    @Test
    void agregarMovimiento_egresoConStockInsuficiente_impactadoFalse() {
        MovimientoProductoRequest request = new MovimientoProductoRequest();
        request.setCodigoProducto("P001");
        request.setCantidad(new BigDecimal("3")); // solicita 3 productos
        request.setTipo("egreso");
        request.setDestino("Sucursal A");

        Producto producto = new Producto();
        producto.setId(1);
        producto.setCodigo("P001");

        Insumo insumo1 = new Insumo();
        insumo1.setId(10);
        insumo1.setCodigo("I001");
        insumo1.setStock(new BigDecimal("5")); // insuficiente para 3*2=6

        InsumoPorProducto receta = new InsumoPorProducto();
        receta.setProducto(producto);
        receta.setInsumo(insumo1);
        receta.setStockNecesarioInsumo(new BigDecimal("2")); // necesita 2 por producto

        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
        when(recetaDao.findByProductoId(1)).thenReturn(List.of(receta));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoDao.save(any(MovimientoProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoProducto resultado = service.agregarMovimiento(request);

        assertFalse(resultado.getImpactado(), "El movimiento no debería estar impactado por stock insuficiente");
        assertEquals(new BigDecimal("5"), insumo1.getStock(), "El stock no debe cambiar cuando es insuficiente");
    }
}
