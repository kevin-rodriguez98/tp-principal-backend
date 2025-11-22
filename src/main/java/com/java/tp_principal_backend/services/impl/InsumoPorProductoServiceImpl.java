package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.InsumoPorProductoDao;
import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.dto.InsumoNecesarioResponse;
import com.java.tp_principal_backend.dto.InsumoPorProductoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.InsumoPorProducto;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.services.InsumoPorProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsumoPorProductoServiceImpl implements InsumoPorProductoService {

    @Autowired
    private InsumoPorProductoDao recetaDao;

    @Autowired
    private ProductosDao productosDao;

    @Autowired
    private InsumosDao insumosDao;

    @Override
    public InsumoPorProducto agregarReceta(InsumoPorProductoRequest request) {
        Producto producto = productosDao.findByCodigo(request.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Insumo insumo = insumosDao.findByCodigo(request.getInsumo().getCodigoInsumo())
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado"));

        InsumoPorProducto receta = new InsumoPorProducto();
        receta.setProducto(producto);
        receta.setInsumo(insumo);
        receta.setStockNecesarioInsumo(request.getInsumo().getCantidadNecesaria());
        receta.setUnidad(insumo.getUnidad());

        return recetaDao.save(receta);
    }

    @Override
    public List<InsumoNecesarioResponse> calcularInsumosNecesarios(String codigoProducto, BigDecimal cantidadProducto) {
        Producto producto = productosDao.findByCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        List<InsumoPorProducto> recetas = recetaDao.findByProductoId(producto.getId());
        return recetas.stream()
                .map(r -> new InsumoNecesarioResponse(
                        r.getInsumo().getCodigo(),
                        r.getInsumo().getNombre(),
                        r.getStockNecesarioInsumo(),
                        r.getUnidad()
                ))
                .collect(Collectors.toList());
    }

	@Override
	public void eliminarInsumo(String codigoProducto, String codigoInsumo) {
		Insumo insumo = insumosDao.findByCodigo(codigoInsumo).orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado"));
		Producto producto = productosDao.findByCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
		
		InsumoPorProducto insumoReceta = recetaDao.findFirstByInsumoIdAndProductoId(insumo.getId(),producto.getId());
		
		recetaDao.delete(insumoReceta);
	}
}
