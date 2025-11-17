package com.java.tp_principal_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	    public ResponseEntity<Empleados> agregarEmpleado(@RequestBody EmpleadoRequest request) {
		 try {
			 Empleados empleado = empleadosService.agregarEmpleado(request);
		     return ResponseEntity.ok(empleado);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
		 	
	    }
	 
	 @GetMapping("/otener-empleados")
	 public ResponseEntity<List<Empleados>> obtenerEmpleados(){
		 try {
			 List<Empleados> empleados = empleadosService.obtenerEmpleados();
		     return ResponseEntity.ok(empleados);
		 } catch (Exception e) {
			 return ResponseEntity.noContent().build();
		 }
	 }
	 
	 @PostMapping("/eliminar-empleado/{legajo}")
	 public ResponseEntity<String> eliminarEmpleado(@PathVariable String legajo){
		 try {
			 empleadosService.eliminarEmpleado(legajo);
			 return ResponseEntity.ok("Empleado eliminado legajo: " + legajo);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Hubo un error al intentar eliminar empleado de legajo: "+ legajo);
		} 
	 }
	

}
