DROP DATABASE IF EXISTS fl_registros_stock;
CREATE DATABASE fl_registros_stock
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE fl_registros_stock;

CREATE TABLE proveedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    -- TODO otros datos
);

CREATE TABLE ingreso_insumo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_prod_insumo INT NOT NULL,
    id_orden_insumo INT,
    id_proveedor INT NOT NULL,
    lote VARCHAR(50) NOT NULL,
    cantidad_inicial DECIMAL(10,2) NOT NULL CHECK (cantidad_inicial > 0),
    cantidad_restante DECIMAL(10,2) NOT NULL CHECK (cantidad_restante >= 0),
    UNIQUE (id_proveedor, id_prod_insumo, lote),
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id),
    FOREIGN KEY (id_orden_insumo) REFERENCES fl_insumos.orden_insumo(id),
    FOREIGN KEY (id_prod_insumo) REFERENCES fl_insumos.prod_insumo(id)
);

-- TODO CREATE TABLE egreso_producto ();

DELIMITER $$

-- cantidad_restante = cantidad_inicial en primera instancia
CREATE TRIGGER trg_ingreso_insumo_before_insert
BEFORE INSERT ON ingreso_insumo
FOR EACH ROW
BEGIN
    IF NEW.cantidad_restante IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se permite asignar manualmente cantidad_restante';
    END IF;
    SET NEW.cantidad_restante = NEW.cantidad_inicial;
END$$

-- actualizar stock_actual de insumo & prod_insumo
CREATE TRIGGER trg_ingreso_insumo_after_insert
AFTER INSERT ON ingreso_insumo
FOR EACH ROW
BEGIN
    DECLARE _id_insumo INT;
    DECLARE _cantidad_aportada DECIMAL(10,2);
    SELECT id_insumo,cantidad_aportada INTO _id_insumo,_cantidad_aportada FROM fl_insumos.prod_insumo WHERE id = NEW.id_prod_insumo;
    UPDATE fl_insumos.prod_insumo SET stock_actual = stock_actual + NEW.cantidad_inicial WHERE id = NEW.id_prod_insumo;
    UPDATE fl_insumos.insumo SET stock_actual = stock_actual + NEW.cantidad_inicial * _cantidad_aportada WHERE id = _id_insumo;
END$$

-- actualizar stock_actual de insumo & prod_insumo
CREATE TRIGGER trg_ingreso_insumo_after_update
AFTER UPDATE ON ingreso_insumo
FOR EACH ROW
BEGIN
    DECLARE _id_insumo INT;
    DECLARE _cantidad_aportada DECIMAL(10,2);
    DECLARE diferencia_stock DECIMAL(10,2);
    SET diferencia_stock = OLD.cantidad_restante - NEW.cantidad_restante;
    IF diferencia_stock < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se permite incrementar la cantidad de un ingreso ya registrado (registre un nuevo ingreso)';
    END IF;
    IF diferencia_stock > 0 THEN
        SELECT id_insumo,cantidad_aportada INTO _id_insumo,_cantidad_aportada FROM fl_insumos.prod_insumo WHERE id = NEW.id_prod_insumo;
        UPDATE fl_insumos.prod_insumo SET stock_actual = stock_actual - diferencia_stock WHERE id = NEW.id_prod_insumo;
        UPDATE fl_insumos.insumo SET stock_actual = stock_actual - diferencia_stock * _cantidad_aportada WHERE id = _id_insumo;
    END IF;
END$$

DELIMITER ;