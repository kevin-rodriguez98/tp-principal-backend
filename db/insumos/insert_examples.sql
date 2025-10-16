USE fl_insumos;

-- ids hardcodeadas
INSERT INTO fabricante (id, nombre) VALUES
    (1, 'La Serenísima'),
    (2, 'Sancor'),
    (3, 'Ledesma'),
    (4, 'Alicante'),
    (5, 'Milkaut'),
    (6, 'Molinos'),
    (7, 'Yogurísimo'),
    (8, 'La Paulina'),
    (9, 'Tregar'),
    (10, 'Ilolay')
;

-- Bebidas Lácteas
-- Quesos
-- Postres
-- Crema
-- Manteca
-- Helado
-- Congelados
INSERT INTO categoria (id, nombre) VALUES
    (1, 'Lácteos'),
    (2, 'Saborizantes'),
    (3, 'Grasas'),
    (4, 'Esencias'),
    (5, 'Endulzantes'),
    (6, 'Cereales')
;

INSERT INTO unidad (id, nombre, abreviacion) VALUES
    (1, 'Kilo', 'kg'),
    (2, 'Gramo', 'gr'),
    (3, 'Litro', 'l'),
    (4, 'Mililitro', 'ml')
;

INSERT INTO insumo (id, codigo, nombre, umbral_minimo_stock,id_categoria,id_unidad) VALUES
    (1, 'I001', 'Leche', 100, 1, 3),
    (2, 'I002', 'Azúcar', 50, 5, 1),
    (3, 'I003', 'Esencia de vainilla', 30, 4, 3),
    (4, 'I004', 'Manteca', 200, 3, 1),
    (5, 'I005', 'Harina', 100, 6, 1)
;

INSERT INTO prod_insumo (id, codigo, id_insumo, id_fabricante, cantidad_aportada) VALUES
    (1, 'PI001', 1, 1, 0.5),
    (2, 'PI002', 2, 3, 1),
    (3, 'PI003', 4, 2, 0.2),
    (4, 'PI004', 3, 4, 0.1),
    (5, 'PI005', 1, 5, 1),
    (6, 'PI006', 5, 6, 1)
;

INSERT INTO orden_insumo (id, codigo, id_insumo, cantidad) VALUES
    (1, 'O001', 1, 500),
    (2, 'O002', 2, 300),
    (3, 'O003', 4, 150)
;