package com.java.tp_principal_backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.dto.EmpleadoResponse;
import com.java.tp_principal_backend.dto.LoginRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.services.EmpleadoService;

@Service
public class EmpleadosServiceImpl implements EmpleadoService{
	
	@Autowired
	private EmpleadosDao empleadoDao;

	@Override
	public EmpleadoResponse agregarEmpleado(EmpleadoRequest empleado) {
		Empleados empleadoNuevo = new Empleados();
		empleadoNuevo.setNombre(empleado.getNombre().toUpperCase());
		empleadoNuevo.setApellido(empleado.getApellido().toUpperCase());
		empleadoNuevo.setLegajo(empleado.getLegajo());
		empleadoNuevo.setArea(empleado.getArea().toUpperCase());
		empleadoNuevo.setRol(empleado.getRol().toUpperCase());
		empleadoNuevo.setPassword(empleado.getLegajo());
		empleadoNuevo.setIsPrimerIngreso(true);
		return new EmpleadoResponse(empleadoDao.save(empleadoNuevo));
	}

	@Override
	public void eliminarEmpleado(String legajo) {
		Empleados empleado = empleadoDao.buscarPorLegajo(legajo);
		empleadoDao.delete(empleado);
	}

	@Override
	public List<EmpleadoResponse> obtenerEmpleados() {
		List<Empleados> empleados =  empleadoDao.findAll();
		empleados.stream().map(e -> new EmpleadoResponse(e)).toList();
		return empleados.stream().map(e -> new EmpleadoResponse(e)).toList();
	}

	@Override
	public EmpleadoResponse login(LoginRequest loginRequest) {
		Empleados empleado = empleadoDao.buscarPorLegajo(loginRequest.getLegajo());
		if(empleado.getPassword().equals(loginRequest.getPassword())) {
			return new EmpleadoResponse(empleado);
		}else
			throw new RuntimeException("Empelado no encontrado");
	}

	@Override
	public EmpleadoResponse obtenerEmpleado(String legajo) {
		return new EmpleadoResponse(empleadoDao.buscarPorLegajo(legajo));
	}

	@Override
	public EmpleadoResponse modificarEmpleado(EmpleadoRequest empleadoReuqest) {
		Empleados empleado = empleadoDao.buscarPorLegajo(empleadoReuqest.getLegajo());
		empleado.setApellido(empleadoReuqest.getApellido());
		empleado.setNombre(empleadoReuqest.getNombre());
		empleado.setArea(empleadoReuqest.getArea());
		empleado.setRol(empleadoReuqest.getRol());
		empleadoDao.save(empleado);
		return new EmpleadoResponse(empleado);
	}

	@Override
	public EmpleadoResponse modificarPasword(LoginRequest loginRequest) {
		Empleados empleado = empleadoDao.buscarPorLegajo(loginRequest.getLegajo());
		if(empleado.getIsPrimerIngreso().equals(Boolean.TRUE))
			empleado.setIsPrimerIngreso(Boolean.FALSE);
		empleado.setPassword(loginRequest.getPassword());
		empleadoDao.save(empleado);
		return new EmpleadoResponse(empleado);
	}
}
