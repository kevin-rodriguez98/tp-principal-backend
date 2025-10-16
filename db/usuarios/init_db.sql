DROP DATABASE IF EXISTS fl_usuarios;
CREATE DATABASE fl_usuarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE fl_usuarios;

CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    nivel_privilegio INT NOT NULL
);

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    legajo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    telefono VARCHAR(50),
    fecha_nacimiento DATE,
    fecha_entrada DATE NOT NULL, -- a la fabrica
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id)
);