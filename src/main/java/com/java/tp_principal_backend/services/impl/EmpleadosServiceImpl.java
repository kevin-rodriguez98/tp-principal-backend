package com.java.tp_principal_backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.services.EmpleadoService;

@Service
public class EmpleadosServiceImpl implements EmpleadoService{
	
	@Autowired
	private EmpleadosDao empleadoDao;

	@Override
	public Empleados agregarEmpleado(EmpleadoRequest empleado) {
		Empleados empleadoNuevo = new Empleados();
		empleadoNuevo.setNombre(empleado.getNombre());
		empleadoNuevo.setApellido(empleado.getApellido());
		empleadoNuevo.setLegajo(empleado.getLegajo());
		empleadoNuevo.setArea(empleado.getArea());
		empleadoNuevo.setRol(empleado.getRol());
		return empleadoDao.save(empleadoNuevo);
	}

	@Override
	public void eliminarEmpleado(String legajo) {
		Empleados empleado = empleadoDao.buscarPorLegajo(legajo);
		empleadoDao.delete(empleado);
	}

	@Override
	public List<Empleados> obtenerEmpleados() {
		return empleadoDao.findAll();
	}
	
}
