package com.java.tp_principal_backend.dto;

import com.java.tp_principal_backend.model.Empleados;

import lombok.Data;

@Data
public class HistorialEtapasResponse {
	private String etapa;
	private String fechaCambio;
	private Empleados empleado;
}
