package com.java.tp_principal_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.tp_principal_backend.data.EmpleadosDao;
import com.java.tp_principal_backend.dto.EmpleadoRequest;
import com.java.tp_principal_backend.dto.EmpleadoResponse;
import com.java.tp_principal_backend.dto.LoginRequest;
import com.java.tp_principal_backend.model.Empleados;
import com.java.tp_principal_backend.services.impl.EmpleadosServiceImpl;

@ExtendWith(MockitoExtension.class)
class EmpleadosServiceImplTest {

    @InjectMocks
    private EmpleadosServiceImpl service;

    @Mock
    private EmpleadosDao empleadoDao;

    private Empleados empleado;
    private EmpleadoRequest request;

    @BeforeEach
    void setup() {
        empleado = new Empleados();
        empleado.setLegajo("100");
        empleado.setNombre("juan");
        empleado.setApellido("perez");
        empleado.setArea("produccion");
        empleado.setRol("operario");
        empleado.setPassword("100");

        request = new EmpleadoRequest();
        request.setLegajo("100");
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setArea("Produccion");
        request.setRol("Operario");
    }

    @Test
    void agregarEmpleado_ok() {
        when(empleadoDao.save(any())).thenReturn(empleado);

        EmpleadoResponse result = service.agregarEmpleado(request);

        assertEquals("juan", result.getNombre());
        assertEquals("perez", result.getApellido());
        assertEquals("100", result.getLegajo());
        verify(empleadoDao).save(any());
    }

    @Test
    void eliminarEmpleado_ok() {
        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);

        service.eliminarEmpleado("100");

        verify(empleadoDao, times(1)).delete(empleado);
    }

    @Test
    void obtenerEmpleados_ok() {
        when(empleadoDao.findAll()).thenReturn(List.of(empleado));

        List<EmpleadoResponse> result = service.obtenerEmpleados();

        assertEquals(1, result.size());
        assertEquals("juan", result.get(0).getNombre());
    }

    @Test
    void login_correcto() {
        LoginRequest login = new LoginRequest();
        login.setLegajo("100");
        login.setPassword("100");

        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);

        EmpleadoResponse result = service.login(login);

        assertEquals("juan", result.getNombre());
    }

    @Test
    void login_incorrecto() {
        LoginRequest login = new LoginRequest();
        login.setLegajo("100");
        login.setPassword("xxx");

        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);

        assertThrows(RuntimeException.class, () -> service.login(login));
    }

    @Test
    void obtenerEmpleado_ok() {
        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);

        EmpleadoResponse result = service.obtenerEmpleado("100");

        assertEquals("juan", result.getNombre());
    }

    @Test
    void modificarEmpleado_ok() {
        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);
        when(empleadoDao.save(any())).thenReturn(empleado);

        EmpleadoRequest req = new EmpleadoRequest();
        req.setLegajo("100");
        req.setNombre("Nuevo");
        req.setApellido("Apellido");
        req.setArea("Calidad");
        req.setRol("Supervisor");

        EmpleadoResponse result = service.modificarEmpleado(req);

        assertEquals("Nuevo", result.getNombre());
        assertEquals("Apellido", result.getApellido());
        assertEquals("Calidad", result.getArea());
        assertEquals("Supervisor", result.getRol());
    }

    @Test
    void modificarPassword_ok() {
        when(empleadoDao.buscarPorLegajo("100")).thenReturn(empleado);
        when(empleadoDao.save(any())).thenReturn(empleado);

        LoginRequest req = new LoginRequest();
        req.setLegajo("100");
        req.setPassword("nueva");

        EmpleadoResponse result = service.modificarPasword(req);
        assertEquals("100", result.getLegajo());
        verify(empleadoDao).save(empleado);
    }
}
