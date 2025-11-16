package com.java.tp_principal_backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.java.tp_principal_backend.model.Empleados;

public interface EmpleadosDao extends JpaRepository<Empleados, Integer>{
	
	@Query("SELECT e FROM Empleados e WHERE e.legajo = :legajo")
    Empleados buscarPorLegajo(String legajo);
	
}

