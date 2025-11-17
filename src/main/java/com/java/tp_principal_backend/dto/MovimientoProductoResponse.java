package com.java.tp_principal_backend.dto;

import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.MovimientoProducto;

import lombok.Data;

@Data
public class MovimientoProductoResponse {
	private MovimientoProducto movimientos;
	private Empleados empleado;
	
	public MovimientoProductoResponse(MovimientoProducto movimientos,Empleados empleado){
		this.movimientos = movimientos;
		this.empleado = empleado;
	}
}
