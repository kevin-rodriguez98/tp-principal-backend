package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.OrdenProduccionDao;
import com.java.tp_principal_backend.data.ProductosDao;
import com.java.tp_principal_backend.data.TiempoProduccionDao;
import com.java.tp_principal_backend.dto.TiempoProduccionRequest;
import com.java.tp_principal_backend.dto.TiempoProduccionResponse;
import com.java.tp_principal_backend.model.OrdenProduccion;
import com.java.tp_principal_backend.model.Producto;
import com.java.tp_principal_backend.model.TiempoProduccion;
import com.java.tp_principal_backend.services.TiempoProduccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TiempoProduccionServiceImpl implements TiempoProduccionService {

    @Autowired
    private TiempoProduccionDao tiempoDao;

    @Autowired
    private ProductosDao productosDao;

    @Autowired
    private OrdenProduccionDao ordenDao;

    @Override
    @Transactional
    public TiempoProduccion agregar(TiempoProduccionRequest tiempoRequest) {
    	TiempoProduccion tiempo = tiempoDao.findByProductoCodigo(tiempoRequest.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no tiene tiempo de producción definido"));
    	Producto producto = productosDao.findByCodigo(tiempoRequest.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    	tiempo.setProducto(producto);
    	tiempo.setTiempoCiclo(tiempoRequest.getTiempoCiclo());
    	tiempo.setTiempoPreparacion(tiempoRequest.getTiempoPreparacion());
    	tiempo.setCantidaTanda(tiempoRequest.getMaximoTanda());
    	return tiempoDao.save(tiempo);
    }

    @Override
    public List<TiempoProduccion> obtenerTodos() {
        return tiempoDao.findAll();
    }

    @Override
    public BigDecimal calcularTiempoTotal(String codigoProducto, BigDecimal cantidad) {
        TiempoProduccion tiempo = tiempoDao.findByProductoCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no tiene tiempo de producción definido"));
        
        BigDecimal tandas;;
        if(cantidad.compareTo(tiempo.getCantidaTanda()) == -1) {
        	tandas = BigDecimal.ONE;
        }else
        	tandas = cantidad.divide(tiempo.getCantidaTanda());
      
        BigDecimal tiempoProduccionTanda = tiempo.getCantidaTanda().multiply(tiempo.getTiempoCiclo());
        BigDecimal tiempoCalculado =  tiempo.getTiempoPreparacion().add(tandas.multiply(tiempoProduccionTanda));
        return tiempoCalculado.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public TiempoProduccionResponse obtenerTiempoPorProducto(String codigoProducto) {
    	TiempoProduccion tiempo = tiempoDao.findByProductoCodigo(codigoProducto).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    	TiempoProduccionResponse response = new TiempoProduccionResponse();
    	response.setCantidadMaximaTanda(tiempo.getCantidaTanda());
    	response.setTiempoCiclo(tiempo.getTiempoCiclo());
    	response.setTiempoPreparacion(tiempo.getTiempoPreparacion());
    	response.setTiempoTotal(tiempo.getTiempoCiclo().add(tiempo.getTiempoPreparacion()));
    	return response;
    }

	@Override
	public BigDecimal obtenertiemposProduccion(String codigoProducto) {
		/* OrdenProduccion orden = ordenDao.findByCodigoProducto(codigoProducto)
	                .orElseThrow(() -> new EntityNotFoundException(
	                        "No se encontró la orden asociada al producto: " + codigoProducto));

	        BigDecimal stockOrden = orden.getStockRequerido();

	        String unidad = orden.getPresentacion();
	        if (unidad == null) unidad = "Kilos";

	        // Conversión a KG
	        if (unidad.equalsIgnoreCase("Gramos")) {
	            stockOrden = stockOrden.divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
	        }

	        // Capacidad por tanda
	        BigDecimal capacidad = BigDecimal.valueOf(500);

	        // Cantidad de tandas
	        BigDecimal multiplyFor = stockOrden.divide(capacidad, 0, RoundingMode.CEILING);

	        // Tiempo base total
	        Integer tiempoBase = tiempoDao.sumAllTiempos();
	        if (tiempoBase == null) tiempoBase = 0;

	        return BigDecimal.valueOf(tiempoBase)
	                .multiply(multiplyFor);*/
		return BigDecimal.ZERO;
	}
    

}
