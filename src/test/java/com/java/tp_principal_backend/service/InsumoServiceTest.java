package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.dto.InsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.services.impl.InsumosServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

public class InsumoServiceTest {
    @Mock
    private InsumosDao insumosDao;

    @InjectMocks
    private InsumosServicesImpl insumosServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void agregarInsumo_exito() {
        // Arrange
        InsumoRequest request = new InsumoRequest();
        request.setCodigo("A001");
        request.setNombre("Harina");
        request.setCategoria("Alimentos");
        request.setMarca("ACME");
        request.setUnidad("kg");
        request.setStock(BigDecimal.TEN);
        request.setLote("L123");
        request.setUmbralMinimoStock(5);

        when(insumosDao.findByCodigo("A001")).thenReturn(Optional.empty());

        Insumo insumoGuardado = new Insumo();
        insumoGuardado.setCodigo("A001");
        when(insumosDao.save(any(Insumo.class))).thenReturn(insumoGuardado);

        // Act
        Insumo resultado = insumosServices.agregarInsumo(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("A001", resultado.getCodigo());
        verify(insumosDao, times(1)).save(any(Insumo.class));
    }

    @Test
    void agregarInsumo_codigoExistente_lanzaExcepcion() {
        // Arrange
        InsumoRequest request = new InsumoRequest();
        request.setCodigo("A001");

        when(insumosDao.findByCodigo("A001")).thenReturn(Optional.of(new Insumo()));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> insumosServices.agregarInsumo(request));
        verify(insumosDao, never()).save(any());
    }

    @Test
    void editarInsumo_modificaCamposCorrectos() {
        // Arrange
        String codigo = "A001";
        Insumo existente = new Insumo();
        existente.setCodigo(codigo);
        existente.setNombre("Viejo");
        existente.setCategoria("Cat");
        existente.setMarca("MarcaVieja");
        existente.setUnidad("u");
        existente.setStock(BigDecimal.ONE);
        existente.setLote("L1");
        existente.setUmbralMinimoStock(1);

        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", "Nuevo nombre");
        cambios.put("stock", 50);

        when(insumosDao.findByCodigo(codigo)).thenReturn(Optional.of(existente));
        when(insumosDao.save(any(Insumo.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Insumo actualizado = insumosServices.editarInsumo(codigo, cambios);

        // Assert
        assertEquals("Nuevo nombre", actualizado.getNombre());
        assertEquals(new BigDecimal("50"), actualizado.getStock());
        verify(insumosDao).save(existente);
    }

    @Test
    void eliminarInsumo_exito() {
        // Arrange
        String codigo = "A001";
        when(insumosDao.findByCodigo(codigo)).thenReturn(Optional.of(new Insumo()));

        // Act
        insumosServices.eliminarInsumo(codigo);

        // Assert
        verify(insumosDao).deleteByCodigo(codigo);
    }

    @Test
    void eliminarInsumo_noExiste_lanzaExcepcion() {
        // Arrange
        String codigo = "A001";
        when(insumosDao.findByCodigo(codigo)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> insumosServices.eliminarInsumo(codigo));
        verify(insumosDao, never()).deleteByCodigo(any());
    }
}
