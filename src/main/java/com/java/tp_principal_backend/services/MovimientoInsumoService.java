package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.MoviemientoInsumoResponse;
import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.model.MovimientoInsumo;

import java.util.List;

public interface MovimientoInsumoService {
    MovimientoInsumo agregarMovimiento(MovimientoInsumoRequest request);
    List<MoviemientoInsumoResponse> obtenerTodosLosMovimientos();
    List<MoviemientoInsumoResponse> obtenerTodosLosIngresos();
}
