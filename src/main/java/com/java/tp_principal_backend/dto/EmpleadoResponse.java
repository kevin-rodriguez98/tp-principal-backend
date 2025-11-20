package com.java.tp_principal_backend.dto;

import com.java.tp_principal_backend.model.Empleados;

import lombok.Data;

@Data
public class EmpleadoResponse {
	private String nombre;
	private String apellido;
	private String legajo;
	private String area;
	private String rol;
	private Boolean isPrimerIngreso;
	
	
	public EmpleadoResponse(Empleados empleado) {
		this.nombre = empleado.getNombre();
		this.apellido = empleado.getApellido();
		this.legajo = empleado.getLegajo();
		this.area = empleado.getArea();
		this.rol = empleado.getRol();
		this.isPrimerIngreso = empleado.getIsPrimerIngreso();
	}
	
	
}
