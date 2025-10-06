package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.InsumoRequest;
import com.java.tp_principal_backend.model.Insumo;

import java.util.List;
import java.util.Map;

public interface InsumosServices {

    Insumo agregarInsumo(InsumoRequest request);

    List<Insumo> obtenerTodosLosInsumos();

    Insumo editarInsumo(String codigo, Map<String, Object> cambios);

    void eliminarInsumo(String codigo);
}
