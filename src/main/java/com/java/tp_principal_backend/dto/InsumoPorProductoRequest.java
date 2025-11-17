package com.java.tp_principal_backend.dto;

import lombok.Data;

@Data
public class InsumoPorProductoRequest {
    private String codigoProducto;
    private InsumoRecetaDTO insumo;
}
