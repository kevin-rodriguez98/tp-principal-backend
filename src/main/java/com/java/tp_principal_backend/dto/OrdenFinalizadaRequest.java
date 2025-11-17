package com.java.tp_principal_backend.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrdenFinalizadaRequest {
	private Integer ordenId;
	private BigDecimal stockProducidoReal;
	private String destino;
	private String legajo;
}
