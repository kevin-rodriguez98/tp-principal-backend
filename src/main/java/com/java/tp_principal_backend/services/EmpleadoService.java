package com.java.tp_principal_backend.services;

import java.util.List;

import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.dto.EmpleadoResponse;
import com.java.tp_principal_backend.dto.LoginRequest;

public interface EmpleadoService {

	EmpleadoResponse agregarEmpleado(EmpleadoRequest empleado);
	List<EmpleadoResponse> obtenerEmpleados();
	void eliminarEmpleado(String legajo);
	EmpleadoResponse login(LoginRequest loginRequest);
	EmpleadoResponse obtenerEmpleado(String legajo);
	EmpleadoResponse modificarEmpleado(EmpleadoRequest empleado);
	EmpleadoResponse modificarPasword(LoginRequest loginRequest);
}
