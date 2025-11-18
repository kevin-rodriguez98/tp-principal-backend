package com.java.tp_principal_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.java.tp_principal_backend.model.Producto;

import lombok.Data;

@Data
public class ProductoDTO {
	private Integer id;
    private String codigo;
    private String nombre;
    private String categoria;
    private String linea;
    private String unidad;
    private BigDecimal stock;
    private String lote;
    private String creationUsername;
    private String fechaCreacion;
    private String presentacion;
    private String empleados;
    
	public ProductoDTO(Producto p) {
		this.id = p.getId();
		this.codigo = p.getCodigo();
		this.nombre = p.getNombre();
		this.categoria = p.getCategoria();
		this.linea = p.getLinea();
		this.unidad = p.getUnidad();
		this.stock = p.getStock();
		this.lote = p.getLote();
		this.creationUsername = p.getCreationUsername();
		this.fechaCreacion = formatearFecha(p.getFechaCreacion());
		this.presentacion = p.getPresentacion();
		this.empleados = p.getEmpleados();
	}
    
	private String  formatearFecha(LocalDateTime fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return fecha.format(formatter);
	}
    
    
    
    
}



