DROP DATABASE IF EXISTS fl_insumos;
CREATE DATABASE fl_insumos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fl_insumos;

-- serenisima, sancor, etc
CREATE TABLE fabricante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- lacteos, saborizantes, etc
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- kg, gr, ml, l, etc
CREATE TABLE unidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    abreviacion VARCHAR(10) NOT NULL
);

-- leche, manteca, azucar, etc
CREATE TABLE insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    umbral_minimo_stock DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (umbral_minimo_stock >= 0),
    stock_actual DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    id_categoria INT NOT NULL,
    id_unidad INT NOT NULL,
    alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario_dador_alta INT,
    UNIQUE (codigo),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id),
    FOREIGN KEY (id_unidad) REFERENCES unidad(id),
    FOREIGN KEY (id_usuario_dador_alta) REFERENCES fl_usuarios.usuario(id)
);

-- leche serenisima 1L, manteca Sancor 200gr, etc
CREATE TABLE prod_insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
--    nombre VARCHAR(50) NOT NULL,
    id_insumo INT NOT NULL,
    id_fabricante INT NOT NULL,
    cantidad_aportada DECIMAL(10,2) NOT NULL CHECK (cantidad_aportada > 0),
    stock_actual DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario_dador_alta INT,
    info_extra VARCHAR(250),
    UNIQUE (codigo),
    FOREIGN KEY (id_insumo) REFERENCES insumo(id),
    FOREIGN KEY (id_fabricante) REFERENCES fabricante(id),
    FOREIGN KEY (id_usuario_dador_alta) REFERENCES fl_usuarios.usuario(id)
);

-- ej pendiente comprar 100L de leche
CREATE TABLE orden_insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    id_insumo INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL CHECK (cantidad > 0),
    fecha_orden TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cumplimiento TIMESTAMP, -- fecha cuando se compra lo que pide la orden
    id_responsable INT,
    UNIQUE (codigo),
    FOREIGN KEY (id_insumo) REFERENCES insumo(id),
    FOREIGN KEY (id_responsable) REFERENCES fl_usuarios.usuario(id)
);