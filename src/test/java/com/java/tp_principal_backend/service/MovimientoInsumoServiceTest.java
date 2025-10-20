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

    // === CASO 1: Crear insumo inexistente (ingreso correcto) ===
    @Test
    void agregarMovimiento_creaNuevoInsumoYMovimiento() {
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A001");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.TEN);
        request.setNombre("Harina");
        request.setCategoria("Alimentos");
        request.setMarca("ACME");
        request.setUnidad("kg");
        request.setLote("L123");
        request.setProveedor("Proveedor X");

        when(insumosDao.findByCodigo("A001")).thenReturn(Optional.empty());
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        assertTrue(movimiento.getImpactado());
        assertEquals("A001", movimiento.getCodigo());
        verify(insumosDao).save(any(Insumo.class));
        verify(movimientoDao).save(any(MovimientoInsumo.class));
    }

    // === CASO 2: Faltan datos para crear un nuevo insumo ===
    @Test
    void agregarMovimiento_faltanDatosInsumoNoCreado() {
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A002");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.ONE);
        // Falta proveedor, nombre, etc.

        when(insumosDao.findByCodigo("A002")).thenReturn(Optional.empty());
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        assertFalse(movimiento.getImpactado());
        verify(insumosDao, never()).save(any());
        verify(movimientoDao).save(any());
    }

    // === CASO 3: Ingreso sobre insumo existente ===
    @Test
    void agregarMovimiento_actualizaStockPorIngreso() {
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

        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        assertTrue(movimiento.getImpactado());
        assertEquals(BigDecimal.valueOf(15), insumoExistente.getStock());
        verify(insumosDao).save(insumoExistente);
        verify(movimientoDao).save(any());
    }

    // === CASO 6: Error al guardar movimiento (excepción capturada) ===
    @Test
    void agregarMovimiento_errorAlGuardarMovimiento() {
        MovimientoInsumoRequest request = new MovimientoInsumoRequest();
        request.setCodigo("A006");
        request.setTipo("ingreso");
        request.setStock(BigDecimal.ONE);
        request.setNombre("Test");
        request.setCategoria("Cat");
        request.setMarca("Marca");
        request.setUnidad("U");
        request.setLote("L1");
        request.setProveedor("Prov");

        when(insumosDao.findByCodigo("A006")).thenReturn(Optional.empty());
        when(insumosDao.save(any(Insumo.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoDao.save(any(MovimientoInsumo.class))).thenThrow(new RuntimeException("Error DB"));

        MovimientoInsumo movimiento = movimientoService.agregarMovimiento(request);

        assertFalse(movimiento.getImpactado());
        verify(insumosDao).save(any());
        verify(movimientoDao).save(any());
    }
}
