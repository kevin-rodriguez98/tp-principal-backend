package com.java.tp_principal_backend.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TiempoProduccionResponse {
	private BigDecimal tiempoProduccion;
	private String unidad;
	private Integer cantidad;
}
