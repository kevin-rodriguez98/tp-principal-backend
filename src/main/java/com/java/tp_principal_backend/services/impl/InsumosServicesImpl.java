package com.java.tp_principal_backend.services.impl;

import com.java.tp_principal_backend.data.InsumosDao;
import com.java.tp_principal_backend.dto.InsumoRequest;
import com.java.tp_principal_backend.model.Insumo;
import com.java.tp_principal_backend.services.InsumosServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InsumosServicesImpl implements InsumosServices {

    @Autowired
    private InsumosDao insumosDao;

    @Override
    public Insumo agregarInsumo(InsumoRequest request) {
        if (insumosDao.findByCodigo(request.getCodigo()).isPresent()) {
            log.info("El insumo con código {} ya se encuentra dado de alta", request.getCodigo());
            throw new RuntimeException("Error al agregar insumo");
        }

        Insumo insumo = new Insumo();
        insumo.setCodigo(request.getCodigo());
        insumo.setNombre(request.getNombre());
        insumo.setCategoria(request.getCategoria());
        insumo.setMarca(request.getMarca());
        insumo.setUnidad(request.getUnidad());
        insumo.setStock(request.getStock());
        insumo.setLote(request.getLote());
        if (request.getUmbralMinimoStock() != null) {
            insumo.setUmbralMinimoStock(request.getUmbralMinimoStock());
        }
        return insumosDao.save(insumo);
    }

    @Override
    public List<Insumo> obtenerTodosLosInsumos() {
        return insumosDao.findAll();
    }

    @Override
    public Insumo editarInsumo(String codigo, Map<String, Object> cambios) {
        Insumo insumo = insumosDao.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        cambios.forEach((campo, valor) -> {
            if (valor != null) {
                switch (campo) {
                    case "nombre":
                        insumo.setNombre((String) valor);
                        break;
                    case "categoria":
                        insumo.setCategoria((String) valor);
                        break;
                    case "marca":
                        insumo.setMarca((String) valor);
                        break;
                    case "unidad":
                        insumo.setUnidad((String) valor);
                        break;
                    case "stock":
                        insumo.setStock(new java.math.BigDecimal(valor.toString()));
                        break;
                    case "lote":
                        insumo.setLote((String) valor);
                        break;
                    case "umbralMinimoStock":
                        insumo.setUmbralMinimoStock(Integer.parseInt(valor.toString()));
                        break;
                }
            }
        });

        return insumosDao.save(insumo);
    }

    @Override
    @Transactional
    public void eliminarInsumo(String codigo) {
        if (!insumosDao.findByCodigo(codigo).isPresent()) {
            throw new RuntimeException("Insumo no encontrado");
        }
        insumosDao.deleteByCodigo(codigo);
    }
}
