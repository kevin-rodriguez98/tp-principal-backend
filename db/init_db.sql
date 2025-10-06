DROP DATABASE IF EXISTS frozen_lacteos;
CREATE DATABASE frozen_lacteos;
USE frozen_lacteos;

CREATE TABLE insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    unidad VARCHAR(10) NOT NULL,
    stock DECIMAL(10,2) DEFAULT 0 CHECK (stock >= 0),
    lote VARCHAR(20) NOT NULL,
    umbral_minimo_stock INT DEFAULT 0 CHECK (umbral_minimo_stock >= 0),
    UNIQUE (codigo)
);

INSERT INTO insumo (codigo, nombre, categoria, marca, unidad, stock, lote) VALUES
('101', 'Leche', 'Lácteos', 'La Serenísima', 'Litro', 100, 'L001'),
('102', 'Leche', 'Lácteos', 'Sancor', 'Litro', 80, 'L002'),
('103', 'Queso', 'Lácteos', 'Milkaut', 'Kilo', 50, 'L003'),
('104', 'Harina', 'Alimentos', 'Molinos', 'Kilo', 30, 'L004')
;