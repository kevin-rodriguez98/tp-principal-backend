package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.LocacionDao;
import com.java.tp_principal_backend.dto.InsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.Locacion;
import com.java.tp_principal_backend.services.impl.InsumosServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InsumoServiceTest {
	
	@InjectMocks
    private InsumosServicesImpl service;

    @Mock
    private InsumosDao insumosDao;

    @Mock
    private LocacionDao locacionDao;

    private InsumoRequest request;
    private Insumo insumo;
    private Locacion locacion;

    @BeforeEach
    void setup() {
        locacion = new Locacion();
        locacion.setId(1);
        locacion.setSector("A1");

        request = new InsumoRequest();
        request.setCodigo("I001");
        request.setNombre("Leche");
        request.setCategoria("Materia Prima");
        request.setMarca("Sancor");
        request.setUnidad("L");
        request.setUmbralMinimoStock(5);
        request.setLocacion(locacion);

        insumo = new Insumo();
        insumo.setCodigo("I001");
        insumo.setNombre("Leche");
        insumo.setCategoria("Materia Prima");
        insumo.setMarca("Sancor");
        insumo.setUnidad("L");
        insumo.setLocacion(locacion);
    }

    @Test
    void agregarInsumo_ok() {
        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.empty());
        when(locacionDao.save(locacion)).thenReturn(locacion);
        when(insumosDao.save(any(Insumo.class))).thenReturn(insumo);

        Insumo result = service.agregarInsumo(request);

        assertEquals("I001", result.getCodigo());
        verify(insumosDao, times(1)).save(any());
    }

    @Test
    void agregarInsumo_existente() {
        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));

        assertThrows(RuntimeException.class, () -> service.agregarInsumo(request));
    }

    @Test
    void obtenerTodosLosInsumos() {
        when(insumosDao.findAll()).thenReturn(List.of(insumo));

        List<Insumo> result = service.obtenerTodosLosInsumos();

        assertEquals(1, result.size());
    }

    @Test
    void editarInsumo_ok() {
        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));
        when(insumosDao.save(any())).thenReturn(insumo);

        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", "Leche Entera");
        cambios.put("stock", "50");
        cambios.put("umbralMinimoStock", 10);

        Insumo result = service.editarInsumo("I001", cambios);

        assertEquals("Leche Entera", result.getNombre());
        assertEquals(new BigDecimal("50"), result.getStock());
        assertEquals(10, result.getUmbralMinimoStock());
    }

    @Test
    void editarInsumo_noExiste() {
        when(insumosDao.findByCodigo("I404")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.editarInsumo("I404", Map.of()));
    }

    @Test
    void eliminarInsumo_ok() {
        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));

        service.eliminarInsumo("I001");

        verify(insumosDao, times(1)).deleteByCodigo("I001");
    }

    @Test
    void eliminarInsumo_noExiste() {
        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.eliminarInsumo("I001"));
    }

    @Test
    void obtenerTodosLosInsumosConBajoStock() {
        when(insumosDao.findInsumoBajoStock()).thenReturn(List.of(insumo));

        List<Insumo> result = service.obtenerTodosLosInsumosConBajoStock();

        assertEquals(1, result.size());
    }

    /*@Mock
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
    }*/
}
