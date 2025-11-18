package com.java.tp_principal_backend.dto;

import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.Producto;

import lombok.Data;

@Data
public class ProductosResponse {
	private ProductoDTO producto;
	private Empleados empleado;
	
	public ProductosResponse(Producto producto, Empleados empleado) {
		this.producto = new ProductoDTO(producto);
		this.empleado = empleado;
	}
}
