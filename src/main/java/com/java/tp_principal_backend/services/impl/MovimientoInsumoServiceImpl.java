package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.data.MovimientoInsumoDao;
import com.java.tp_principal_backend.dto.MovimientoInsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.model.MovimientoInsumo;
import com.java.tp_principal_backend.services.MovimientoInsumoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@Slf4j
public class MovimientoInsumoServiceImpl implements MovimientoInsumoService {

    @Autowired
    private InsumosDao insumosDao;

    @Autowired
    private MovimientoInsumoDao movimientoDao;

    private String randomUsername() {
        String[] names = {"Ana", "Luis", "Juan", "María", "Carlos", "Selena", "Kevin", "Juliana", "Matias"};
        return names[new Random().nextInt(names.length)];
    }

    @Override
    @Transactional
    public MovimientoInsumo agregarMovimiento(MovimientoInsumoRequest request) {
        boolean impactado = false;

        try {
            Insumo insumo = insumosDao.findByCodigo(request.getCodigo()).orElse(null);

            // Si no existe el insumo y faltan campos, loggear
            if (insumo == null) {
                if (request.getNombre() == null || request.getCategoria() == null ||
                        request.getMarca() == null || request.getUnidad() == null || request.getLote() == null) {
                    System.out.println("Faltan datos para crear el insumo con código " + request.getCodigo());
                    impactado = false;
                } else {
                    insumo = new Insumo();
                    insumo.setCodigo(request.getCodigo());
                    insumo.setNombre(request.getNombre());
                    insumo.setCategoria(request.getCategoria());
                    insumo.setMarca(request.getMarca());
                    insumo.setUnidad(request.getUnidad());
                    insumo.setLote(request.getLote());
                    insumo.setStock(request.getStock());
                    insumo.setUmbralMinimoStock(0);
                    insumosDao.save(insumo);
                    impactado = true;
                }
            } else {
                // Actualizar stock existente
                BigDecimal nuevoStock = insumo.getStock();
                if ("ingreso".equalsIgnoreCase(request.getTipo())) {
                    nuevoStock = nuevoStock.add(request.getStock());
                } else if ("egreso".equalsIgnoreCase(request.getTipo())) {
                    nuevoStock = nuevoStock.subtract(request.getStock());

                    if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                        // Loguear que se egresó más que el disponible
                        System.out.println("¡Atención! Se egresó más stock del disponible para el código: "
                                + request.getCodigo() + ". Stock actual: " + insumo.getStock()
                                + ", egreso: " + request.getStock());
                    }
                }
                insumo.setStock(nuevoStock);
                insumosDao.save(insumo);
                impactado = true;
            }
        } catch (Exception e) {
            log.info("No se pudo dar de alta o actualizar el insumo: " + e.getMessage());
            impactado = false;
        }

        MovimientoInsumo movimiento = new MovimientoInsumo();
        try {
            movimiento.setCodigo(request.getCodigo());
            movimiento.setTipo(request.getTipo());
            movimiento.setStock(request.getStock());
            movimiento.setImpactado(impactado);
            movimiento.setCreationUsername(randomUsername());
            // Setear otros campos si existen
            movimiento.setNombre(request.getNombre());
            movimiento.setCategoria(request.getCategoria());
            movimiento.setMarca(request.getMarca());
            movimiento.setUnidad(request.getUnidad());
            movimiento.setLote(request.getLote());

            movimientoDao.save(movimiento);
        } catch (Exception e) {
            log.info("No se pudo guardar el movimiento: " + e.getMessage());
            movimiento.setImpactado(false);
        }

        return movimiento;
    }
}
