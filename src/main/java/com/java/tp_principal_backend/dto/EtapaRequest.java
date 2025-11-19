package com.java.tp_principal_backend.dto;

import lombok.Data;

@Data
public class EtapaRequest {
	private int idOrden;
	private String responsable;
	private String estado;
	private Boolean isEstado;

}
