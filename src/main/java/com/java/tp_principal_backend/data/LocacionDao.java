package com.java.tp_principal_backend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.tp_principal_backend.model.Locacion;

@Repository
public interface LocacionDao extends JpaRepository<Locacion, Long> {

}
