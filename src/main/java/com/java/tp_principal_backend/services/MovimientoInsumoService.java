package com.java.tp_principal_backend.services;

import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.model.MovimientoInsumo;

public interface MovimientoInsumoService {
    MovimientoInsumo agregarMovimiento(MovimientoInsumoRequest request);
}
