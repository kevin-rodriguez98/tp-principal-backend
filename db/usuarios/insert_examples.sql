USE fl_usuarios;
-- ids hardcodeadas
INSERT INTO rol (id, nombre, nivel_privilegio) VALUES
    (1, 'Operario'      , 1),
    (2, 'Supervisor'    , 3),
    (3, 'Administrador' , 5)
;

INSERT INTO usuario (legajo, nombre, apellido, email,telefono, fecha_entrada, id_rol) VALUES
    ('1239043', 'Juan', 'Gómez', 'test1@gmail.com', '34234', '2023-06-23', 1),
    ('1239044', 'Lucía', 'López', 'test2@gmail.com', '334234', '2020-02-20', 2),
    ('1239045', 'Franco', 'Fernandez', 'test3@gmail.com', '12334234', '2025-04-10', 1),
    ('1111123', 'Luis', 'test', 'asdasd@yahoo.com.ar', '12901239', '2025-07-01', 3)
;