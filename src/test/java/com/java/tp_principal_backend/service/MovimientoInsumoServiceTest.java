package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.MovimientoInsumoDao;
import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.MovimientoInsumo;
import com.java.tp_principal_backend.services.impl.MovimientoInsumoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovimientoInsumoServiceTest {
    @Mock
    private InsumosDao insumosDao;

    @Mock
    private MovimientoInsumoDao movimientoDao;

    @InjectMocks
    private MovimientoInsumoServiceImpl movimientoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === CASO 1: Crear movimiento con insumo inexistente pero con datos completos ===
    @Test
    void agregarMovimiento_creaNuevoInsumoYMovimiento() {
        // Arrange
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A001");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.TEN);
        request.setNombre("Harina");
        request.setCategoria("Alimentos");
        request.setMarca("ACME");
        request.setUnidad("kg");
        request.setLote("L123");

        when(insumosDao.findByCodigo("A001")).thenReturn(Optional.empty());
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertTrue(movimiento.getImpactado());
        assertEquals("A001", movimiento.getCodigo());
        verify(insumosDao, times(1)).save(any(Insumo.class));
        verify(movimientoDao, times(1)).save(any(MovimientoInsumo.class));
    }

    // === CASO 2: Intentar crear insumo sin datos suficientes ===
    @Test
    void agregarMovimiento_faltanDatosInsumoNoCreado() {
        // Arrange
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A002");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.TEN);
        // faltan nombre, marca, unidad, etc.

        when(insumosDao.findByCodigo("A002")).thenReturn(Optional.empty());
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertFalse(movimiento.getImpactado());
        verify(insumosDao, never()).save(any());
        verify(movimientoDao).save(any());
    }

    // === CASO 3: Actualizar insumo existente con ingreso ===
    @Test
    void agregarMovimiento_actualizaStockPorIngreso() {
        // Arrange
        Insumo insumoExistente = new Insumo();
        insumoExistente.setCodigo("A003");
        insumoExistente.setStock(BigDecimal.valueOf(5));

        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A003");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.TEN);

        when(insumosDao.findByCodigo("A003")).thenReturn(Optional.of(insumoExistente));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertTrue(movimiento.getImpactado());
        assertEquals(BigDecimal.valueOf(15), insumoExistente.getStock());
        verify(insumosDao).save(insumoExistente);
        verify(movimientoDao).save(any());
    }

    // === CASO 4: Actualizar insumo existente con egreso ===
    @Test
    void agregarMovimiento_actualizaStockPorEgreso() {
        // Arrange
        Insumo insumoExistente = new Insumo();
        insumoExistente.setCodigo("A004");
        insumoExistente.setStock(BigDecimal.valueOf(20));

        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A004");
        request.setTipo("egreso");
        request.setStock(BigDecimal.valueOf(5));

        when(insumosDao.findByCodigo("A004")).thenReturn(Optional.of(insumoExistente));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertTrue(movimiento.getImpactado());
        assertEquals(BigDecimal.valueOf(15), insumoExistente.getStock());
        verify(insumosDao).save(insumoExistente);
        verify(movimientoDao).save(any());
    }

    // === CASO 5: Egreso mayor al stock disponible (log de advertencia) ===
    @Test
    void agregarMovimiento_egresoMayorQueStock() {
        // Arrange
        Insumo insumoExistente = new Insumo();
        insumoExistente.setCodigo("A005");
        insumoExistente.setStock(BigDecimal.valueOf(3));

        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A005");
        request.setTipo("egreso");
        request.setStock(BigDecimal.valueOf(10));

        when(insumosDao.findByCodigo("A005")).thenReturn(Optional.of(insumoExistente));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertTrue(movimiento.getImpactado());
        assertEquals(BigDecimal.valueOf(-7), insumoExistente.getStock());
        verify(insumosDao).save(insumoExistente);
    }

    // === CASO 6: Error al guardar insumo o movimiento ===
    @Test
    void agregarMovimiento_errorAlGuardar() {
        // Arrange
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A006");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.ONE);
        request.setNombre("Test");
        request.setCategoria("Cat");
        request.setMarca("Marca");
        request.setUnidad("U");
        request.setLote("L1");

        when(insumosDao.findByCodigo("A006")).thenReturn(Optional.empty());
        when(insumosDao.save(any(Insumo.class))).thenThrow(new RuntimeException("Falla DB"));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        // Assert
        assertFalse(movimiento.getImpactado());
        verify(insumosDao).save(any());
        verify(movimientoDao).save(any());
    }
}
