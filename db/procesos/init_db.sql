DROP DATABASE IF EXISTS fl_procesos;
CREATE DATABASE fl_procesos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE fl_procesos;

-- producto que vende por la pyme
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(250),
    id_unidad INT NOT NULL,
    cantidad_aportada DECIMAL(10,2) NOT NULL CHECK (cantidad_aportada > 0),
    alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (codigo),
    FOREIGN KEY (id_unidad) REFERENCES fl_insumos.unidad(id)
);

CREATE TABLE producto_ingrediente (
    id_producto INT NOT NULL,
    id_insumo INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL CHECK (cantidad > 0),
    FOREIGN KEY (id_producto) REFERENCES producto(id),
    FOREIGN KEY (id_insumo) REFERENCES fl_insumos.insumo(id),
    PRIMARY KEY (id_producto, id_insumo)
);

-- TODO
-- etapa de elaboracion

CREATE TABLE orden_produccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    id_producto INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL CHECK (cantidad > 0),
    estado ENUM('pendiente aprobación','pendiente', 'en producción', 'terminado'),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP,
    fecha_inicio TIMESTAMP
    fecha_cierre TIMESTAMP,
    UNIQUE (codigo),
    FOREIGN KEY (id_producto) REFERENCES producto(id)
);