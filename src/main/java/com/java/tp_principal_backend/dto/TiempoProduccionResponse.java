package com.java.tp_principal_backend.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TiempoProduccionResponse {
	private BigDecimal tiempoPreparacion;
    private BigDecimal tiempoCiclo;
    private BigDecimal tiempoTotal;
    private BigDecimal cantidadMaximaTanda;
}
