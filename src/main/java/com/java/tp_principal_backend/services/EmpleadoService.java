package com.java.tp_principal_backend.services;

import java.util.List;

import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.model.Empleados;

public interface EmpleadoService {

	Empleados agregarEmpleado(EmpleadoRequest empleado);
	List<Empleados> obtenerEmpleados();
	void eliminarEmpleado(String legajo);
}
