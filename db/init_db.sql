DROP DATABASE IF EXISTS frozen_lacteos;
CREATE DATABASE frozen_lacteos;
USE frozen_lacteos;

--Crea tabla de insumos
CREATE TABLE insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    unidad VARCHAR(10) NOT NULL,
    stock DECIMAL(10,2) DEFAULT 0,
    lote VARCHAR(20) NOT NULL,
    umbral_minimo_stock INT DEFAULT 0 CHECK (umbral_minimo_stock >= 0),
    UNIQUE (codigo)
);

--Crea tabla de registro de stock
CREATE TABLE movimiento_insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50),
    categoria VARCHAR(50),
    marca VARCHAR(50),
    unidad VARCHAR(10),
    stock DECIMAL(10,2) DEFAULT 0,
    lote VARCHAR(20),
    umbral_minimo_stock INT DEFAULT 0,
    tipo VARCHAR(20), -- ingreso o egreso
    impactado BOOLEAN,
    creation_username VARCHAR(50)
);

-- Agregar columnas a la tabla insumo
ALTER TABLE insumo
ADD COLUMN proveedor VARCHAR(100) NULL,
ADD COLUMN destino VARCHAR(100) NULL;

-- Agregar columnas a la tabla movimiento_insumo
ALTER TABLE movimiento_insumo
ADD COLUMN proveedor VARCHAR(100) NULL,
ADD COLUMN destino VARCHAR(100) NULL;

-- Crea tabla de productos
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50),
    categoria VARCHAR(50),
    marca VARCHAR(50),
    unidad VARCHAR(10),
    stock DECIMAL(10,2) DEFAULT 0,
    lote VARCHAR(20),
    creation_username VARCHAR(50)
);

-- Elimina columnas de la tabla insumos
ALTER TABLE insumo
DROP COLUMN proveedor,
DROP COLUMN destino;

-- Tabla de insumos necesarios por producto
CREATE TABLE insumo_por_producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_insumo INT NOT NULL,
    stock_necesario_insumo DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES producto(id),
    FOREIGN KEY (id_insumo) REFERENCES insumo(id)
);

-- Como no se deben registar egresos de insumos se elimina campo destino
ALTER TABLE movimiento_insumo
DROP COLUMN destino;

-- Crea tabla de movimientos para productos
CREATE TABLE movimiento_producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_producto VARCHAR(50) NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- ingreso o egreso
    impactado BOOLEAN,
    creation_username VARCHAR(50),
    destino VARCHAR(100),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crea tabla para ordenes de producción
CREATE TABLE orden_produccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_requerido VARCHAR(100) NOT NULL, -- nombre del producto que se requiere
    marca VARCHAR(50) NOT NULL,              -- marca del producto
    stock_requerido DECIMAL(10,2) NOT NULL,
    codigo_producto VARCHAR(50),
    fecha_entrega DATE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'Evaluación',
    lote VARCHAR(50),                        -- se completará luego
    stock_producido_real DECIMAL(10,2) DEFAULT 0,
    creation_username VARCHAR(50),
    impactado BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla para tiempo necesario por producto
CREATE TABLE tiempo_produccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    tiempo_por_unidad DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES producto(id)
);