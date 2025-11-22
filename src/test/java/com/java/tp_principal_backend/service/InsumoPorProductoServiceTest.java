package com.java.tp_principal_backend.service;

import com.java.tp_principal_backend.dto.InsumoNecesarioResponse;
import com.java.tp_principal_backend.dto.InsumoPorProductoRequest;
import com.java.tp_principal_backend.dto.InsumoRecetaDTO;
import com.java.tp_principal_backend.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InsumoPorProductoServiceTest {

	 @InjectMocks
	    private InsumoPorProductoServiceImpl service;

	    @Mock
	    private InsumoPorProductoDao recetaDao;

	    @Mock
	    private ProductosDao productosDao;

	    @Mock
	    private InsumosDao insumosDao;

	    private Producto producto;
	    private Insumo insumo;
	    private InsumoPorProducto receta;

	    @BeforeEach
	    void setup() {
	        producto = new Producto();
	        producto.setId(1);
	        producto.setCodigo("P001");
	        producto.setNombre("Dulce de leche");

	        insumo = new Insumo();
	        insumo.setId(10);
	        insumo.setCodigo("I001");
	        insumo.setNombre("Leche");
	        insumo.setUnidad("L");

	        receta = new InsumoPorProducto();
	        receta.setId(100);
	        receta.setProducto(producto);
	        receta.setInsumo(insumo);
	        receta.setStockNecesarioInsumo(BigDecimal.valueOf(2));
	        receta.setUnidad("L");
	    }

	    @Test
	    void agregarReceta_ok() {
	    	InsumoRecetaDTO insumoReq = new InsumoRecetaDTO();
	        insumoReq.setCodigoInsumo("I001");
	        insumoReq.setCantidadNecesaria(BigDecimal.valueOf(3));

	        InsumoPorProductoRequest req = new InsumoPorProductoRequest();
	        req.setCodigoProducto("P001");
	        req.setInsumo(insumoReq);

	        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
	        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));
	        when(recetaDao.save(any(InsumoPorProducto.class))).thenReturn(receta);

	        InsumoPorProducto result = service.agregarReceta(req);

	        assertEquals(producto, result.getProducto());
	        assertEquals(insumo, result.getInsumo());
	        assertEquals(BigDecimal.valueOf(2), result.getStockNecesarioInsumo());
	        verify(recetaDao).save(any(InsumoPorProducto.class));
	    }

	    @Test
	    void agregarReceta_productoNoEncontrado() {
	    	InsumoRecetaDTO insumoReq = new InsumoRecetaDTO();
	        insumoReq.setCodigoInsumo("I001");
	        insumoReq.setCantidadNecesaria(BigDecimal.ONE);

	        InsumoPorProductoRequest req = new InsumoPorProductoRequest();
	        req.setCodigoProducto("PX");
	        req.setInsumo(insumoReq);

	        when(productosDao.findByCodigo("PX")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () -> service.agregarReceta(req));
	        verify(insumosDao, never()).findByCodigo(anyString());
	        verify(recetaDao, never()).save(any());
	    }

	    @Test
	    void agregarReceta_insumoNoEncontrado() {
	    	InsumoRecetaDTO insumoReq = new InsumoRecetaDTO();
	        insumoReq.setCodigoInsumo("IX");
	        insumoReq.setCantidadNecesaria(BigDecimal.ONE);

	        InsumoPorProductoRequest req = new InsumoPorProductoRequest();
	        req.setCodigoProducto("P001");
	        req.setInsumo(insumoReq);

	        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
	        when(insumosDao.findByCodigo("IX")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () -> service.agregarReceta(req));
	        verify(recetaDao, never()).save(any());
	    }

	    @Test
	    void calcularInsumosNecesarios_ok() {
	        producto.setId(1);
	        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
	        when(recetaDao.findByProductoId(1)).thenReturn(List.of(receta));

	        List<InsumoNecesarioResponse> result =
	                service.calcularInsumosNecesarios("P001", BigDecimal.TEN);

	        assertEquals(1, result.size());
	        InsumoNecesarioResponse r = result.get(0);
	        assertEquals("I001", r.getCodigoInsumo());
	        assertEquals("Leche", r.getNombreInsumo());
	        assertEquals(receta.getStockNecesarioInsumo(), r.getCantidadNecesaria());
	        assertEquals("L", r.getUnidad());
	    }

	    @Test
	    void calcularInsumosNecesarios_productoNoEncontrado() {
	        when(productosDao.findByCodigo("PX")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class,
	                () -> service.calcularInsumosNecesarios("PX", BigDecimal.ONE));
	        verify(recetaDao, never()).findByProductoId(any());
	    }

	    @Test
	    void eliminarInsumo_ok() {
	        producto.setId(1);
	        insumo.setId(10);

	        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));
	        when(productosDao.findByCodigo("P001")).thenReturn(Optional.of(producto));
	        when(recetaDao.findFirstByInsumoIdAndProductoId(10, 1)).thenReturn(receta);

	        service.eliminarInsumo("P001", "I001");

	        verify(recetaDao, times(1)).delete(receta);
	    }

	    @Test
	    void eliminarInsumo_insumoNoEncontrado() {
	        when(insumosDao.findByCodigo("IX")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class,
	                () -> service.eliminarInsumo("P001", "IX"));

	        verify(productosDao, never()).findByCodigo(anyString());
	        verify(recetaDao, never()).delete(any());
	    }

	    @Test
	    void eliminarInsumo_productoNoEncontrado() {
	        when(insumosDao.findByCodigo("I001")).thenReturn(Optional.of(insumo));
	        when(productosDao.findByCodigo("PX")).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class,
	                () -> service.eliminarInsumo("PX", "I001"));

	        verify(recetaDao, never()).delete(any());
	    }
}
