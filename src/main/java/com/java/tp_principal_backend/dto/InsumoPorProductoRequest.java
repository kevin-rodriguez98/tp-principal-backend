package com.java.tp_principal_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.java.tp_principal_backend.model.InsumoPorProducto;

@Data
public class InsumoPorProductoRequest {
    private String codigoProducto;
    private InsumoRecetaDTO insumo;
}
