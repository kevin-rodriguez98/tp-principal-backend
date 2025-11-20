package com.java.tp_principal_backend.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleados {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(nullable = false)
	private String legajo;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(name = "apellido", nullable = false)
	private String apellido;
	
	@Column(nullable = true)
	private String area;
	
	@Column(nullable = true)
	private String rol;
	
	@Column(nullable = true)
	private String password;
	
	@Column(name="isprimeringreso", nullable = true)
	private Boolean isPrimerIngreso;

}
