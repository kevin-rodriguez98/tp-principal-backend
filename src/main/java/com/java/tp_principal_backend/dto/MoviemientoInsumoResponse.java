package com.java.tp_principal_backend.dto;

import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.model.MovimientoInsumo;

import lombok.Data;

@Data
public class MoviemientoInsumoResponse {
	private MovimientoInsumo movimientos;
	private Empleados empleado;
	
	public MoviemientoInsumoResponse(MovimientoInsumo movimientos, Empleados empleado) {
		this.movimientos = movimientos;
		this.empleado = empleado;
	}
	
	
}
