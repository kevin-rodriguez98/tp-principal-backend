package com.java.tp_principal_backend.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InsumoRecetaDTO {
    private String codigoInsumo;
    private BigDecimal stockNecesarioInsumo;
    private String unidad;
    private String nombreInusmo;
}
