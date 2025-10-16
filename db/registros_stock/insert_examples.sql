USE fl_registros_stock;

INSERT INTO proveedor (id, nombre) VALUES
    (1, 'Proveedor 1'),
    (2, 'Proveedor 2'),
    (3, 'Proveedor 3'),
    (4, 'Proveedor 4'),
    (5, 'Proveedor 5')
;

INSERT INTO ingreso_insumo (id, id_prod_insumo, id_proveedor, lote,cantidad_inicial) VALUES
    (1, 1, 3, 'L001', 40),
    (2, 2, 2, 'L002', 50),
    (3, 3, 1, 'L003', 100),
    (4, 4, 5, 'L004', 50),
    (5, 1, 1, 'L005', 70),
    (6, 5, 4, 'L006', 20),
    (7, 6, 2, 'L007', 100)
;