package com.java.tp_principal_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequest {

	private String nombre;
	private String apellido;
	private String legajo;
	private String area;
	private String rol;
}
