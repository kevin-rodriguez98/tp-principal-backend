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
import com.java.tp_principal_backend.dto.EmpleadoResponse;
import com.java.tp_principal_backend.dto.LoginRequest;
import com.java.tp_principal_backend.services.EmpleadoService;

@RestController
@RequestMapping("/empleados")
public class EmpleadosController {
	
	@Autowired
	private EmpleadoService empleadosService;
	
	 @PostMapping("/agregar-empleado")
	    public ResponseEntity<EmpleadoResponse> agregarEmpleado(@RequestBody EmpleadoRequest request) {
		 try {
		     return ResponseEntity.ok(empleadosService.agregarEmpleado(request));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
		 	
	    }
	 
	 @GetMapping("/obtener-empleados")
	 public ResponseEntity<List<EmpleadoResponse>> obtenerEmpleados(){
		 try {
		     return ResponseEntity.ok(empleadosService.obtenerEmpleados());
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
	 
	 @GetMapping("/login")
	 public ResponseEntity<EmpleadoResponse> login(@RequestBody LoginRequest loginRequest){
		 try {
			return ResponseEntity.ok(empleadosService.login(loginRequest));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	 }
	 
	 @GetMapping("/obtener-empleado/{legajo}")
	 public ResponseEntity<EmpleadoResponse> obtenerEmpleado(@PathVariable String legajo){
		 try {
			 return ResponseEntity.ok(empleadosService.obtenerEmpleado(legajo));
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
		 
	 }
	 
	 @PostMapping("/modificar-empleado")
	 public ResponseEntity<EmpleadoResponse> modificarEmpleado(@RequestBody EmpleadoRequest request){
		 try {
			return ResponseEntity.ok(empleadosService.modificarEmpleado(request));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	 }
	 
	 @PostMapping("/modificar-password")
	 public ResponseEntity<EmpleadoResponse> modificarPassword(@RequestBody LoginRequest loginRequest){
		 try {
			 return ResponseEntity.ok(empleadosService.modificarPasword(loginRequest));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	 }
	

}
