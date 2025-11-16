package com.java.tp_principal_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.services.EmpleadoService;

@RestController
@RequestMapping("/empleados")
public class EmpleadosController {
	
	@Autowired
	private EmpleadoService empleadosService;
	
	 @PostMapping("/agregar-empleado")
	    public ResponseEntity<Empleados> agregarReceta(@RequestBody EmpleadoRequest request) {
		 	Empleados empleado = empleadosService.agregarEmpleado(request);
	        return ResponseEntity.ok(empleado);
	    }
	
	

}
