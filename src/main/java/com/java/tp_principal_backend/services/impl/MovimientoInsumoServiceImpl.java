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
import java.util.List;
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
        MovimientoInsumo movimiento;

        try {
            // Solo ingreso
            if (!"ingreso".equalsIgnoreCase(request.getTipo())) {
                throw new IllegalArgumentException("Solo se permite tipo 'ingreso'.");
            }
            movimiento = procesarIngreso(request);
        } catch (Exception e) {
            log.error("Error al procesar el movimiento: {}", e.getMessage());
            movimiento = crearMovimientoBase(request, false);
        }

        try {
            movimientoDao.save(movimiento);
        } catch (Exception e) {
            log.error("No se pudo guardar el movimiento: {}", e.getMessage());
            movimiento.setImpactado(false);
        }

        return movimiento;
    }

    private MovimientoInsumo procesarIngreso(MovimientoInsumoRequest request) {
        Insumo insumo = insumosDao.findByCodigo(request.getCodigo()).orElse(null);
        boolean impactado = false;

        if (insumo == null) {
            // Validar proveedor
            if (request.getProveedor() == null || request.getProveedor().isBlank()) {
                throw new IllegalArgumentException("El campo 'proveedor' no puede estar vacío para crear un nuevo insumo.");
            }

            // Validar datos mínimos
            if (request.getNombre() == null || request.getCategoria() == null ||
                    request.getMarca() == null || request.getUnidad() == null || request.getLote() == null) {
                throw new IllegalArgumentException("Faltan datos para crear el insumo con código " + request.getCodigo());
            }

            insumo = new Insumo();
            insumo.setCodigo(request.getCodigo());
            insumo.setNombre(request.getNombre());
            insumo.setCategoria(request.getCategoria());
            insumo.setMarca(request.getMarca());
            insumo.setUnidad(request.getUnidad());
            insumo.setLote(request.getLote());
            insumo.setProveedor(request.getProveedor());
            insumo.setStock(request.getStock());
            insumo.setUmbralMinimoStock(0);
            insumosDao.save(insumo);
            impactado = true;
        } else {
            // El insumo ya existe → sumamos stock
            BigDecimal nuevoStock = insumo.getStock().add(request.getStock());
            insumo.setStock(nuevoStock);
            insumosDao.save(insumo);
            impactado = true;
        }

        return crearMovimientoBase(request, impactado);
    }

    private MovimientoInsumo crearMovimientoBase(MovimientoInsumoRequest request, boolean impactado) {
        MovimientoInsumo movimiento = new MovimientoInsumo();
        movimiento.setCodigo(request.getCodigo());
        movimiento.setTipo(request.getTipo());
        movimiento.setStock(request.getStock());
        movimiento.setImpactado(impactado);
        movimiento.setCreationUsername(randomUsername());
        movimiento.setNombre(request.getNombre());
        movimiento.setCategoria(request.getCategoria());
        movimiento.setMarca(request.getMarca());
        movimiento.setUnidad(request.getUnidad());
        movimiento.setLote(request.getLote());
        movimiento.setProveedor(request.getProveedor());
        return movimiento;
    }

    @Override
    public List<MovimientoInsumo> obtenerTodosLosMovimientos() {
        return movimientoDao.findAll();
    }

    @Override
    public List<MovimientoInsumo> obtenerTodosLosIngresos() {
        return movimientoDao.findByTipoIgnoreCase("ingreso");
    }
}
